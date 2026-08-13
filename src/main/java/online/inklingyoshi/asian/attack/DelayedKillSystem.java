package online.inklingyoshi.asian.attack;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DelayedKillSystem {
    private DelayedKillSystem() {}

    private static final Map<UUID, ScheduledKill> SCHEDULED = new HashMap<>();

    public record ScheduledKill(long deathTime, ResourceKey<DamageType> damageKey) {}

    public static void schedule(ServerPlayer player, ResourceKey<DamageType> damageKey, int delayTicks) {
        long deathTime = player.level().getLevelData().getGameTime() + delayTicks;
        SCHEDULED.put(player.getUUID(), new ScheduledKill(deathTime, damageKey));
    }

    public static void tick(ServerPlayer player) {
        ScheduledKill kill = SCHEDULED.get(player.getUUID());
        if (kill == null) return;
        if (player.level().getLevelData().getGameTime() < kill.deathTime()) return;

        SCHEDULED.remove(player.getUUID());
        player.hurt(ModDamageTypes.source((net.minecraft.server.level.ServerLevel) player.level(), player, kill.damageKey()), 10000.0f);
    }
}
