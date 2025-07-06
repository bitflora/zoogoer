package net.bitflora.zoogoer.entity.custom;


import net.bitflora.zoogoer.data.EntityValuesManager;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;

import java.util.Optional;

import javax.annotation.Nonnull;

public class HerpFanEntity extends ZooGoerEntity {

    @Override
    protected Optional<Double> getSpecialistValue(@Nonnull LivingEntity entity) {
        return EntityValuesManager.HERP_VALUES.getEntityValue(entity);
    }

    @Override
    protected double getBaseModifier() {
        return 0.5;
    }

    public HerpFanEntity(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

}
