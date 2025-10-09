package net.bitflora.zoogoer.entity.client;


import com.mojang.blaze3d.vertex.PoseStack;
import net.bitflora.zoogoer.entity.custom.SteveEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerModelPart;

public class SteveRenderer extends MobRenderer<SteveEntity, PlayerModel<SteveEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("yourmod", "textures/entity/player_model_mob.png");

    public SteveRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(SteveEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(SteveEntity entity, float entityYaw, float partialTicks,
                      PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // Set model parts visibility - customize as needed
        this.model.setAllVisible(true);
        this.model.hat.visible = true;
        this.model.jacket.visible = true;
        this.model.leftPants.visible = true;
        this.model.rightPants.visible = true;
        this.model.leftSleeve.visible = true;
        this.model.rightSleeve.visible = true;

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale(SteveEntity entity, PoseStack poseStack, float partialTickTime) {
        // Optional: scale the model if needed
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}