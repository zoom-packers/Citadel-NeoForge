package com.github.alexthe666.citadel.client.rewards;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.ClientProxy;
import com.github.alexthe666.citadel.client.shader.CitadelShaderRenderTypes;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.github.alexthe666.citadel.client.texture.CitadelTextureManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.github.alexthe666.citadel.client.render.pathfinding.WorldRenderMacros;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.BiConsumer;

public class SpaceStationPatreonRenderer extends CitadelPatreonRenderer {


    private static final Identifier CITADEL_TEXTURE = Identifier.fromNamespaceAndPath("citadel", "textures/patreon/citadel_model.png");
    private static final Identifier CITADEL_LIGHTS_TEXTURE = Identifier.fromNamespaceAndPath("citadel", "textures/patreon/citadel_model_glow.png");
    private final Identifier textureKey;
    private int[] colors;

    public SpaceStationPatreonRenderer(Identifier textureKey, int[] colors) {
        this.textureKey = textureKey;
        this.colors = colors;
    }


    @Override
    public void render(PoseStack matrixStackIn, WorldRenderMacros.BufferSource buffer, int light, float partialTick, LivingEntity entity, float distanceIn, float rotateSpeed, float rotateHeight) {
        draw(matrixStackIn, partialTick, entity, distanceIn, rotateSpeed, rotateHeight, (renderType, poseStack) ->
                ClientProxy.CITADEL_MODEL.renderToBuffer(poseStack, buffer.getBuffer(renderType), light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF));
    }

    // 26.3: through the frame's collector, so the station draws with the player, in order, and in shader packs' shadow passes
    @Override
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int light, float partialTick, LivingEntity entity, float distanceIn, float rotateSpeed, float rotateHeight) {
        draw(matrixStackIn, partialTick, entity, distanceIn, rotateSpeed, rotateHeight, (renderType, poseStack) ->
                collector.submitCustomGeometry(poseStack, renderType, (pose, consumer) -> {
                    PoseStack at = new PoseStack();
                    at.last().set(pose);
                    ClientProxy.CITADEL_MODEL.renderToBuffer(at, consumer, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
                }));
    }

    /** Places the station and hands each of its render types to {@code model}, with the pose stack at the station. */
    private void draw(PoseStack matrixStackIn, float partialTick, LivingEntity entity, float distanceIn, float rotateSpeed, float rotateHeight, BiConsumer<RenderType, PoseStack> model) {
        float tick = entity.tickCount + partialTick;
        float bob = (float) (Math.sin(tick * 0.1F) * 1 * 0.05F - 1 * 0.05F);
        float scale = 0.4F;
        float rotation = Mth.wrapDegrees((tick * rotateSpeed) % 360);
        matrixStackIn.pushPose();
        matrixStackIn.rotate(Axis.YP.rotationDegrees(rotation));
        matrixStackIn.translate(0, entity.getBbHeight() + bob + (rotateHeight - 1F), entity.getBbWidth() * distanceIn);
        matrixStackIn.pushPose();
        matrixStackIn.rotate(Axis.XP.rotationDegrees(75));
        matrixStackIn.scale(scale, scale, scale);
        matrixStackIn.rotate(Axis.XP.rotationDegrees(90));
        matrixStackIn.rotate(Axis.YP.rotationDegrees(rotation * 10));
        ClientProxy.CITADEL_MODEL.resetToDefaultPose();
        if(CitadelConstants.debugShaders()){
            PostEffectRegistry.renderEffectForNextTick(ClientProxy.RAINBOW_AURA_POST_SHADER);
            model.accept(CitadelShaderRenderTypes.getRainbowAura(CITADEL_TEXTURE), matrixStackIn);
        }else{
            model.accept(RenderTypes.entityCutout(CitadelTextureManager.getColorMappedTexture(textureKey, CITADEL_TEXTURE, colors), false), matrixStackIn);
            model.accept(RenderTypes.eyes(CITADEL_LIGHTS_TEXTURE), matrixStackIn);
        }
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }
}
