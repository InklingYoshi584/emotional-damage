package online.inklingyoshi.asian.difficulty;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.Difficulty;

public final class PendingModDifficulty {
    public static volatile ModDifficulty pending = null;

    public static void registerServerStartHandler() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            if (pending != null && pending != ModDifficulty.NORMAL) {
                ModDifficultyState state = ModDifficultyState.getOrCreate(server);
                state.setDifficulty(pending);
                Difficulty forced = pending.getForcedVanillaDifficulty();
                if (forced != null) {
                    server.setDifficulty(forced, true);
                }
                pending = null;
            }
        });
    }
}
