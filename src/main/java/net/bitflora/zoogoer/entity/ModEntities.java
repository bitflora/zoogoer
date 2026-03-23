package net.bitflora.zoogoer.entity;

import net.bitflora.zoogoer.ZooGoerMod;
import net.bitflora.zoogoer.entity.custom.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, ZooGoerMod.MOD_ID);

    public static final float ZOO_GOER_WIDTH = 0.6f;
    public static final float ZOO_GOER_HEIGHT = 1.95f;

    public static final DeferredHolder<EntityType<?>, EntityType<ZooGoerEntity>> ZOO_GOER =
            ENTITY_TYPES.register("zoo_goer", () -> EntityType.Builder.of(ZooGoerEntity::new, MobCategory.CREATURE)
                    .sized(ZOO_GOER_WIDTH, ZOO_GOER_HEIGHT).build("zoo_goer"));

    public static final DeferredHolder<EntityType<?>, EntityType<BirdZooGoerEntity>> BIRD_ZOO_GOER =
        ENTITY_TYPES.register("bird_zoo_goer", () -> EntityType.Builder.of(BirdZooGoerEntity::new, MobCategory.CREATURE)
                .sized(ZOO_GOER_WIDTH, ZOO_GOER_HEIGHT).build("bird_zoo_goer"));

    public static final DeferredHolder<EntityType<?>, EntityType<FishZooGoerEntity>> FISH_ZOO_GOER =
        ENTITY_TYPES.register("fish_zoo_goer", () -> EntityType.Builder.of(FishZooGoerEntity::new, MobCategory.CREATURE)
                .sized(ZOO_GOER_WIDTH, ZOO_GOER_HEIGHT).build("fish_zoo_goer"));

    public static final DeferredHolder<EntityType<?>, EntityType<HerpZooGoerEntity>> HERP_ZOO_GOER =
        ENTITY_TYPES.register("herp_zoo_goer", () -> EntityType.Builder.of(HerpZooGoerEntity::new, MobCategory.CREATURE)
                .sized(ZOO_GOER_WIDTH, ZOO_GOER_HEIGHT).build("herp_zoo_goer"));

    public static final DeferredHolder<EntityType<?>, EntityType<MonsterZooGoerEntity>> MONSTER_ZOO_GOER =
        ENTITY_TYPES.register("monster_zoo_goer", () -> EntityType.Builder.of(MonsterZooGoerEntity::new, MobCategory.CREATURE)
                .sized(ZOO_GOER_WIDTH, ZOO_GOER_HEIGHT).build("monster_zoo_goer"));

    public static final DeferredHolder<EntityType<?>, EntityType<StarZooGoerEntity>> STAR_ZOO_GOER =
        ENTITY_TYPES.register("star_zoo_goer", () -> EntityType.Builder.of(StarZooGoerEntity::new, MobCategory.CREATURE)
                .sized(ZOO_GOER_WIDTH, ZOO_GOER_HEIGHT).build("star_zoo_goer"));


    public static final DeferredHolder<EntityType<?>, EntityType<SteveEntity>> STEVE_MOB =
            ENTITY_TYPES.register("steve_mob",
                    () -> EntityType.Builder.of(SteveEntity::new, MobCategory.CREATURE)
                            .sized(ZOO_GOER_WIDTH, ZOO_GOER_HEIGHT) // Same size as player
                            .build("steve_mob"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
