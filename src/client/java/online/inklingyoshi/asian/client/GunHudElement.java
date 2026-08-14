package online.inklingyoshi.asian.client;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class GunHudElement implements HudElement {

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker) {
        if (!ClientGunTracker.isActive) return;

        Minecraft client = Minecraft.getInstance();
        if (client.player == null || !client.player.isAlive()) {
            ClientGunTracker.clear();
            return;
        }

        if (ClientGunTracker.isActive) {
            // Mirror server timeouts: 20 ticks per button, 60 for the action prompt.
            // Client-side failsafe so creative/invulnerable players can't get softlocked
            // (server kill is a no-op there and never resets the tracker).
            ClientGunTracker.timerFraction += deltaTracker.getGameTimeDeltaTicks()
                / (ClientGunTracker.inAction ? 60.0f : 20.0f);
            if (ClientGunTracker.timerFraction > 1.0f) {
                ClientGunTracker.clear();
                return;
            }
        }

        int screenW = extractor.guiWidth();
        int screenH = extractor.guiHeight();

        extractor.fill(0, 0, screenW, screenH, 0x88000000);

        Font font = client.font;
        int centerX = screenW / 2;

        if (ClientGunTracker.inAction) {
            extractor.centeredText(font, ClientGunTracker.actionText, centerX, screenH / 2 - 20, 0xFFFF5555);
        } else {
            extractor.centeredText(font, String.valueOf(ClientGunTracker.buttonChar),
                centerX, screenH / 2 - 40, 0xFFFFFFFF);
        }
    }
}
