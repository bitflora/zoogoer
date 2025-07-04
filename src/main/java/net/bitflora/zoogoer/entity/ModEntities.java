package net.bitflora.zoogoer.entity;

import net.bitflora.zoogoer.ZooGoerMod;
import net.bitflora.zoogoer.entity.custom.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ZooGoerMod.MOD_ID);

    public static final RegistryObject<EntityType<ZooGoerEntity>> ZOO_GOER =
            ENTITY_TYPES.register("zoo_goer", () -> EntityType.Builder.of(ZooGoerEntity::new, MobCategory.CREATURE)
                    .sized(2.5f, 2.5f).build("zoo_goer"));

    public static final RegistryObject<EntityType<FishLoverEntity>> FISH_LOVER =
        ENTITY_TYPES.register("fish_lover", () -> EntityType.Builder.of(FishLoverEntity::new, MobCategory.CREATURE)
                .sized(2.5f, 2.5f).build("fish_lover"));

    public static final RegistryObject<EntityType<HerpFanEntity>> HERP_FAN =
        ENTITY_TYPES.register("herp_fan", () -> EntityType.Builder.of(HerpFanEntity::new, MobCategory.CREATURE)
                .sized(2.5f, 2.5f).build("herp_fan"));

    public static final RegistryObject<EntityType<MonsterWatcherEntity>> MONSTER_WATCHER =
        ENTITY_TYPES.register("monster_watcher", () -> EntityType.Builder.of(MonsterWatcherEntity::new, MobCategory.CREATURE)
                .sized(2.5f, 2.5f).build("monster_watcher"));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
