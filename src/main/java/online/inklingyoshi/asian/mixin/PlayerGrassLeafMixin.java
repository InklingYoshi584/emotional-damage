package online.inklingyoshi.asian.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import online.inklingyoshi.asian.attack.ModDamageTypes;
import online.inklingyoshi.asian.util.DifficultyHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerGrassLeafMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void checkGrassAndLeafDamage(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (!(self instanceof ServerPlayer player)) return;

        if (!DifficultyHelper.isAsianOrHigher(((ServerLevel) player.level()).getServer())) return;

        ServerLevel level = (ServerLevel) player.level();

        BlockPos feetPos = player.blockPosition();
        BlockState feetState = level.getBlockState(feetPos);
        Block feetBlock = feetState.getBlock();

        if (isHarmfulGrass(feetBlock)) {
            if (player.getRandom().nextInt(200) == 0) {
                player.hurt(ModDamageTypes.simpleSource(level, ModDamageTypes.POISON_GRASS), 4.0f);
            }
        }

        for (int y = 1; y <= 5; y++) {
            BlockState aboveState = level.getBlockState(feetPos.above(y));
            if (aboveState.is(BlockTags.LEAVES)) {
                if (player.getRandom().nextInt(200) == 0) {
                    player.hurt(ModDamageTypes.simpleSource(level, ModDamageTypes.HIT_BY_LEAF), 4.0f);
                }
                break;
            }
        }
    }

    private static boolean isHarmfulGrass(Block block) {
        return block == Blocks.SHORT_GRASS
            || block == Blocks.TALL_GRASS
            || block == Blocks.FERN
            || block == Blocks.LARGE_FERN;
    }
}
