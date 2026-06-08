package online.inklingyoshi.asian.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageType;
import online.inklingyoshi.asian.EmotionalDamage;

import java.util.List;
import online.inklingyoshi.asian.attack.GunChallengeState;
import online.inklingyoshi.asian.attack.IGunPlayer;
import online.inklingyoshi.asian.attack.ModDamageTypes;

public final class GunPackets {
    private GunPackets() {}

    public record ShowButtonS2CPayload(char button, int keyCode) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ShowButtonS2CPayload> TYPE =
            new CustomPacketPayload.Type<>(
                Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "gun_show_button")
            );

        public static final StreamCodec<RegistryFriendlyByteBuf, ShowButtonS2CPayload> STREAM_CODEC =
            StreamCodec.of(
                (buf, payload) -> {
                    buf.writeChar(payload.button);
                    buf.writeInt(payload.keyCode);
                },
                buf -> new ShowButtonS2CPayload(buf.readChar(), buf.readInt())
            );

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record ShowActionS2CPayload(int actionId) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ShowActionS2CPayload> TYPE =
            new CustomPacketPayload.Type<>(
                Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "gun_show_action")
            );

        public static final StreamCodec<RegistryFriendlyByteBuf, ShowActionS2CPayload> STREAM_CODEC =
            StreamCodec.of(
                (buf, payload) -> buf.writeInt(payload.actionId),
                buf -> new ShowActionS2CPayload(buf.readInt())
            );

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record KeyPressedC2SPayload() implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<KeyPressedC2SPayload> TYPE =
            new CustomPacketPayload.Type<>(
                Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "gun_key_pressed")
            );

        public static final StreamCodec<RegistryFriendlyByteBuf, KeyPressedC2SPayload> STREAM_CODEC =
            StreamCodec.of(
                (buf, payload) -> {},
                buf -> new KeyPressedC2SPayload()
            );

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private static final String[] ACTION_TEXTS = {
        "Get into Harvard",
        "Become a neurologist at 9",
        "Be a billionaire at 10",
        "Get Panda Express"
    };

    private static final List<ResourceKey<DamageType>> ACTION_DAMAGE_TYPES = List.of(
        ModDamageTypes.STOOBID,
        ModDamageTypes.COUSIN_DID_BETTER,
        ModDamageTypes.DISAPPOINTMENT,
        ModDamageTypes.FORGOT_ASIAN
    );

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(ShowButtonS2CPayload.TYPE, ShowButtonS2CPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ShowActionS2CPayload.TYPE, ShowActionS2CPayload.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(KeyPressedC2SPayload.TYPE, KeyPressedC2SPayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(KeyPressedC2SPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            if (!(player instanceof IGunPlayer gp)) return;
            if (gp.emotionalDamage$getGunState() != GunChallengeState.BUTTON) return;

            gp.emotionalDamage$setGunStep(gp.emotionalDamage$getGunStep() + 1);
            gp.emotionalDamage$setGunTimer(0);

            if (gp.emotionalDamage$getGunStep() >= 3) {
                int actionId = player.getRandom().nextInt(4);
                gp.emotionalDamage$setGunState(GunChallengeState.ACTION);
                gp.emotionalDamage$setGunAction(actionId);
                gp.emotionalDamage$setGunTimer(20);
                ServerPlayNetworking.send(player, new ShowActionS2CPayload(actionId));
            } else {
                char[] buttons = gp.emotionalDamage$getGunButtons();
                char next = buttons[gp.emotionalDamage$getGunStep()];
                ServerPlayNetworking.send(player, new ShowButtonS2CPayload(next, (int) next));
            }
        });
    }

    public static void startChallenge(ServerPlayer player, char[] buttons) {
        if (!(player instanceof IGunPlayer gp)) return;
        gp.emotionalDamage$setGunState(GunChallengeState.BUTTON);
        gp.emotionalDamage$setGunButtons(buttons);
        gp.emotionalDamage$setGunStep(0);
        gp.emotionalDamage$setGunTimer(0);

        ServerPlayNetworking.send(player, new ShowButtonS2CPayload(buttons[0], (int) buttons[0]));
    }

    public static String getActionText(int actionId) {
        return ACTION_TEXTS[actionId];
    }

    public static ResourceKey<DamageType> getActionDamageType(int actionId) {
        return ACTION_DAMAGE_TYPES.get(actionId);
    }
}
