package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class SafeGuardMixin {

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void fixBrokenHealth(ValueInput input, CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (Float.isNaN(self.getHealth()) || Float.isInfinite(self.getHealth())) {
            self.setHealth(self.getMaxHealth());
        }
        if (Float.isNaN(self.getAbsorptionAmount()) || Float.isInfinite(self.getAbsorptionAmount())) {
            self.setAbsorptionAmount(0);
        }
    }
}
