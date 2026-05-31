package online.inklingyoshi.asian.difficulty;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Difficulty;
import java.util.Optional;

public enum ModDifficulty implements StringRepresentable {
    NORMAL(0, "normal"),
    ASIAN_LOWER(1, "asian"),
    ASIAN_UPPER(2, "ASIAN");

    public static final Codec<ModDifficulty> CODEC = StringRepresentable.fromEnum(ModDifficulty::values);
    private static final ModDifficulty[] BY_LEVEL = values();

    private final int level;
    private final String name;

    ModDifficulty(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public Component getDisplayName() {
        return Component.literal(name);
    }

    public boolean isAtLeast(ModDifficulty other) {
        return this.level >= other.level;
    }

    public Difficulty getForcedVanillaDifficulty() {
        return this == NORMAL ? null : Difficulty.HARD;
    }

    public static ModDifficulty byName(String name) {
        for (ModDifficulty d : values()) {
            if (d.name.equals(name)) return d;
        }
        return NORMAL;
    }

    public static ModDifficulty byLevel(int level) {
        if (level < 0 || level >= BY_LEVEL.length) return NORMAL;
        return BY_LEVEL[level];
    }
}
