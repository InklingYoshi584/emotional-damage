package online.inklingyoshi.asian.attack;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface InsultCriteria {
    boolean test(Mob mob, Player player, ServerLevel level);
}
