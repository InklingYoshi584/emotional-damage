package online.inklingyoshi.asian.attack;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.util.List;

/**
 * Steal taunt: opening/breaking a loot chest, placing a hopper under one, or
 * angering a piglin makes nearby Enemy mobs roast the player, who then dies
 * of emotional damage 1 second later. Always on (no gamerule gate).
 */
public final class StealTaunt {
    private StealTaunt() {}

    private static final String[] TAUNTS = {
        "Stealing is for pussies",
        "Only losers steal",
        "Look at this broke dude trying to steal"
    };

    private static final double TAUNT_RADIUS = 15.0;
    private static final int KILL_DELAY_TICKS = 20;

    public static boolean hasLoot(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof ChestBlockEntity chest)) return false;
        for (int i = 0; i < chest.getContainerSize(); i++) {
            if (!chest.getItem(i).isEmpty()) return true;
        }
        return false;
    }

    public static void triggerTaunt(ServerLevel level, ServerPlayer player) {
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
