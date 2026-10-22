package com.github.alexthe666.citadel.client.rewards;

import com.github.alexthe666.citadel.client.render.pathfinding.WorldRenderMacros;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.LivingEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class CitadelPatreonRenderer {
    private static final Map<String, CitadelPatreonRenderer> PATREON_RENDERER_MAP = new LinkedHashMap<>();

    public static CitadelPatreonRenderer get(String identifier) {
        return PATREON_RENDERER_MAP.get(identifier);
    }

    public static void register(String identifier, CitadelPatreonRenderer renderer) {
        PATREON_RENDERER_MAP.put(identifier, renderer);
    }

    public abstract void render(PoseStack matrixStackIn, WorldRenderMacros.BufferSource buffer, int light, float partialTick, LivingEntity entity, float distanceIn, float rotateSpeed, float rotateHeight);

    /**
     * 26.3: draws the follower while the player is submitted. Override it to submit through {@code collector}, which
     * draws in order with the world and in shader packs' shadow passes. The default draws through {@link #render} and
     * a {@link WorldRenderMacros} batch of its own, as before.
     */
    public void submit(PoseStack matrixStackIn, SubmitNodeCollector collector, int light, float partialTick, LivingEntity entity, float distanceIn, float rotateSpeed, float rotateHeight) {
        WorldRenderMacros.BufferSource buffer = WorldRenderMacros.getBufferSource();
        render(matrixStackIn, buffer, light, partialTick, entity, distanceIn, rotateSpeed, rotateHeight);
        buffer.endBatch();
    }

    public static String getIdOfNext(String identifier) {
        Object[] ids = PATREON_RENDERER_MAP.keySet().toArray();
        if(identifier.equals("none") && ids.length > 0){
            return (String)ids[0];
        }
        for (int i = 0; i < ids.length - 1; i++) {
           if(ids[i].equals(identifier)){
               return (String)ids[i+1];
           }
        }
        return "none";
    }

    public static String getDefault(){
        return "citadel";
    }
}
