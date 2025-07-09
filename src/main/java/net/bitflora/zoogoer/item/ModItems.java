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

    public static final RegistryObject<Item> FISH_ZOO_GOER_SPAWN_EGG = ITEMS.register("fish_zoo_goer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.FISH_ZOO_GOER, 0x0000ff, 0x00ff00, new Item.Properties()));

    public static final RegistryObject<Item> HERP_ZOO_GOER_SPAWN_EGG = ITEMS.register("herp_zoo_goer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.HERP_ZOO_GOER, 0x000055, 0x0000ff, new Item.Properties()));

    public static final RegistryObject<Item> MONSTER_ZOO_GOER_SPAWN_EGG = ITEMS.register("monster_zoo_goer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MONSTER_ZOO_GOER, 0xffffff, 0x000000, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
