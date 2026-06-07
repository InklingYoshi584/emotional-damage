package online.inklingyoshi.asian.mixin;

import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import online.inklingyoshi.asian.attack.IHomingProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public class HomingGravityMixin {

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;applyGravity()V"), cancellable = true)
    private void cancelGravityForHoming(CallbackInfo ci) {
        if (this instanceof IHomingProjectile homing && homing.emotionalDamage$isHoming()) {
            ci.cancel();
        }
    }
}
