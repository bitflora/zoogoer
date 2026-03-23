package net.bitflora.zoogoer.event;

import net.bitflora.zoogoer.ZooGoerMod;
import net.bitflora.zoogoer.block.entity.ModBlockEntities;
import net.bitflora.zoogoer.block.entity.ZooDonationBarrelBlockEntity;
import net.bitflora.zoogoer.entity.ModEntities;
import net.bitflora.zoogoer.entity.custom.*;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ZooGoerMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.ZOO_GOER.get(), ZooGoerEntity.createAttributes().build());
        event.put(ModEntities.BIRD_ZOO_GOER.get(), BirdZooGoerEntity.createAttributes().build());
        event.put(ModEntities.FISH_ZOO_GOER.get(), FishZooGoerEntity.createAttributes().build());
        event.put(ModEntities.HERP_ZOO_GOER.get(), HerpZooGoerEntity.createAttributes().build());
        event.put(ModEntities.MONSTER_ZOO_GOER.get(), MonsterZooGoerEntity.createAttributes().build());
        event.put(ModEntities.STAR_ZOO_GOER.get(), StarZooGoerEntity.createAttributes().build());

        event.put(ModEntities.STEVE_MOB.get(), SteveEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.MOB_SPAWNER_BLOCK_ENTITY.get(),
            (be, side) -> be.getItemHandler()
        );
    }
}
