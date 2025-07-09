
package net.bitflora.zoogoer.block;


import javax.annotation.Nonnull;

import net.bitflora.zoogoer.entity.ModEntities;
import net.bitflora.zoogoer.entity.custom.*;
import net.minecraft.server.level.ServerLevel;

public class MonsterDonationBarrelBlock extends ZooDonationBarrelBlock {

    @Override
    protected ZooGoerEntity getEntityType(ServerLevel level) {
        return new FishZooGoerEntity(ModEntities.MONSTER_ZOO_GOER.get(), level);
    }
}