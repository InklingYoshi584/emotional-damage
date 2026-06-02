package online.inklingyoshi.asian.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import online.inklingyoshi.asian.attack.ModDamageTypes;
import online.inklingyoshi.asian.util.DifficultyHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerGameMode.class)
public class PlayerBlockPunchMixin {

    @Shadow
    protected ServerPlayer player;

    @Inject(method = "handleBlockBreakAction", at = @At("HEAD"))
    private void onPunchBlock(BlockPos pos,
            net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action action,
            Direction direction, int i, int j, CallbackInfo ci) {
        if (action != net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
            return;
        }
        if (!DifficultyHelper.isAsianOrHigher(((ServerLevel) player.level()).getServer())) return;

        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.isEmpty()) return;

        ServerLevel level = (ServerLevel) player.level();
        BlockState state = level.getBlockState(pos);
        player.hurt(ModDamageTypes.blockSource(level, player, state.getBlock()), 4.0f);
    }
}
