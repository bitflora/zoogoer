package net.bitflora.zoogoer.entity.ai;

import net.bitflora.zoogoer.entity.custom.*;
import net.bitflora.zoogoer.sound.*;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SteveWanderGoal extends Goal {
    private static final Logger LOGGER = LoggerFactory.getLogger(SteveWanderGoal.class);
    private final SteveEntity mob;
    private final double searchRange;
    private final double moveSpeed;
    private final int retargetTime;
    private final int wanderRadius;

    private Mob targetMob;
    private BlockPos targetPos;
    private int retargetTicks;
    private int stuckTicks;

    public SteveWanderGoal(SteveEntity mob, double searchRange, double moveSpeed, int retargetTime, int wanderRadius) {
        this.mob = mob;
        this.searchRange = searchRange;
        this.moveSpeed = moveSpeed;
        this.retargetTime = retargetTime;
        this.wanderRadius = wanderRadius;
        this.retargetTicks = 0;
        this.stuckTicks = 0;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // Always try to use this goal
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        // Continue until it's time to retarget
        if (this.retargetTicks >= this.retargetTime) {
            return false;
        }

        // Stop if target mob is dead
        if (this.targetMob != null && !this.targetMob.isAlive()) {
            return false;
        }

        return true;
    }

    @Override
    public void start() {
        LOGGER.info("** start()");
        this.retargetTicks = 0;
        this.stuckTicks = 0;
        this.findNewTarget();
    }

    @Override
    public void tick() {
        this.retargetTicks++;

        // Time to find a new target?
        if (this.retargetTicks >= this.retargetTime) {
            return; // Will trigger stop() and restart with new target
        }

        // Navigate to current target
        if (this.targetMob != null && this.targetMob.isAlive()) {
            double distanceSq = this.mob.distanceToSqr(this.targetMob);

            // If we've reached the target, STOP and just watch
            if (distanceSq <= 16.0D) { // Within 4 blocks
                this.mob.getNavigation().stop();
                this.mob.getLookControl().setLookAt(this.targetMob, 30.0F, 30.0F);
                // Don't do any more pathfinding - just stay here
                return;
            }

            // Look at the target mob
            this.mob.getLookControl().setLookAt(this.targetMob, 30.0F, 30.0F);

            // Only recalculate path occasionally and if not already navigating
            PathNavigation navigation = this.mob.getNavigation();
            if (this.retargetTicks % 40 == 0 || !navigation.isInProgress()) { // Recalculate path every 2 seconds or if stopped
                Path path = navigation.createPath(this.targetMob, 2); // Stop 2 blocks away
                if (path != null) {
                    navigation.moveTo(path, this.moveSpeed);
                    this.stuckTicks = 0;
                } else {
                    this.stuckTicks++;
                }
            }

            // If stuck for too long, just wait for retarget timer
            if (this.stuckTicks > 5) {
                navigation.stop();
            }

        } else if (this.targetPos != null) {
            double distanceSq = this.mob.distanceToSqr(Vec3.atCenterOf(this.targetPos));

            // If we've reached the position, STOP and just wait
            if (distanceSq <= 16.0D) { // Within 4 blocks
                this.mob.getNavigation().stop();
                return;
            }

            // Look at the target position
            this.mob.getLookControl().setLookAt(
                this.targetPos.getX() + 0.5,
                this.targetPos.getY(),
                this.targetPos.getZ() + 0.5,
                30.0F, 30.0F
            );

            // Move toward the target position
            PathNavigation navigation = this.mob.getNavigation();
            if (!navigation.isInProgress() || this.retargetTicks % 40 == 0) {
                navigation.moveTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), this.moveSpeed);
            }
        }
    }

    @Override
    public void stop() {
        this.targetMob = null;
        this.targetPos = null;
        this.mob.getNavigation().stop();
    }

    private void findNewTarget() {
        LOGGER.info("*** Find new target");
        // Try to find a nearby mob first
        List<Mob> nearbyMobs = this.mob.level().getEntitiesOfClass(
            Mob.class,
            this.mob.getBoundingBox().inflate(this.searchRange),
            (otherMob) -> otherMob != this.mob && otherMob.isAlive() && !(otherMob instanceof SteveEntity)
        );

        if (!nearbyMobs.isEmpty()) {
            // Pick a random mob from the list
            this.targetMob = nearbyMobs.get(this.mob.getRandom().nextInt(nearbyMobs.size()));
            this.targetPos = null;

            // Play sound when targeting a mob
            this.mob.playSound(ModSounds.STEVE_LOOK.get(), 0.8F, 1.0F);

        } else {
            // No mobs found, pick a random position to walk to
            this.targetMob = null;
            this.targetPos = this.getRandomWanderPos();

            // Play a different sound or same sound at different pitch
            // this.mob.playSound(ModSounds.PLAYER_MODEL_MOB_AMBIENT.get(), 0.6F, 0.9F);
        }
    }

    private BlockPos getRandomWanderPos() {
        // Generate a random position within wanderRadius
        int x = this.mob.getBlockX() + this.mob.getRandom().nextInt(this.wanderRadius * 2) - this.wanderRadius;
        int z = this.mob.getBlockZ() + this.mob.getRandom().nextInt(this.wanderRadius * 2) - this.wanderRadius;
        int y = this.mob.getBlockY();

        // Try to find a valid Y level
        BlockPos pos = new BlockPos(x, y, z);
        for (int i = -3; i <= 3; i++) {
            BlockPos testPos = pos.offset(0, i, 0);
            if (this.mob.level().getBlockState(testPos).isAir() &&
                !this.mob.level().getBlockState(testPos.below()).isAir()) {
                return testPos;
            }
        }

        return pos;
    }
}