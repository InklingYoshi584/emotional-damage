package online.inklingyoshi.asian.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Steal taunt triggers living on Block: breaking a loot chest, and placing a
 * hopper directly under a loot chest.
 */
@Mixin(Block.class)
public class StealTauntBlockMixin {

    @Inject(method = "playerWillDestroy", at = @At("HEAD"))
    private void onChestBroken(Level level, BlockPos pos, BlockState state, Player player, CallbackInfo ci) {
        if (level.isClientSide()) return;
        if (!(state.getBlock() instanceof ChestBlock)) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (!StealTauntMixin.hasLoot(level, pos)) return;
        StealTauntMixin.triggerTaunt((ServerLevel) level, serverPlayer);
    }

    @Inject(method = "setPlacedBy", at = @At("HEAD"))
    private void onHopperPlaced(Level level, BlockPos pos, BlockState state, LivingEntity placer,
            ItemStack stack, CallbackInfo ci) {
        if (level.isClientSide()) return;
        if (!(state.getBlock() instanceof HopperBlock)) return;
        if (!(placer instanceof ServerPlayer serverPlayer)) return;
        BlockPos chestPos = pos.above();
        if (!(level.getBlockState(chestPos).getBlock() instanceof ChestBlock)) return;
        if (!StealTauntMixin.hasLoot(level, chestPos)) return;
        StealTauntMixin.triggerTaunt((ServerLevel) level, serverPlayer);
    }
}
