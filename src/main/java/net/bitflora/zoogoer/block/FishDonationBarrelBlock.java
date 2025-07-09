
package net.bitflora.zoogoer.block;


import javax.annotation.Nonnull;

import net.bitflora.zoogoer.entity.ModEntities;
import net.bitflora.zoogoer.entity.custom.*;
import net.minecraft.server.level.ServerLevel;

public class FishDonationBarrelBlock extends ZooDonationBarrelBlock {

    @Override
    protected ZooGoerEntity getEntityType(ServerLevel level) {
        return new FishZooGoerEntity(ModEntities.FISH_ZOO_GOER.get(), level);
    }
}