
// File: src/main/java/com/example/mobspawnermod/blocks/MobSpawnerBlock.java
package net.kaupenjoe.tutorialmod.block;


import net.kaupenjoe.tutorialmod.TutorialMod;
import net.kaupenjoe.tutorialmod.entity.ModEntities;
import net.kaupenjoe.tutorialmod.entity.custom.ZooGoerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.entity.MobSpawnType;

public class MobSpawnerBlock extends Block {

    public MobSpawnerBlock() {
        super(BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .strength(3.0F, 3.0F)
            .requiresCorrectToolForDrops());
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);

        // 5% chance to spawn mob each random tick
        if (random.nextFloat() < 0.05F) {
            spawnWalkingMob(level, pos);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    private void spawnWalkingMob(ServerLevel level, BlockPos spawnerPos) {
        BlockPos spawnPos = spawnerPos.above();

        // Check if spawn position is valid
        if (!level.getBlockState(spawnPos).isAir() || !level.getBlockState(spawnPos.above()).isAir()) {
            return;
        }

        // Create and spawn custom walker entity
        ZooGoerEntity walker = new ZooGoerEntity(ModEntities.ZOO_GOER.get(), level);
        walker.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
        walker.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.SPAWNER, null, null);

        level.addFreshEntity(walker);
    }
}