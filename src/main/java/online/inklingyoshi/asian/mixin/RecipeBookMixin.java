package online.inklingyoshi.asian.mixin;

import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
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
public class RecipeBookMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleRecipeBookChangeSettingsPacket", at = @At("HEAD"))
    private void onRecipeBookToggle(ServerboundRecipeBookChangeSettingsPacket packet, CallbackInfo ci) {
        ServerPlayer sp = player;
        if (sp != null) {
            sp.hurt(ModDamageTypes.simpleSource((ServerLevel) sp.level(), ModDamageTypes.SUCKS_AT_GAME), 10000.0f);
        }
    }
}
