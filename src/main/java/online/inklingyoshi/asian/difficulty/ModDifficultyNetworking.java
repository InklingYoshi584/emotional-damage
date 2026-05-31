package online.inklingyoshi.asian.difficulty;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.Difficulty;
import online.inklingyoshi.asian.network.ModDifficultyPayload;

public final class ModDifficultyNetworking {
    private ModDifficultyNetworking() {}

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(ModDifficultyPayload.TYPE, ModDifficultyPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ModDifficultyPayload.TYPE, ModDifficultyPayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ModDifficultyPayload.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                ModDifficultyState state = ModDifficultyState.getOrCreate(context.server());
                state.setDifficulty(payload.difficulty());

                ServerPlayNetworking.send(context.player(), payload);

                Difficulty forced = payload.difficulty().getForcedVanillaDifficulty();
                if (forced != null) {
                    context.server().setDifficulty(forced, true);
                }
            });
        });
    }
}
