package net.kaupenjoe.tutorialmod.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.level.LevelReader;

 public class MoveToGoal extends MoveToBlockGoal
    {
        public MoveToGoal(PathfinderMob pathfinderMob, double speed, BlockPos to)
        {
            super(pathfinderMob, speed, 0);
            this.blockPos = to;
        }

        @Override
        protected boolean isValidTarget(LevelReader var0, BlockPos var1)
        {
            return true;
        }

        @Override
        public void start()
        {
            this.mob.getNavigation().moveTo(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ(), this.speedModifier);
        }

        @Override
        public boolean canUse()
        {
            return true;
        }

        @Override
        protected boolean findNearestBlock()
        {
            return true;
        }


    }