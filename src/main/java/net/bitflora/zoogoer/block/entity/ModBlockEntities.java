package net.bitflora.zoogoer.block.entity;

import net.bitflora.zoogoer.ZooGoerMod;
import net.bitflora.zoogoer.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ZooGoerMod.MOD_ID);



    public static final RegistryObject<BlockEntityType<ZooDonationBarrelBlockEntity>> MOB_SPAWNER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("zoo_donation_barrel_block_entity", () ->
                    BlockEntityType.Builder.of(ZooDonationBarrelBlockEntity::new,
                            ZooGoerMod.ZOO_DONATION_BARREL_BLOCK.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
