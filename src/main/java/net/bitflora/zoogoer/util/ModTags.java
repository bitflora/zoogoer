package net.bitflora.zoogoer.util;

import net.bitflora.zoogoer.ZooGoerMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Entities {
        public static final TagKey<EntityType<?>> ZOO_GOER_IGNORED_SPECIES =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ZooGoerMod.MOD_ID, "zoo_goer_ignored_species"));
    }

    public static class Blocks {
        public static final TagKey<Block> METAL_DETECTOR_VALUABLES = tag("metal_detector_valuables");
        public static final TagKey<Block> NEEDS_SAPPHIRE_TOOL = tag("needs_sapphire_tool");



        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(ZooGoerMod.MOD_ID, name));
        }
    }

    public static class Items {

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(ZooGoerMod.MOD_ID, name));
        }
    }
}
