package net.bitflora.zoogoer.util;

import net.bitflora.zoogoer.ZooGoerMod;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class ModWoodTypes {
    public static final WoodType PINE = WoodType.register(new WoodType(ZooGoerMod.MOD_ID + ":pine", BlockSetType.OAK));
}
