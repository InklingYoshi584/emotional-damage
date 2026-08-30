package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import online.inklingyoshi.asian.gamerule.ModGameRules;
import net.minecraft.world.phys.Vec3;
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
    private static final double MAX_HORIZONTAL_DISTANCE = 8.0;

    private static final int LEAP_COOLDOWN = 60;
    private static final double LEAP_HORIZONTAL_PUSH = 0.7;
    private static final double LEAP_VERTICAL_BOOST = 0.55;

    @Unique
    private int emotionalDamage$jumpCooldown;

    @Unique
    private int emotionalDamage$fallImmunityTicks;

    @Unique
    private double emotionalDamage$lastTargetDistance = Double.MAX_VALUE;

    @Unique
    private int emotionalDamage$noProgressTicks;

    @Unique
    private int emotionalDamage$leapCooldown;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tryHighJump(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Mob mob)) return;
        if (self.level().isClientSide()) return;
        if (!ModGameRules.enabled(((ServerLevel) self.level()).getServer(), ModGameRules.HIGH_JUMP)) return;
        if (emotionalDamage$jumpCooldown > 0) emotionalDamage$jumpCooldown--;
        if (emotionalDamage$fallImmunityTicks > 0) emotionalDamage$fallImmunityTicks--;
        if (emotionalDamage$leapCooldown > 0) emotionalDamage$leapCooldown--;

        LivingEntity target = mob.getTarget();
        if (!(target instanceof Player player)) return;
        if (!self.onGround()) return;
        if (emotionalDamage$jumpCooldown > 0) return;

        double height = player.getY() - self.getY();

        double dx = player.getX() - self.getX();
        double dz = player.getZ() - self.getZ();
        double horiz = Math.sqrt(dx * dx + dz * dz);

        if (horiz > MAX_HORIZONTAL_DISTANCE) {
            emotionalDamage$lastTargetDistance = Double.MAX_VALUE;
            emotionalDamage$noProgressTicks = 0;
            return;
        }

        // Periodic lunge toward the player, independent of the stall detector.
        if (emotionalDamage$leapCooldown == 0) {
            Vec3 motion = self.getDeltaMovement();
            double mx = horiz > 0 ? dx / horiz * LEAP_HORIZONTAL_PUSH : 0.0;
            double mz = horiz > 0 ? dz / horiz * LEAP_HORIZONTAL_PUSH : 0.0;
            mob.setDeltaMovement(motion.add(mx, LEAP_VERTICAL_BOOST, mz));
            emotionalDamage$leapCooldown = LEAP_COOLDOWN;
        }

        if (horiz < emotionalDamage$lastTargetDistance - 0.1) {
            emotionalDamage$lastTargetDistance = horiz;
            emotionalDamage$noProgressTicks = 0;
        } else {
            emotionalDamage$noProgressTicks++;
        }

        boolean unreachable = emotionalDamage$noProgressTicks >= 60;
        if (!unreachable) return;

        if (height > MIN_JUMP_HEIGHT) {
            double capped = Math.min(height, MAX_JUMP_HEIGHT);
            double vy = Math.sqrt(2.0 * GRAVITY * capped);

            Vec3 motion = self.getDeltaMovement();
            double push = 0.15;
            double mx = horiz > 0 ? dx / horiz * push : 0.0;
            double mz = horiz > 0 ? dz / horiz * push : 0.0;

            mob.setDeltaMovement(motion.add(mx, vy, mz));
            emotionalDamage$fallImmunityTicks = (int) Math.ceil(2.0 * vy / GRAVITY) + 5;
        } else {
            // Player at the same level or below: drag them to the mob instead of leaping.
            player.teleportTo(self.getX(), self.getY(), self.getZ());
            player.fallDistance = 0;
        }
        emotionalDamage$jumpCooldown = JUMP_COOLDOWN;
        emotionalDamage$lastTargetDistance = Double.MAX_VALUE;
        emotionalDamage$noProgressTicks = 0;
    }

    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    private void ignoreHighJumpFallDamage(double fallDistance, float damageMultiplier, DamageSource source,
            CallbackInfoReturnable<Boolean> cir) {
        if (emotionalDamage$fallImmunityTicks > 0) {
            cir.setReturnValue(false);
        }
    }
}
