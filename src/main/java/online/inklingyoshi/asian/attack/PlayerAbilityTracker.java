package online.inklingyoshi.asian.attack;

import net.minecraft.world.entity.player.Player;

public final class PlayerAbilityTracker {
    private PlayerAbilityTracker() {}

    public static boolean hasThrowUnlock(Player player) {
        return player instanceof IPlayerAbilityTracker tracker && tracker.emotionalDamage$hasThrowUnlock();
    }

    public static void setThrowUnlock(Player player, boolean value) {
        if (player instanceof IPlayerAbilityTracker tracker) {
            tracker.emotionalDamage$setThrowUnlock(value);
        }
    }
}
