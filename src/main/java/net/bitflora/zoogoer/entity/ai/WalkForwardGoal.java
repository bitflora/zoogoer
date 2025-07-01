
// File: src/main/java/com/example/mobspawnermod/entities/ai/WalkForwardGoal.java
package net.bitflora.zoogoer.entity.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;


/**
 * Actually works! They move forward about a step every few seconds.
 */
public class WalkForwardGoal extends Goal {
    private final PathfinderMob mob;
    private final int maxSteps;
    private int stepsTaken;
    private Vec3 initialDirection;
    private boolean hasStarted;
    private int tickCounter;

    public WalkForwardGoal(PathfinderMob mob, int maxSteps) {
        this.mob = mob;
        this.maxSteps = maxSteps;
        this.stepsTaken = 0;
        this.hasStarted = false;
        this.tickCounter = 0;
    }

    @Override
    public boolean canUse() {
        return stepsTaken < maxSteps;
    }

    @Override
    public boolean canContinueToUse() {
        return stepsTaken < maxSteps && mob.isAlive();
    }

    @Override
    public void start() {
        if (!hasStarted) {
            // Get initial facing direction
            initialDirection = mob.getLookAngle().normalize();
            hasStarted = true;
        }
    }

    @Override
    public void tick() {
        tickCounter++;

        // Move every 20 ticks (1 second)
        if (tickCounter >= 20 && mob.getNavigation().isDone()) {
            Vec3 currentPos = mob.position();
            Vec3 targetPos = currentPos.add(initialDirection.scale(1.0));

            mob.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, 1.0);
            stepsTaken++;
            tickCounter = 0;
        }
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
    }
}