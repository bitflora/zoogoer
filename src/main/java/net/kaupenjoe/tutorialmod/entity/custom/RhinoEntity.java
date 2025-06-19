package net.kaupenjoe.tutorialmod.entity.custom;

import net.kaupenjoe.tutorialmod.entity.ModEntities;
import net.kaupenjoe.tutorialmod.entity.ai.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
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
import org.jetbrains.annotations.Nullable;

public class RhinoEntity extends PathfinderMob { // Not extending AbstractVillager

    public RhinoEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ARMOR_TOUGHNESS, 0.1f)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5f)
                .add(Attributes.ATTACK_DAMAGE, 2f);
    }

    @Override
    protected void registerGoals() {
        // Only add the AI goals you actually want
        // this.goalSelector.addGoal(0, new FloatGoal(this));

        // this.goalSelector.addGoal(1, new RhinoAttackGoal(this, 1.0D, true));

        // this.goalSelector.addGoal(1, new RandomStrollGoal(this, 1.0));
        var destination = new BlockPos(this.getBlockX() + 10, this.getBlockY(), this.getBlockZ() + 10);
        // this.goalSelector.addGoal(1, new MoveToGoal(this, 1.0, destination));
        this.goalSelector.addGoal(1, new WalkForwardGoal(this, 50));
        //this.goalSelector.addGoal(1, new WalkForwardGoal(this, 20));

        // this.goalSelector.addGoal(1, new BreedGoal(this, 1.15D));
        // this.goalSelector.addGoal(2, new TemptGoal(this, 1.2D, Ingredient.of(Items.COOKED_BEEF), false));

        // this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.1D));

        // this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.1D));
        // this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 3f));
        // this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        // this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    // Then use VillagerModel in your renderer without inheriting villager behaviors
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
