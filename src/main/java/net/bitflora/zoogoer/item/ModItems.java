package net.bitflora.zoogoer.item;

import net.bitflora.zoogoer.ZooGoerMod;
import net.bitflora.zoogoer.block.ModBlocks;
import net.bitflora.zoogoer.entity.ModEntities;
import net.bitflora.zoogoer.sound.ModSounds;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ZooGoerMod.MOD_ID);

    public static final RegistryObject<Item> ZOO_GOER_SPAWN_EGG = ITEMS.register("zoo_goer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.ZOO_GOER, 0xff0000, 0xffff00, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
