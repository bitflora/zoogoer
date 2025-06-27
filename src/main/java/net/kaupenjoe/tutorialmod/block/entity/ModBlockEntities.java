package net.kaupenjoe.tutorialmod.block.entity;

import net.kaupenjoe.tutorialmod.TutorialMod;
import net.kaupenjoe.tutorialmod.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TutorialMod.MOD_ID);



    public static final RegistryObject<BlockEntityType<MobSpawnerBlockEntity>> MOB_SPAWNER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("mob_spawner_block_entity", () ->
                    BlockEntityType.Builder.of(MobSpawnerBlockEntity::new,
                            TutorialMod.MOB_SPAWNER_BLOCK.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
