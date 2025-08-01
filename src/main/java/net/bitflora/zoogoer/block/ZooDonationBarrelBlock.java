
package net.bitflora.zoogoer.block;


import javax.annotation.Nonnull;

import net.bitflora.zoogoer.block.entity.ZooDonationBarrelBlockEntity;
import net.bitflora.zoogoer.entity.ModEntities;
import net.bitflora.zoogoer.entity.custom.ZooGoerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;

public class ZooDonationBarrelBlock extends BaseEntityBlock {

    public ZooDonationBarrelBlock() {
        super(BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .strength(3.0F, 3.0F)
            .requiresCorrectToolForDrops());
    }

    protected ZooGoerEntity getEntityType(ServerLevel level) {
        return new ZooGoerEntity(ModEntities.ZOO_GOER.get(), level);
    }


    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                               Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ZooDonationBarrelBlockEntity spawnerEntity) {
                NetworkHooks.openScreen((ServerPlayer) player, spawnerEntity, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);

        // 10% chance to spawn mob each random tick
        if (random.nextFloat() < 0.1F && !level.isNight()) {
            spawnZooGoer(level, pos);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    private void spawnZooGoer(ServerLevel level, BlockPos spawnerPos) {
        BlockPos spawnPos = spawnerPos.above();

        // Check if spawn position is valid
        if (!level.getBlockState(spawnPos).isAir() || !level.getBlockState(spawnPos.above()).isAir()) {
            return;
        }

        // Create and spawn custom walker entity
        ZooGoerEntity walker = getEntityType(level);
        walker.setOrigin(spawnerPos);
        walker.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
        walker.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.SPAWNER, null, null);

        level.addFreshEntityWithPassengers(walker);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ZooDonationBarrelBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ZooDonationBarrelBlockEntity spawnerEntity) {
                // Drop items when block is broken
                spawnerEntity.drops();
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}