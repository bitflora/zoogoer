package net.bitflora.zoogoer.entity.client;

import net.bitflora.zoogoer.ZooGoerMod;
import net.bitflora.zoogoer.entity.custom.*;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FishZooGoerRenderer extends MobRenderer<FishZooGoerEntity, VillagerModel<FishZooGoerEntity>> {
    public FishZooGoerRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(FishZooGoerEntity entity) {
        return new ResourceLocation(ZooGoerMod.MOD_ID, "textures/entity/fish.png");
    }
}
