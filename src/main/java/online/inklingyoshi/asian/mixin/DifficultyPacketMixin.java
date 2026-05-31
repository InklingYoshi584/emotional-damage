package online.inklingyoshi.asian.mixin;

import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import online.inklingyoshi.asian.difficulty.ModDifficulty;
import online.inklingyoshi.asian.difficulty.ModDifficultyState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class DifficultyPacketMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleChangeDifficulty", at = @At("HEAD"))
    private void onHandleChangeDifficulty(ServerboundChangeDifficultyPacket packet, CallbackInfo ci) {
        MinecraftServer server = player.level().getServer();
        if (server == null) return;
        ModDifficultyState state = ModDifficultyState.getOrCreate(server);
        if (state.getDifficulty() != ModDifficulty.NORMAL) {
            state.setDifficulty(ModDifficulty.NORMAL);
        }
    }
}
