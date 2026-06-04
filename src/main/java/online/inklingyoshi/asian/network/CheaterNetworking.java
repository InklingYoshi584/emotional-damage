package online.inklingyoshi.asian.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class CheaterNetworking {
    private CheaterNetworking() {}

    public static void registerServer() {
        PayloadTypeRegistry.clientboundPlay().register(CheaterCrashPayload.TYPE, CheaterCrashPayload.STREAM_CODEC);
    }

    public static void sendCrash(ServerPlayNetworking.Context context) {
        ServerPlayNetworking.send(context.player(), new CheaterCrashPayload());
    }
}
