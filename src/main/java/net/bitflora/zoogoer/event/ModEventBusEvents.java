package net.bitflora.zoogoer.event;

import net.bitflora.zoogoer.ZooGoerMod;
import net.bitflora.zoogoer.entity.ModEntities;
import net.bitflora.zoogoer.entity.custom.FishLoverEntity;
import net.bitflora.zoogoer.entity.custom.ZooGoerEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ZooGoerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.ZOO_GOER.get(), ZooGoerEntity.createAttributes().build());
        event.put(ModEntities.FISH_LOVER.get(), FishLoverEntity.createAttributes().build());
    }
}
