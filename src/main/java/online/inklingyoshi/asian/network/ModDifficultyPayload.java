package online.inklingyoshi.asian.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import online.inklingyoshi.asian.EmotionalDamage;
import online.inklingyoshi.asian.difficulty.ModDifficulty;

public record ModDifficultyPayload(ModDifficulty difficulty) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ModDifficultyPayload> TYPE =
        new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "set_difficulty")
        );

    public static final StreamCodec<RegistryFriendlyByteBuf, ModDifficultyPayload> STREAM_CODEC =
        StreamCodec.ofMember(
            (payload, buf) -> buf.writeUtf(payload.difficulty().getSerializedName()),
            buf -> new ModDifficultyPayload(ModDifficulty.byName(buf.readUtf(16)))
        );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
