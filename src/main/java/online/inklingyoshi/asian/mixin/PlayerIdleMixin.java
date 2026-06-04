package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import online.inklingyoshi.asian.attack.ModDamageTypes;
import online.inklingyoshi.asian.util.DifficultyHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerIdleMixin {

    @Unique
    private double emotionalDamage$lastX;

    @Unique
    private double emotionalDamage$lastY;

    @Unique
    private double emotionalDamage$lastZ;

    @Unique
    private int emotionalDamage$stationaryTicks;

    private static final int GRACE_TICKS = 60;
    private static final int DAMAGE_INTERVAL = 20;
    private static final float DAMAGE_AMOUNT = 1.0f;

    @Inject(method = "tick", at = @At("HEAD"))
    private void checkIdleDamage(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (!(self instanceof ServerPlayer player)) return;

        if (!DifficultyHelper.isAsianOrHigher(((ServerLevel) player.level()).getServer())) return;

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        if (x == emotionalDamage$lastX && y == emotionalDamage$lastY && z == emotionalDamage$lastZ) {
            emotionalDamage$stationaryTicks++;
        } else {
            emotionalDamage$stationaryTicks = 0;
        }

        emotionalDamage$lastX = x;
        emotionalDamage$lastY = y;
        emotionalDamage$lastZ = z;

        if (emotionalDamage$stationaryTicks > GRACE_TICKS && emotionalDamage$stationaryTicks % DAMAGE_INTERVAL == 0) {
            player.hurt(
                ModDamageTypes.simpleSource(player.level(), ModDamageTypes.TOO_LAZY),
                DAMAGE_AMOUNT
            );
        }
    }
}
