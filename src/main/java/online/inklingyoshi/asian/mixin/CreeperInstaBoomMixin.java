package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import online.inklingyoshi.asian.gamerule.ModGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Creeper.class)
public abstract class CreeperInstaBoomMixin {

    private static final double EXPLOSION_RANGE_SQ = 3.0 * 3.0;

    @Unique
    private int emotionalDamage$fuseTicks;

    @Invoker("explodeCreeper")
    protected abstract void emotionalDamage$invokeExplodeCreeper();

    @Inject(method = "tick", at = @At("TAIL"))
    private void instaFuse(CallbackInfo ci) {
        Creeper self = (Creeper) (Object) this;
        if (self.level().isClientSide()) return;
        if (!ModGameRules.enabled(((ServerLevel) self.level()).getServer(), ModGameRules.CREEPER_INSTA_BOOM)) return;
        if (self.isRemoved()) return;

        if (emotionalDamage$fuseTicks > 0) {
            emotionalDamage$fuseTicks--;
            if (emotionalDamage$fuseTicks == 0) {
                emotionalDamage$invokeExplodeCreeper();
            }
            return;
        }

        for (Player player : ((ServerLevel) self.level()).players()) {
            if (player.distanceToSqr(self) < EXPLOSION_RANGE_SQ) {
                emotionalDamage$fuseTicks = 5;
                return;
            }
        }
    }

    @Inject(method = "spawnLingeringCloud", at = @At("HEAD"), cancellable = true)
    private void noAreaEffectCloud(CallbackInfo ci) {
        ci.cancel();
    }
}
