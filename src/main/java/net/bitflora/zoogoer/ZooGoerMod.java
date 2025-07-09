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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ZooGoerMod.MOD_ID)
public class ZooGoerMod {
    public static final String MOD_ID = "zoogoer";
    public static final Logger LOGGER = LogUtils.getLogger();


    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ZooGoerMod.MOD_ID);


    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ZooGoerMod.MOD_ID);

    public static final RegistryObject<Block> ZOO_DONATION_BARREL_BLOCK = BLOCKS.register("zoo_donation_barrel_block", ZooDonationBarrelBlock::new);
    public static final RegistryObject<Item> ZOO_DONATION_BARREL_BLOCK_ITEM = ITEMS.register("zoo_donation_barrel_block",
        () -> new BlockItem(ZOO_DONATION_BARREL_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Block> FISH_DONATION_BARREL_BLOCK = BLOCKS.register("fish_donation_barrel_block", FishDonationBarrelBlock::new);
    public static final RegistryObject<Item> FISH_DONATION_BARREL_BLOCK_ITEM = ITEMS.register("fish_donation_barrel_block",
        () -> new BlockItem(FISH_DONATION_BARREL_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Block> HERP_DONATION_BARREL_BLOCK = BLOCKS.register("herp_donation_barrel_block", HerpDonationBarrelBlock::new);
    public static final RegistryObject<Item> HERP_DONATION_BARREL_BLOCK_ITEM = ITEMS.register("herp_donation_barrel_block",
        () -> new BlockItem(HERP_DONATION_BARREL_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Block> MONSTER_DONATION_BARREL_BLOCK = BLOCKS.register("monster_donation_barrel_block", MonsterDonationBarrelBlock::new);
    public static final RegistryObject<Item> MONSTER_DONATION_BARREL_BLOCK_ITEM = ITEMS.register("monster_donation_barrel_block",
        () -> new BlockItem(MONSTER_DONATION_BARREL_BLOCK.get(), new Item.Properties()));

    public ZooGoerMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

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

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }


    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
        }
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ZOO_DONATION_BARREL_BLOCK_ITEM);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            EntityRenderers.register(ModEntities.ZOO_GOER.get(), ZooGoerRenderer::new);
            EntityRenderers.register(ModEntities.FISH_ZOO_GOER.get(), FishZooGoerRenderer::new);
            EntityRenderers.register(ModEntities.HERP_ZOO_GOER.get(), HerpZooGoerRenderer::new);
            EntityRenderers.register(ModEntities.MONSTER_ZOO_GOER.get(), MonsterZooGoerRenderer::new);

        }
    }
}
