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

        ClientGunTracker.timerFraction += deltaTracker.getGameTimeDeltaTicks() / 20.0f;
        if (ClientGunTracker.timerFraction > 1.0f) {
            ClientGunTracker.clear();
            return;
        }

        Minecraft client = Minecraft.getInstance();
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

        int barW = screenW - 60;
        int barH = 6;
        int barX = 30;
        int barY = screenH / 2 + 30;
        extractor.fill(barX, barY, barX + barW, barY + barH, 0xFF333333);

        float frac = Math.min(ClientGunTracker.timerFraction, 1.0f);
        int fillW = (int) (barW * frac);
        int fillColor = frac > 0.7f ? 0xFFFF5555 : frac > 0.4f ? 0xFFFFFF55 : 0xFF55FF55;
        extractor.fill(barX, barY, barX + fillW, barY + barH, fillColor);
    }
}
