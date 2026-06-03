package online.inklingyoshi.asian.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import online.inklingyoshi.asian.attack.ModEntities;
import online.inklingyoshi.asian.difficulty.ClientModDifficulty;

public class EmotionalDamageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientModDifficulty.registerReceiver();

        EntityRendererRegistry.register(ModEntities.THROWN_SLIPPER, ctx ->
            new EntityRenderer<Entity, EntityRenderState>(ctx) {
                @Override
                public EntityRenderState createRenderState() {
                    return new EntityRenderState();
                }
            });

        EntityRendererRegistry.register(ModEntities.THROWN_ITEM, ctx ->
            new EntityRenderer<Entity, EntityRenderState>(ctx) {
                @Override
                public EntityRenderState createRenderState() {
                    return new EntityRenderState();
                }
            });
    }
}
