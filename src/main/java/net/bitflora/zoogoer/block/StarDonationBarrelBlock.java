
package net.bitflora.zoogoer.block;


import javax.annotation.Nonnull;

import net.bitflora.zoogoer.entity.ModEntities;
import net.bitflora.zoogoer.entity.custom.*;
import net.minecraft.server.level.ServerLevel;

public class StarDonationBarrelBlock extends ZooDonationBarrelBlock {

    @Override
    protected ZooGoerEntity getEntityType(ServerLevel level) {
        return new StarZooGoerEntity(ModEntities.STAR_ZOO_GOER.get(), level);
    }
}