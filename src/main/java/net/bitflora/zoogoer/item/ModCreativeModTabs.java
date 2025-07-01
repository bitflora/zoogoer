package net.bitflora.zoogoer.item;

import net.bitflora.zoogoer.ZooGoerMod;
import net.bitflora.zoogoer.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ZooGoerMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ZOOGOER_TAB = CREATIVE_MODE_TABS.register("zoogoer_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.ZOO_GOER_SPAWN_EGG.get()))
                    .title(Component.translatable("creativetab.zoogoer_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.ZOO_GOER_SPAWN_EGG.get());


                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
