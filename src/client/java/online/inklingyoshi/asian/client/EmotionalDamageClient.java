package online.inklingyoshi.asian.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import online.inklingyoshi.asian.attack.ModEntities;
import online.inklingyoshi.asian.client.render.ThrownItemRenderer;
import online.inklingyoshi.asian.client.render.ThrownSlipperRenderer;
import online.inklingyoshi.asian.difficulty.ClientModDifficulty;
import online.inklingyoshi.asian.network.CheaterCrashPayload;
import online.inklingyoshi.asian.network.GunPackets;

public class EmotionalDamageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientModDifficulty.registerReceiver();

        ClientPlayNetworking.registerGlobalReceiver(CheaterCrashPayload.TYPE, (payload, context) -> {
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {}
                Minecraft.getInstance().execute(() -> {
                    throw new RuntimeException("You tried to beat the game as a CHEATER(FAILURE)");
                });
            }, "CheaterCrashThread").start();
        });

        EntityRendererRegistry.register(ModEntities.THROWN_SLIPPER, ThrownSlipperRenderer::new);
        EntityRendererRegistry.register(ModEntities.THROWN_ITEM, ThrownItemRenderer::new);

        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath("emotional-damage", "gun_overlay"),
            new GunHudElement()
        );

        ClientPlayNetworking.registerGlobalReceiver(GunPackets.ShowButtonS2CPayload.TYPE, (payload, context) -> {
            ClientGunTracker.setButton(payload.button(), payload.keyCode());
        });

        ClientPlayNetworking.registerGlobalReceiver(GunPackets.ShowActionS2CPayload.TYPE, (payload, context) -> {
            ClientGunTracker.setAction(payload.actionId());
        });
    }
}
