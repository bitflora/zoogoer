package net.bitflora.zoogoer.entity.custom;


import net.minecraft.world.entity.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class MonsterWatcherEntity extends ZooGoerEntity {

    public MonsterWatcherEntity(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

}
