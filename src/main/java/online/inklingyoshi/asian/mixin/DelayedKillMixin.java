package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerPlayer;
import online.inklingyoshi.asian.attack.DelayedKillSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class DelayedKillMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        DelayedKillSystem.tick(self);
    }
}
