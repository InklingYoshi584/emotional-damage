package online.inklingyoshi.asian;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import online.inklingyoshi.asian.command.ModDifficultyCommand;
import online.inklingyoshi.asian.difficulty.ModDifficultyNetworking;
import online.inklingyoshi.asian.difficulty.PendingModDifficulty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmotionalDamage implements ModInitializer {
    public static final String MOD_ID = "emotional-damage";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModDifficultyNetworking.register();
        PendingModDifficulty.registerServerStartHandler();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ModDifficultyCommand.register(dispatcher);
        });

        LOGGER.info("Emotional Damage initialized");
    }
}
