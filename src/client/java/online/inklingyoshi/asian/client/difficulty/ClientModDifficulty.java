package online.inklingyoshi.asian.difficulty;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.options.WorldOptionsScreen;
import online.inklingyoshi.asian.network.ModDifficultyPayload;

public final class ClientModDifficulty {
    public static ModDifficulty current = ModDifficulty.NORMAL;
    public static ModDifficulty pending = null;

    public static void registerReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(ModDifficultyPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                current = payload.difficulty();
                pending = null;
                Minecraft mc = Minecraft.getInstance();
                if (mc.screen instanceof OptionsScreen os) {
                    os.onDifficultyChanged();
                } else if (mc.screen instanceof WorldOptionsScreen wos) {
                    wos.onDifficultyChanged();
                }
            });
        });
    }
}
