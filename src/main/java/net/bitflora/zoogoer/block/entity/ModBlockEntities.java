package net.bitflora.zoogoer.block.entity;

import net.bitflora.zoogoer.ZooGoerMod;
import net.bitflora.zoogoer.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ZooGoerMod.MOD_ID);



    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ZooDonationBarrelBlockEntity>> MOB_SPAWNER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("zoo_donation_barrel_block_entity", () ->
                    BlockEntityType.Builder.of(ZooDonationBarrelBlockEntity::new,
                            ZooGoerMod.ZOO_DONATION_BARREL_BLOCK.get(),
                            ZooGoerMod.BIRD_DONATION_BARREL_BLOCK.get(),
                            ZooGoerMod.FISH_DONATION_BARREL_BLOCK.get(),
                            ZooGoerMod.HERP_DONATION_BARREL_BLOCK.get(),
                            ZooGoerMod.MONSTER_DONATION_BARREL_BLOCK.get(),
                            ZooGoerMod.STAR_DONATION_BARREL_BLOCK.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
