package online.inklingyoshi.asian.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import online.inklingyoshi.asian.EmotionalDamage;

public record CheaterCrashPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CheaterCrashPayload> TYPE =
        new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "cheater_crash")
        );

    public static final StreamCodec<RegistryFriendlyByteBuf, CheaterCrashPayload> STREAM_CODEC =
        StreamCodec.of(
            (buf, payload) -> {},
            buf -> new CheaterCrashPayload()
        );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
