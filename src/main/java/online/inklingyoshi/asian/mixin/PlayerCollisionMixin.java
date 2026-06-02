package online.inklingyoshi.asian.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import online.inklingyoshi.asian.attack.ModDamageTypes;
import online.inklingyoshi.asian.util.DifficultyHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerCollisionMixin {

    @Unique
    private boolean emotionalDamage$wasColliding;

    @Inject(method = "tick", at = @At("HEAD"))
    private void checkHorizontalCollision(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (!(self instanceof ServerPlayer player)) return;

        if (!DifficultyHelper.isAsianOrHigher(((ServerLevel) player.level()).getServer())) return;

        boolean colliding = player.horizontalCollision;
        if (!colliding || emotionalDamage$wasColliding) {
            emotionalDamage$wasColliding = colliding;
            return;
        }
        emotionalDamage$wasColliding = true;

        Vec3 delta = player.getDeltaMovement();
        int dx = delta.x > 0 ? 1 : (delta.x < 0 ? -1 : 0);
        int dz = delta.z > 0 ? 1 : (delta.z < 0 ? -1 : 0);
        Direction dir = Direction.getNearest(dx, 0, dz, Direction.NORTH);
        BlockPos adjacentPos = player.blockPosition().relative(dir);
        ServerLevel level = (ServerLevel) player.level();
        BlockState state = level.getBlockState(adjacentPos);

        if (state.isAir()) {
            for (Direction alt : Direction.values()) {
                if (alt.getAxis().isHorizontal()) {
                    BlockPos altPos = player.blockPosition().relative(alt);
                    BlockState altState = level.getBlockState(altPos);
                    if (!altState.isAir()) {
                        state = altState;
                        break;
                    }
                }
            }
        }

        player.hurt(ModDamageTypes.blockSource(level, player, state.getBlock()), 4.0f);
    }
}
