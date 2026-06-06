package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import online.inklingyoshi.asian.attack.ModDamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class BookSignMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleEditBook", at = @At("TAIL"))
    private void onSignBook(net.minecraft.network.protocol.game.ServerboundEditBookPacket packet, CallbackInfo ci) {
        ServerPlayer sp = player;
        if (sp != null) {
            sp.hurt(ModDamageTypes.simpleSource((ServerLevel) sp.level(), ModDamageTypes.ART_DEGREE), 10000.0f);
        }
    }
}
