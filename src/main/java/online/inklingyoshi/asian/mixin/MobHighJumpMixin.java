package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import online.inklingyoshi.asian.util.DifficultyHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MobHighJumpMixin {

    private static final double MAX_JUMP_HEIGHT = 10.0;
    private static final double MIN_JUMP_HEIGHT = 2.0;
    private static final double GRAVITY = 0.08;
    private static final int JUMP_COOLDOWN = 20;

    @Unique
    private int emotionalDamage$jumpCooldown;

    @Unique
    private int emotionalDamage$fallImmunityTicks;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tryHighJump(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Mob mob)) return;
        if (!(mob instanceof Enemy)) return;
        if (self.level().isClientSide()) return;
        if (!DifficultyHelper.isAsianOrHigher(((ServerLevel) self.level()).getServer())) return;

        if (emotionalDamage$jumpCooldown > 0) emotionalDamage$jumpCooldown--;
        if (emotionalDamage$fallImmunityTicks > 0) emotionalDamage$fallImmunityTicks--;

        LivingEntity target = mob.getTarget();
        if (!(target instanceof Player player)) return;
        if (!self.onGround()) return;
        if (emotionalDamage$jumpCooldown > 0) return;

        boolean unreachable = mob.getNavigation().isStuck()
            || (mob.getNavigation().getPath() == null && !mob.getNavigation().isInProgress());
        if (!unreachable) return;

        double height = player.getY() - self.getY();
        if (height <= MIN_JUMP_HEIGHT) return;

        double capped = Math.min(height, MAX_JUMP_HEIGHT);
        double vy = Math.sqrt(2.0 * GRAVITY * capped);

        Vec3 motion = self.getDeltaMovement();
        double dx = player.getX() - self.getX();
        double dz = player.getZ() - self.getZ();
        double horiz = Math.sqrt(dx * dx + dz * dz);
        double push = 0.15;
        double mx = horiz > 0 ? dx / horiz * push : 0.0;
        double mz = horiz > 0 ? dz / horiz * push : 0.0;

        mob.setDeltaMovement(motion.add(mx, vy, mz));
        emotionalDamage$jumpCooldown = JUMP_COOLDOWN;
        emotionalDamage$fallImmunityTicks = (int) Math.ceil(2.0 * vy / GRAVITY) + 5;
    }

    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    private void ignoreHighJumpFallDamage(double fallDistance, float damageMultiplier, DamageSource source,
            CallbackInfoReturnable<Boolean> cir) {
        if (emotionalDamage$fallImmunityTicks > 0) {
            cir.setReturnValue(false);
        }
    }
}
