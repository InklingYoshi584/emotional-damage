package online.inklingyoshi.asian.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import online.inklingyoshi.asian.attack.IPlayerStatsTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerAttackMixin {

    @Shadow
    public abstract float getAttackStrengthScale(float partialTick);

    @Inject(method = "attack", at = @At("HEAD"))
    private void trackAttackCharge(Entity target, CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (!(self instanceof IPlayerStatsTracker tracker)) {
            return;
        }
        float strength = getAttackStrengthScale(0.5f);
        if (strength < 1.0f) {
            tracker.emotionalDamage$setMissedFullCharge(true);
        }
    }
}
