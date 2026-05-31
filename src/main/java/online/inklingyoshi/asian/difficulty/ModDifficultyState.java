package online.inklingyoshi.asian.difficulty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import online.inklingyoshi.asian.EmotionalDamage;

public class ModDifficultyState extends SavedData {
    private static final Identifier ID = Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "difficulty");

    private static final Codec<ModDifficultyState> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("difficulty_level").forGetter(s -> s.difficulty.ordinal())
        ).apply(instance, ModDifficultyState::new)
    );

    public static final SavedDataType<ModDifficultyState> TYPE = new SavedDataType<>(
        ID, ModDifficultyState::new, CODEC, DataFixTypes.LEVEL
    );

    private ModDifficulty difficulty;

    public ModDifficultyState() {
        this.difficulty = ModDifficulty.NORMAL;
    }

    public ModDifficultyState(int difficultyOrdinal) {
        this.difficulty = ModDifficulty.values()[difficultyOrdinal % ModDifficulty.values().length];
    }

    public static ModDifficultyState getOrCreate(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    public ModDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(ModDifficulty difficulty) {
        this.difficulty = difficulty;
        setDirty();
    }
}
