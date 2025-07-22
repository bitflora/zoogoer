package net.bitflora.zoogoer.event;

import net.bitflora.zoogoer.ZooGoerMod;
import net.bitflora.zoogoer.entity.ModEntities;
import net.bitflora.zoogoer.entity.custom.*;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ZooGoerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.ZOO_GOER.get(), ZooGoerEntity.createAttributes().build());
        event.put(ModEntities.BIRD_ZOO_GOER.get(), BirdZooGoerEntity.createAttributes().build());
        event.put(ModEntities.FISH_ZOO_GOER.get(), FishZooGoerEntity.createAttributes().build());
        event.put(ModEntities.HERP_ZOO_GOER.get(), HerpZooGoerEntity.createAttributes().build());
        event.put(ModEntities.MONSTER_ZOO_GOER.get(), MonsterZooGoerEntity.createAttributes().build());
    }
}
