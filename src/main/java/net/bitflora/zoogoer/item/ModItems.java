package net.bitflora.zoogoer.item;

import net.bitflora.zoogoer.ZooGoerMod;
import net.bitflora.zoogoer.block.ModBlocks;
import net.bitflora.zoogoer.entity.ModEntities;
import net.bitflora.zoogoer.sound.ModSounds;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, ZooGoerMod.MOD_ID);

    public static final DeferredHolder<Item, Item> ZOO_GOER_SPAWN_EGG = ITEMS.register("zoo_goer_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.ZOO_GOER, 0xff0000, 0xffff00, new Item.Properties()));

    public static final DeferredHolder<Item, Item> BIRD_ZOO_GOER_SPAWN_EGG = ITEMS.register("bird_zoo_goer_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.BIRD_ZOO_GOER, 0xffff0, 0xff00ff, new Item.Properties()));

    public static final DeferredHolder<Item, Item> FISH_ZOO_GOER_SPAWN_EGG = ITEMS.register("fish_zoo_goer_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.FISH_ZOO_GOER, 0x0000ff, 0x00ff00, new Item.Properties()));

    public static final DeferredHolder<Item, Item> HERP_ZOO_GOER_SPAWN_EGG = ITEMS.register("herp_zoo_goer_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.HERP_ZOO_GOER, 0x000055, 0x0000ff, new Item.Properties()));

    public static final DeferredHolder<Item, Item> MONSTER_ZOO_GOER_SPAWN_EGG = ITEMS.register("monster_zoo_goer_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.MONSTER_ZOO_GOER, 0x333333, 0x000000, new Item.Properties()));

    public static final DeferredHolder<Item, Item> STAR_ZOO_GOER_SPAWN_EGG = ITEMS.register("star_zoo_goer_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.STAR_ZOO_GOER, 0x000000, 0xffff00, new Item.Properties()));

    public static final DeferredHolder<Item, Item> STEVE_SPAWN_EGG = ITEMS.register("steve_spawn_egg",
        () -> new AnimatedSpawnEggItem(
                ModEntities.STEVE_MOB,  // Your entity type supplier
                0xffffff,               // Background color (white)
                0x000000,               // Highlight color (black)
                new Item.Properties()
        ));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
