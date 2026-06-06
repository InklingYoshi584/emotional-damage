package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import online.inklingyoshi.asian.attack.ModDamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class TotemMixin {

    @Unique
    private int emotionalDamage$totemTimer = -1;

    @Inject(method = "checkTotemDeathProtection", at = @At("RETURN"))
    private void onTotemActivate(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;
        if ((Object) this instanceof ServerPlayer) {
            emotionalDamage$totemTimer = 20;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickTotemKill(CallbackInfo ci) {
        if (emotionalDamage$totemTimer < 0) return;
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof ServerPlayer sp)) {
            emotionalDamage$totemTimer = -1;
            return;
        }
        emotionalDamage$totemTimer--;
        if (emotionalDamage$totemTimer == 0) {
            sp.hurt(ModDamageTypes.simpleSource(sp.level(), ModDamageTypes.FAILURE_SKILL_ISSUE), 1.0E12f);
            emotionalDamage$totemTimer = -1;
        }
    }
}
