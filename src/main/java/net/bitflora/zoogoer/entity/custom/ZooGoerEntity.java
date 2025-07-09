package net.bitflora.zoogoer.entity.custom;

import net.bitflora.zoogoer.data.EntityValuesManager;
import net.bitflora.zoogoer.entity.ModEntities;
import net.bitflora.zoogoer.entity.ai.*;
import net.bitflora.zoogoer.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Should spawn from the zoo entrance block
 * Wanders around for a while
 * Counts up how many unique species it has seen
 * Comes back to the block (nightfall, or after a while)
 * At the block, deposits pay equal to RANDOM(num_species_seen), and despawns
 */
public class ZooGoerEntity extends AbstractVillager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVillager.class);
    private BlockPos origin;
    private Set<ResourceLocation> detectedSpecies = new HashSet<>();

    double score = 0.0;

    public void setOrigin(BlockPos origin) {
        this.origin = origin;
        this.goalSelector.addGoal(1, new ReturnAndDepositGoal(this, this.origin, 1.0));
    }

    @Nullable
    public BlockPos getOrigin() {
        return this.origin;
    }

    protected Optional<Double> getSpecialistValue(@Nonnull LivingEntity entity) {
        return Optional.empty();
    }

    protected double getBaseModifier() {
        return 1.0;
    }

    public void noticeMob(@Nonnull LivingEntity entity) {
        ResourceLocation entityType = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        //String entityTypeName = entityType.toString();

        if (!entity.getType().is(ModTags.Entities.ZOO_GOER_IGNORED_SPECIES)) {
            if (this.detectedSpecies.add(entityType)) {
                var specialistValue = getSpecialistValue(entity);
                var baseValue = EntityValuesManager.BASE_VALUES.getEntityValue(entity);
                var baseModifier = getBaseModifier();
                double value = specialistValue.orElse(baseValue.orElse(1.0) * baseModifier);
                score += value;
                LOGGER.info("Saw {} which is worth specialist {}, base {}, actual {}", entityType, specialistValue, baseValue, value);
                LOGGER.info(" Score is now {}", score);
            }
        } else {
            LOGGER.info("BORING! {}", entityType);
        }
    }

    public void debugNoticedMobs() {
        LOGGER.info("{} counted {} unique species nearby", this.getName().getString(), this.detectedSpecies.size());
        for (var species : this.detectedSpecies) {
            LOGGER.info("- {}", species);
        }
    }

    public int calculatePrimaryDonation() {
        int limit = (int) Math.floor(this.score);
        Random rng = new Random();
        return rng.nextInt(limit);
    }

    /**
     * Override to specify the path a loot table that will (sometimes) deposit into the domation box
     */
    public Optional<String> getTipLootTable() {
        return Optional.empty();
    }



    public ZooGoerEntity(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
        this.refreshDimensions();
    }

    // public ZooGoerEntity(@Nullable BlockPos origin, EntityType<? extends AbstractVillager> entityType, Level level) {
    //     super(entityType, level);
    //     this.origin = origin;
    //     this.refreshDimensions();
    // }

    // NBT Save/Load methods for persistence
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        // Save origin position
        if (this.origin != null) {
            compound.putInt("OriginX", this.origin.getX());
            compound.putInt("OriginY", this.origin.getY());
            compound.putInt("OriginZ", this.origin.getZ());
        }

        // Save score
        compound.putDouble("Score", this.score);

        // Save detected species as a single string with delimiter
        if (!this.detectedSpecies.isEmpty()) {
            String speciesString = String.join(";", this.detectedSpecies.stream()
                    .map(ResourceLocation::toString)
                    .toArray(String[]::new));
            compound.putString("DetectedSpecies", speciesString);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        // Load origin position
        if (compound.contains("OriginX") && compound.contains("OriginY") && compound.contains("OriginZ")) {
            int x = compound.getInt("OriginX");
            int y = compound.getInt("OriginY");
            int z = compound.getInt("OriginZ");
            var origin = new BlockPos(x, y, z);
            this.setOrigin(origin);
        }

        // Load score
        if (compound.contains("Score")) {
            this.score = compound.getDouble("Score");
        }

        // Load detected species
        if (compound.contains("DetectedSpecies")) {
            String speciesString = compound.getString("DetectedSpecies");
            this.detectedSpecies.clear();
            if (!speciesString.isEmpty()) {
                String[] speciesArray = speciesString.split(";");
                for (String species : speciesArray) {
                    this.detectedSpecies.add(new ResourceLocation(species));
                }
            }
        }
    }

    @Override
    public EntityDimensions getDimensions(@Nonnull Pose pose ) {
        // Villager dimensions: 0.6F width, 1.95F height
        return EntityDimensions.scalable(0.6F, 1.95F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10D)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ARMOR_TOUGHNESS, 0.1f)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5f)
                .add(Attributes.ATTACK_DAMAGE, 2f);
    }

    @Override
    protected void registerGoals() {
        final float LOOK_RANGE = 10.0F;
        int priority = 0;

        // Only add the AI goals you actually want
        // this.goalSelector.addGoal(0, new FloatGoal(this));

        // this.goalSelector.addGoal(1, new RhinoAttackGoal(this, 1.0D, true));

        // this.goalSelector.addGoal(1, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(0, new PanicGoal(this, 2.0));

        this.goalSelector.addGoal(2, new SpeciesCounterGoal(this, LOOK_RANGE, 300));
        this.goalSelector.addGoal(3, new AnimalAIWanderRanged(this, 100, 1.0, 25, 25));
        // this.goalSelector.addGoal(priority++, new LookAtPlayerGoal(this, PathfinderMob.class, 10.0F));
        // this.goalSelector.addGoal(priority++, new RandomLookAroundGoal(this));

        //this.goalSelector.addGoal(1, new WalkForwardGoal(this, 20));

        // this.goalSelector.addGoal(1, new BreedGoal(this, 1.15D));
        // this.goalSelector.addGoal(2, new TemptGoal(this, 1.2D, Ingredient.of(Items.COOKED_BEEF), false));

        // this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.1D));

        // this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.1D));
        // this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 3f));
        // this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        // this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    protected void rewardTradeXp(MerchantOffer pOffer) {
    }

    @Override
    protected void updateTrades() {
    }

    @Override
    @javax.annotation.Nullable
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }
}

// public class RhinoEntity extends AbstractVillager {
//     private static final EntityDataAccessor<Boolean> ATTACKING =
//             SynchedEntityData.defineId(RhinoEntity.class, EntityDataSerializers.BOOLEAN);

//     public RhinoEntity(EntityType<? extends AbstractVillager> pEntityType, Level pLevel) {
//         super(pEntityType, pLevel);
//         this.refreshDimensions();
//     }

//     public final AnimationState idleAnimationState = new AnimationState();
//     private int idleAnimationTimeout = 0;

//     public final AnimationState attackAnimationState = new AnimationState();
//     public int attackAnimationTimeout = 0;


//     @Override
//     public void tick() {
//         super.tick();

//         if(this.level().isClientSide()) {
//             setupAnimationStates();
//         }
//     }

//     private void setupAnimationStates() {
//         if(this.idleAnimationTimeout <= 0) {
//             this.idleAnimationTimeout = this.random.nextInt(40) + 80;
//             this.idleAnimationState.start(this.tickCount);
//         } else {
//             --this.idleAnimationTimeout;
//         }

//         if(this.isAttacking() && attackAnimationTimeout <= 0) {
//             attackAnimationTimeout = 80; // Length in ticks of your animation
//             attackAnimationState.start(this.tickCount);
//         } else {
//             --this.attackAnimationTimeout;
//         }

//         if(!this.isAttacking()) {
//             attackAnimationState.stop();
//         }
//     }

//     @Override
//     protected void updateWalkAnimation(float pPartialTick) {
//         float f;
//         if(this.getPose() == Pose.STANDING) {
//             f = Math.min(pPartialTick * 6F, 1f);
//         } else {
//             f = 0f;
//         }

//         this.walkAnimation.update(f, 0.2f);
//     }

//     public void setAttacking(boolean attacking) {
//         this.entityData.set(ATTACKING, attacking);
//     }

//     public boolean isAttacking() {
//         return this.entityData.get(ATTACKING);
//     }

//     @Override
//     protected void defineSynchedData() {
//         super.defineSynchedData();
//         this.entityData.define(ATTACKING, false);
//     }

//     @Override
//     protected void registerGoals() {
//         // this.goalSelector.addGoal(0, new FloatGoal(this));

//         // this.goalSelector.addGoal(1, new RhinoAttackGoal(this, 1.0D, true));

//         // this.goalSelector.addGoal(1, new RandomStrollGoal(this, 1.0));
//         var destination = new BlockPos(this.getBlockX() + 10, this.getBlockY(), this.getBlockZ() + 10);
//         // this.goalSelector.addGoal(1, new MoveToGoal(this, 1.0, destination));
//         this.goalSelector.addGoal(1, new WalkForwardGoal(this, 50));
//         //this.goalSelector.addGoal(1, new WalkForwardGoal(this, 20));

//         // this.goalSelector.addGoal(1, new BreedGoal(this, 1.15D));
//         // this.goalSelector.addGoal(2, new TemptGoal(this, 1.2D, Ingredient.of(Items.COOKED_BEEF), false));

//         // this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.1D));

//         // this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.1D));
//         // this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 3f));
//         // this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

//         // this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
//     }

//     public static AttributeSupplier.Builder createAttributes() {
//         return Animal.createLivingAttributes()
//                 .add(Attributes.MAX_HEALTH, 20D)
//                 .add(Attributes.FOLLOW_RANGE, 24D)
//                 .add(Attributes.MOVEMENT_SPEED, 0.25D)
//                 .add(Attributes.ARMOR_TOUGHNESS, 0.1f)
//                 .add(Attributes.ATTACK_KNOCKBACK, 0.5f)
//                 .add(Attributes.ATTACK_DAMAGE, 2f);
//     }

//     @Nullable
//     @Override
//     public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
//         return ModEntities.RHINO.get().create(pLevel);
//     }


//     // @Nullable
//     // @Override
//     // protected SoundEvent getAmbientSound() {
//     //     return SoundEvents.HOGLIN_AMBIENT;
//     // }

//     // @Nullable
//     // @Override
//     // protected SoundEvent getHurtSound(DamageSource pDamageSource) {
//     //     return SoundEvents.RAVAGER_HURT;
//     // }

//     // @Nullable
//     // @Override
//     // protected SoundEvent getDeathSound() {
//     //     return SoundEvents.DOLPHIN_DEATH;
//     // }

//     @Override
//     protected void rewardTradeXp(MerchantOffer pOffer) {
//         // TODO Auto-generated method stub
//     }

//     @Override
//     protected void updateTrades() {
//         // TODO Auto-generated method stub
//     }

//     @Override
//     public InteractionResult mobInteract(Player player, InteractionHand hand) {
//         // Customize what happens when player right-clicks
//         if (!this.level().isClientSide) {
//             return InteractionResult.SUCCESS;
//         }
//         return InteractionResult.sidedSuccess(this.level().isClientSide);
//     }

//     @Override
//     public boolean removeWhenFarAway(double distance) {
//         return false; // Prevent despawning like villagers
//     }

//     @Override
//     protected void pickUpItem(ItemEntity itemEntity) {
//         // Control what items the entity can pick up
//         // Override to prevent item pickup or customize behavior
//     }

//     @Override
//     public boolean isSleeping() {
//         return false; // Prevent sleeping behavior
//     }

//     @Override
//     protected void customServerAiStep() {
//         // Override villager's daily schedule logic
//         super.customServerAiStep();
//     }
// }
