package online.inklingyoshi.asian.util;

import net.minecraft.server.MinecraftServer;
import online.inklingyoshi.asian.difficulty.ModDifficulty;
import online.inklingyoshi.asian.difficulty.ModDifficultyState;

public final class DifficultyHelper {
    private DifficultyHelper() {}
    /** Lethal damage amount used for maxed mod damage and the mod's kill-on-hit sources. */
    public static final float MAX_MOD_DAMAGE = 10000.0f;

    public static ModDifficulty getModDifficulty(MinecraftServer server) {
        return ModDifficultyState.getOrCreate(server).getDifficulty();
    }

    public static boolean isAtLeast(MinecraftServer server, ModDifficulty threshold) {
        return getModDifficulty(server).isAtLeast(threshold);
    }

    public static boolean isAsianOrHigher(MinecraftServer server) {
        return isAtLeast(server, ModDifficulty.ASIAN_LOWER);
    }

    public static boolean isASIAN(MinecraftServer server) {
        return isAtLeast(server, ModDifficulty.ASIAN_UPPER);
    }

    /** Mod environmental damage: lethal on ASIAN difficulty, unchanged below. */
    public static float modDamage(MinecraftServer server, float normalAmount) {
        return isASIAN(server) ? MAX_MOD_DAMAGE : normalAmount;
    }
}
