package online.inklingyoshi.asian.util;

import net.minecraft.server.MinecraftServer;
import online.inklingyoshi.asian.difficulty.ModDifficulty;
import online.inklingyoshi.asian.difficulty.ModDifficultyState;

public final class DifficultyHelper {
    private DifficultyHelper() {}

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
}
