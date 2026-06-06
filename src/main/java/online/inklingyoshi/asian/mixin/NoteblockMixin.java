package online.inklingyoshi.asian.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import online.inklingyoshi.asian.attack.ModDamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoteBlock.class)
public class NoteblockMixin {

    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttack(BlockState state, Level level, BlockPos pos, Player player, CallbackInfo ci) {
        if (player instanceof ServerPlayer sp) {
            sp.hurt(ModDamageTypes.simpleSource((ServerLevel) sp.level(), ModDamageTypes.MUSICIAN), 10000.0f);
        }
    }

    @Inject(method = "useWithoutItem", at = @At("HEAD"))
    private void onUse(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit,
            CallbackInfoReturnable<InteractionResult> cir) {
        if (player instanceof ServerPlayer sp) {
            sp.hurt(ModDamageTypes.simpleSource((ServerLevel) sp.level(), ModDamageTypes.MUSICIAN), 10000.0f);
        }
    }
}
