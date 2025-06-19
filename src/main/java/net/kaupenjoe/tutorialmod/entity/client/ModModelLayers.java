package net.kaupenjoe.tutorialmod.entity.client;

import net.kaupenjoe.tutorialmod.TutorialMod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation ZOO_GOER_LAYER = new ModelLayerLocation(
            new ResourceLocation(TutorialMod.MOD_ID, "zoo_goer_layer"), "main");

    public static final ModelLayerLocation PINE_BOAT_LAYER = new ModelLayerLocation(
            new ResourceLocation(TutorialMod.MOD_ID, "boat/pine"), "main");
    public static final ModelLayerLocation PINE_CHEST_BOAT_LAYER = new ModelLayerLocation(
            new ResourceLocation(TutorialMod.MOD_ID, "chest_boat/pine"), "main");

}
