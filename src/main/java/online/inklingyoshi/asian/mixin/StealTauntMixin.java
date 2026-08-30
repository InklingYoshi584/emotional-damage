package online.inklingyoshi.asian.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import online.inklingyoshi.asian.attack.DelayedKillSystem;
import online.inklingyoshi.asian.attack.ModDamageTypes;
import online.inklingyoshi.asian.gamerule.ModGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Steal taunt: opening a chest that contains loot makes nearby Enemy mobs
 * roast the player and the player dies of emotional damage 1 second later.
 * Also hosts the shared trigger logic used by the other steal-taunt mixins.
 */
@Mixin(ChestBlock.class)
public class StealTauntMixin {

    @Unique
    private static final String[] TAUNTS = {
        "Stealing is for pussies",
        "Only losers steal",
        "Look at this broke dude trying to steal"
    };

    @Unique
    private static final double TAUNT_RADIUS = 15.0;

    @Unique
    private static final int KILL_DELAY_TICKS = 20;

    @Inject(method = "useWithoutItem", at = @At("HEAD"))
    private void onChestOpen(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hit, CallbackInfo ci) {
        if (level.isClientSide()) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (!hasLoot(level, pos)) return;
        triggerTaunt((ServerLevel) level, serverPlayer);
    }

    @Unique
    public static boolean hasLoot(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof ChestBlockEntity chest)) return false;
        for (int i = 0; i < chest.getContainerSize(); i++) {
            if (!chest.getItem(i).isEmpty()) return true;
        }
        return false;
    }

    @Unique
    public static void triggerTaunt(ServerLevel level, ServerPlayer player) {
        if (!ModGameRules.enabled(level.getServer(), ModGameRules.STEAL_TAUNT)) return;

        String taunt = TAUNTS[level.getRandom().nextInt(TAUNTS.length)];
        List<Mob> mobs = level.getEntitiesOfClass(Mob.class,
            player.getBoundingBox().inflate(TAUNT_RADIUS),
            mob -> mob instanceof Enemy && mob.distanceToSqr(player) <= TAUNT_RADIUS * TAUNT_RADIUS);

        for (Mob mob : mobs) {
            String chatMsg = "[" + mob.getName().getString() + "] " + taunt;
            Component message = Component.literal(chatMsg).withStyle(ChatFormatting.RED);
            player.sendSystemMessage(message);
        }

        DelayedKillSystem.schedule(player, ModDamageTypes.EMOTIONAL_DAMAGE, KILL_DELAY_TICKS);
    }
}
