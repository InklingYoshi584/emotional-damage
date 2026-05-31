package online.inklingyoshi.asian.client;

import net.fabricmc.api.ClientModInitializer;
import online.inklingyoshi.asian.difficulty.ClientModDifficulty;

public class EmotionalDamageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientModDifficulty.registerReceiver();
    }
}
