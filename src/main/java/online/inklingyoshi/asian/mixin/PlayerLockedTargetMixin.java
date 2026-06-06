package online.inklingyoshi.asian.mixin;

import java.util.UUID;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import online.inklingyoshi.asian.attack.IPlayerLockedTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class PlayerLockedTargetMixin implements IPlayerLockedTarget {

    @Unique
    private UUID emotionalDamage$lockedTarget;

    @Override
    public UUID emotionalDamage$getLockedTarget() {
        return emotionalDamage$lockedTarget;
    }

    @Override
    public void emotionalDamage$setLockedTarget(UUID uuid) {
        emotionalDamage$lockedTarget = uuid;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void updateLockedTarget(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        HitResult hit = ProjectileUtil.getHitResultOnViewVector(
            player,
            e -> e instanceof LivingEntity && e != player,
            64.0
        );

        if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity target) {
            emotionalDamage$lockedTarget = target.getUUID();
        } else {
            emotionalDamage$lockedTarget = null;
        }
    }
}
