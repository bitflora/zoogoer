package net.bitflora.zoogoer.entity.ai;

import net.bitflora.zoogoer.entity.custom.ZooGoerEntity;
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

    private final ZooGoerEntity mob;
    private final BlockPos targetBlock;
    private final double speedModifier;

    public ReturnAndDepositGoal(ZooGoerEntity mob, BlockPos targetBlock, double speedModifier) {
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
            return false;
        }
        LOGGER.info("Hark! It is night!");

        // Check if target block still exists
        // boolean targetExists = level.getBlockState(targetBlock).getBlock() != net.minecraft.world.level.block.Blocks.AIR;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        Level level = this.mob.level();
        long dayTime = level.getDayTime() % 24000;
        boolean isNight = dayTime >= 13000 && dayTime <= 23000;

        return isNight;
    }

    @Override
    public void start() {
    }

    @Override
    public void tick() {

        double distanceToTarget = this.mob.distanceToSqr(
            targetBlock.getX() + 0.5,
            targetBlock.getY(),
            targetBlock.getZ() + 0.5
        );

        // If we're close enough to the target block, try to deposit
        if (distanceToTarget <= 4.0) { // Within 2 block radius
            this.tryDeposit();

            // Despawn the mob, even if there is no longer somewhere to deposit
            this.mob.discard();
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
                ItemStack emeralds = new ItemStack(Items.EMERALD, this.mob.calculatePrimaryDonation());

                // Try to insert the emeralds
                ItemStack remainder = insertItems(itemHandler, emeralds);

                if (remainder.getCount() < emeralds.getCount()) {
                    level.playSound(null, targetBlock,
                        net.minecraft.sounds.SoundEvents.VILLAGER_TRADE,
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        0.5F, 1.0F);


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

    public BlockPos getTargetBlock() {
        return this.targetBlock;
    }
}