package net.bitflora.zoogoer;

import com.mojang.logging.LogUtils;

import net.bitflora.zoogoer.block.*;
import net.bitflora.zoogoer.block.entity.ModBlockEntities;
import net.bitflora.zoogoer.entity.ModEntities;
import net.bitflora.zoogoer.entity.client.*;
import net.bitflora.zoogoer.item.ModCreativeModTabs;
import net.bitflora.zoogoer.item.ModItems;
import net.bitflora.zoogoer.loot.ModLootModifiers;
import net.bitflora.zoogoer.recipe.ModRecipes;
import net.bitflora.zoogoer.sound.ModSounds;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ZooGoerMod.MOD_ID)
public class ZooGoerMod {
    public static final String MOD_ID = "zoogoer";
    public static final Logger LOGGER = LogUtils.getLogger();


    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, ZooGoerMod.MOD_ID);


    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, ZooGoerMod.MOD_ID);

    public static final DeferredHolder<Block, Block> ZOO_DONATION_BARREL_BLOCK = BLOCKS.register("zoo_donation_barrel_block", () -> new ZooDonationBarrelBlock());
    public static final DeferredHolder<Item, Item> ZOO_DONATION_BARREL_BLOCK_ITEM = ITEMS.register("zoo_donation_barrel_block",
        () -> new BlockItem(ZOO_DONATION_BARREL_BLOCK.get(), new Item.Properties()));

    public static final DeferredHolder<Block, Block> BIRD_DONATION_BARREL_BLOCK = BLOCKS.register("bird_donation_barrel_block", BirdDonationBarrelBlock::new);
    public static final DeferredHolder<Item, Item> BIRD_DONATION_BARREL_BLOCK_ITEM = ITEMS.register("bird_donation_barrel_block",
        () -> new BlockItem(BIRD_DONATION_BARREL_BLOCK.get(), new Item.Properties()));


    public static final DeferredHolder<Block, Block> FISH_DONATION_BARREL_BLOCK = BLOCKS.register("fish_donation_barrel_block", FishDonationBarrelBlock::new);
    public static final DeferredHolder<Item, Item> FISH_DONATION_BARREL_BLOCK_ITEM = ITEMS.register("fish_donation_barrel_block",
        () -> new BlockItem(FISH_DONATION_BARREL_BLOCK.get(), new Item.Properties()));

    public static final DeferredHolder<Block, Block> HERP_DONATION_BARREL_BLOCK = BLOCKS.register("herp_donation_barrel_block", HerpDonationBarrelBlock::new);
    public static final DeferredHolder<Item, Item> HERP_DONATION_BARREL_BLOCK_ITEM = ITEMS.register("herp_donation_barrel_block",
        () -> new BlockItem(HERP_DONATION_BARREL_BLOCK.get(), new Item.Properties()));

    public static final DeferredHolder<Block, Block> MONSTER_DONATION_BARREL_BLOCK = BLOCKS.register("monster_donation_barrel_block", MonsterDonationBarrelBlock::new);
    public static final DeferredHolder<Item, Item> MONSTER_DONATION_BARREL_BLOCK_ITEM = ITEMS.register("monster_donation_barrel_block",
        () -> new BlockItem(MONSTER_DONATION_BARREL_BLOCK.get(), new Item.Properties()));

    public static final DeferredHolder<Block, Block> STAR_DONATION_BARREL_BLOCK = BLOCKS.register("star_donation_barrel_block", StarDonationBarrelBlock::new);
    public static final DeferredHolder<Item, Item> STAR_DONATION_BARREL_BLOCK_ITEM = ITEMS.register("star_donation_barrel_block",
        () -> new BlockItem(STAR_DONATION_BARREL_BLOCK.get(), new Item.Properties()));

    public ZooGoerMod(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        ModCreativeModTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModLootModifiers.register(modEventBus);

        ModSounds.register(modEventBus);
        ModEntities.register(modEventBus);

        ModBlockEntities.register(modEventBus);

        ModRecipes.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }


    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
        }
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ZOO_DONATION_BARREL_BLOCK_ITEM.get());
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            EntityRenderers.register(ModEntities.ZOO_GOER.get(), ZooGoerRenderer::new);
            EntityRenderers.register(ModEntities.BIRD_ZOO_GOER.get(), BirdZooGoerRenderer::new);
            EntityRenderers.register(ModEntities.FISH_ZOO_GOER.get(), FishZooGoerRenderer::new);
            EntityRenderers.register(ModEntities.HERP_ZOO_GOER.get(), HerpZooGoerRenderer::new);
            EntityRenderers.register(ModEntities.MONSTER_ZOO_GOER.get(), MonsterZooGoerRenderer::new);
            EntityRenderers.register(ModEntities.STAR_ZOO_GOER.get(), StarZooGoerRenderer::new);

            EntityRenderers.register(ModEntities.STEVE_MOB.get(), SteveRenderer::new);

        }
    }
}
