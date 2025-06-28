package net.kaupenjoe.tutorialmod.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReturnAndDepositGoal extends Goal {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReturnAndDepositGoal.class);

    private final PathfinderMob mob;
    private final BlockPos targetBlock;
    private final double speedModifier;
    private int depositCooldown = 0;
    private boolean hasDeposited = false;

    public ReturnAndDepositGoal(PathfinderMob mob, BlockPos targetBlock, double speedModifier) {
        this.mob = mob;
        this.targetBlock = targetBlock;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        Level level = this.mob.level();

        // Check if it's nighttime (time between 13000 and 23000)
        long dayTime = level.getDayTime() % 24000;
        boolean isNight = dayTime >= 13000 && dayTime <= 23000;

        // Only activate during night and if we haven't deposited yet today
        if (!isNight) {
            this.hasDeposited = false; // Reset for next night
            return false;
        }
        LOGGER.info("Hark! It is night!");

        // Don't activate if we already deposited tonight
        if (this.hasDeposited) {
            return false;
        }

        // Check if target block still exists
        boolean targetExists = level.getBlockState(targetBlock).getBlock() != net.minecraft.world.level.block.Blocks.AIR;
        LOGGER.info("targetExists: {}", targetExists);
        return targetExists;
    }

    @Override
    public boolean canContinueToUse() {
        Level level = this.mob.level();
        long dayTime = level.getDayTime() % 24000;
        boolean isNight = dayTime >= 13000 && dayTime <= 23000;

        return isNight && !this.hasDeposited && this.depositCooldown <= 0;
    }

    @Override
    public void start() {
        this.depositCooldown = 0;
    }

    @Override
    public void tick() {
        if (this.depositCooldown > 0) {
            this.depositCooldown--;
            return;
        }

        double distanceToTarget = this.mob.distanceToSqr(
            targetBlock.getX() + 0.5,
            targetBlock.getY(),
            targetBlock.getZ() + 0.5
        );

        // If we're close enough to the target block, try to deposit
        if (distanceToTarget <= 4.0) { // Within 2 block radius
            this.tryDeposit();
        } else {
            // Move towards the target block
            PathNavigation navigation = this.mob.getNavigation();
            if (navigation.isDone()) {
                navigation.moveTo(
                    targetBlock.getX() + 0.5,
                    targetBlock.getY(),
                    targetBlock.getZ() + 0.5,
                    this.speedModifier
                );
            }
        }
    }

    private void tryDeposit() {
        Level level = this.mob.level();
        BlockEntity blockEntity = level.getBlockEntity(targetBlock);

        if (blockEntity != null) {
            // Try to get the item handler capability using the new 1.20.1 system
            blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
                ItemStack emeralds = new ItemStack(Items.EMERALD, 5);

                // Try to insert the emeralds
                ItemStack remainder = insertItems(itemHandler, emeralds);

                if (remainder.getCount() < emeralds.getCount()) {
                    // Successfully deposited at least some emeralds
                    this.hasDeposited = true;
                    this.depositCooldown = 100; // 5 second cooldown

                    // Optional: Play a sound or particle effect
                    level.playSound(null, targetBlock,
                        net.minecraft.sounds.SoundEvents.VILLAGER_TRADE,
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        0.5F, 1.0F);


                    // Despawn the mob after successful deposit
                    this.mob.discard();
                }
            });
        }
    }

    private ItemStack insertItems(IItemHandler itemHandler, ItemStack stack) {
        ItemStack remaining = stack.copy();

        for (int slot = 0; slot < itemHandler.getSlots() && !remaining.isEmpty(); slot++) {
            remaining = itemHandler.insertItem(slot, remaining, false);
        }

        return remaining;
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
    }

    // Getter methods for external access
    public boolean hasDepositedTonight() {
        return this.hasDeposited;
    }

    public BlockPos getTargetBlock() {
        return this.targetBlock;
    }
}