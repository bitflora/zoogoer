package net.kaupenjoe.tutorialmod.entity.ai;


import net.kaupenjoe.tutorialmod.entity.custom.ZooGoerEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpeciesCounterGoal extends Goal {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpeciesCounterGoal.class);

    private final ZooGoerEntity mob;
    private final double detectionRange;
    private final int cooldownTicks;
    private int ticksUntilNextCount;
    private LivingEntity currentTarget;
    private int lookTime;

    public SpeciesCounterGoal(ZooGoerEntity mob, double detectionRange, int cooldownTicks) {
        this.mob = mob;
        this.detectionRange = detectionRange;
        this.cooldownTicks = cooldownTicks;
        this.ticksUntilNextCount = 0;
    }

    @Override
    public boolean canUse() {
        if (this.ticksUntilNextCount > 0) {
            this.ticksUntilNextCount--;
            return false;
        }

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.lookTime > 0 && this.currentTarget != null && this.currentTarget.isAlive();
    }

    @Override
    public void start() {
        this.countNearbySpecies();
        this.selectRandomTargetToLookAt();
        this.lookTime = 40 + this.mob.getRandom().nextInt(40); // Look for 2-4 seconds
    }

    @Override
    public void tick() {
        if (this.currentTarget != null) {
            // Make the mob look at the current target
            this.mob.getLookControl().setLookAt(
                this.currentTarget.getX(),
                this.currentTarget.getEyeY(),
                this.currentTarget.getZ(),
                10.0F, // Max yaw change per tick
                (float) this.mob.getMaxHeadXRot() // Max pitch change
            );

            this.lookTime--;

            // Occasionally switch targets while counting
            if (this.lookTime % 20 == 0) { // Every second
                this.selectRandomTargetToLookAt();
            }
        }
    }

    @Override
    public void stop() {
        this.currentTarget = null;
        this.lookTime = 0;
        this.ticksUntilNextCount = this.cooldownTicks;
    }

    private void countNearbySpecies() {
        List<LivingEntity> nearbyMobs = this.mob.level().getEntitiesOfClass(
            LivingEntity.class,
            new AABB(this.mob.blockPosition()).inflate(this.detectionRange),
            entity -> entity != this.mob && entity.isAlive()
        );

        for (LivingEntity entity : nearbyMobs) {
            this.mob.noticeMob(entity);
        }
        this.mob.debugNoticedMobs();
    }

    private void selectRandomTargetToLookAt() {
        List<LivingEntity> nearbyMobs = this.mob.level().getEntitiesOfClass(
            LivingEntity.class,
            new AABB(this.mob.blockPosition()).inflate(this.detectionRange),
            entity -> entity != this.mob && entity.isAlive()
        );

        if (!nearbyMobs.isEmpty()) {
            this.currentTarget = nearbyMobs.get(this.mob.getRandom().nextInt(nearbyMobs.size()));
        }
    }

}
