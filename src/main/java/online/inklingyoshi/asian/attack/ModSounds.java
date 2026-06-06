package online.inklingyoshi.asian.attack;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import online.inklingyoshi.asian.EmotionalDamage;

public final class ModSounds {
    private ModSounds() {}

    public static final SoundEvent WHY_YOU_SO_FAT = register("why_you_so_fat");
    public static final SoundEvent MINIMUM_WAGE = register("minimum_wage");
    public static final SoundEvent WHEN_GETTING_JOB = register("when_getting_job");
    public static final SoundEvent YOU_SO_SKINNY = register("you_so_skinny");
    public static final SoundEvent THEY_SENT_YOU = register("they_sent_you");
    public static final SoundEvent STOOPID_NO_STONE_TOOLS = register("stoopid_no_stone_tools");
    public static final SoundEvent MISSED_FULL_CHARGE = register("missed_full_charge");

    public static void register() {
    }

    private static SoundEvent register(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }
}
