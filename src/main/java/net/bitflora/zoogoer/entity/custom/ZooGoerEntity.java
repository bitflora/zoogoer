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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

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
    private Map<ResourceLocation, Integer> speciesCount = new HashMap<>();
    private Set<UUID> countedEntities = new HashSet<>();

    double score = 0.0;

    public void setOrigin(BlockPos origin) {
        this.origin = origin;
        this.goalSelector.addGoal(2, new ReturnAndDepositGoal(this, this.origin, 1.0));
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
        UUID entityId = entity.getUUID();

        if (!entity.getType().is(ModTags.Entities.ZOO_GOER_IGNORED_SPECIES)) {
            // Only count if we haven't seen this specific entity before
            if (!this.countedEntities.contains(entityId)) {
                this.countedEntities.add(entityId);

                int currentCount = this.speciesCount.getOrDefault(entityType, 0);
                this.speciesCount.put(entityType, currentCount + 1);

                // Calculate value with diminishing returns
                var specialistValue = getSpecialistValue(entity);
                var baseValue = EntityValuesManager.BASE_VALUES.getEntityValue(entity);
                var baseModifier = getBaseModifier();
                double fullValue = specialistValue.orElse(baseValue.orElse(1.0) * baseModifier);

                // Apply diminishing returns: each additional mob of same species is worth half as much
                double diminishedValue = fullValue * Math.pow(0.5, currentCount);

                score += diminishedValue;
                LOGGER.info("Saw {} (count: {}) which is worth specialist {}, base {}, full value {}, diminished value {}",
                           entityType, currentCount + 1, specialistValue, baseValue, fullValue, diminishedValue);
                LOGGER.info(" Score is now {}", score);
            }
        } else {
            LOGGER.info("BORING! {}", entityType);
        }
    }

    public void debugNoticedMobs() {
        int totalMobs = this.speciesCount.values().stream().mapToInt(Integer::intValue).sum();
        LOGGER.info("{} counted {} mobs across {} unique species nearby",
                   this.getName().getString(), totalMobs, this.speciesCount.size());
        for (var entry : this.speciesCount.entrySet()) {
            LOGGER.info("- {}: {} seen", entry.getKey(), entry.getValue());
        }
    }

    public int calculatePrimaryDonation() {
        int limit = (int) Math.floor(this.score);
        if(limit > 0) {
            Random rng = new Random();
            return rng.nextInt(limit);
        } else {
            return 0;
        }
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

        // Save species counts
        CompoundTag speciesTag = new CompoundTag();
        for (Map.Entry<ResourceLocation, Integer> entry : this.speciesCount.entrySet()) {
            speciesTag.putInt(entry.getKey().toString(), entry.getValue());
        }
        compound.put("SpeciesCount", speciesTag);

        // Save counted entities
        long[] entityIds = this.countedEntities.stream()
            .mapToLong(uuid -> uuid.getMostSignificantBits())
            .toArray();
        long[] entityIds2 = this.countedEntities.stream()
            .mapToLong(uuid -> uuid.getLeastSignificantBits())
            .toArray();
        compound.putLongArray("CountedEntitiesMSB", entityIds);
        compound.putLongArray("CountedEntitiesLSB", entityIds2);
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

        // Load species counts
        if (compound.contains("SpeciesCount")) {
            CompoundTag speciesTag = compound.getCompound("SpeciesCount");
            this.speciesCount.clear();
            for (String key : speciesTag.getAllKeys()) {
                int count = speciesTag.getInt(key);
                this.speciesCount.put(new ResourceLocation(key), count);
            }
        }

        // Load counted entities
        if (compound.contains("CountedEntitiesMSB") && compound.contains("CountedEntitiesLSB")) {
            long[] entityIdsMSB = compound.getLongArray("CountedEntitiesMSB");
            long[] entityIdsLSB = compound.getLongArray("CountedEntitiesLSB");
            this.countedEntities.clear();
            for (int i = 0; i < Math.min(entityIdsMSB.length, entityIdsLSB.length); i++) {
                this.countedEntities.add(new UUID(entityIdsMSB[i], entityIdsLSB[i]));
            }
        }

        // Handle legacy data from old format
        if (compound.contains("DetectedSpecies") && this.speciesCount.isEmpty()) {
            String speciesString = compound.getString("DetectedSpecies");
            if (!speciesString.isEmpty()) {
                String[] speciesArray = speciesString.split(";");
                for (String species : speciesArray) {
                    this.speciesCount.put(new ResourceLocation(species), 1);
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
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // this.goalSelector.addGoal(1, new RhinoAttackGoal(this, 1.0D, true));

        // this.goalSelector.addGoal(1, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));

        this.goalSelector.addGoal(3, new SpeciesCounterGoal(this, LOOK_RANGE, 300));
        this.goalSelector.addGoal(4, new AnimalAIWanderRanged(this, 100, 1.0, 25, 25));
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