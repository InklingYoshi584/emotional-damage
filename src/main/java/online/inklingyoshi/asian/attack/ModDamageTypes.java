package online.inklingyoshi.asian.attack;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import online.inklingyoshi.asian.EmotionalDamage;

public final class ModDamageTypes {
    private ModDamageTypes() {}

    public static final ResourceKey<DamageType> EMOTIONAL_DAMAGE = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "emotional_damage")
    );

    public static final ResourceKey<DamageType> TOO_FAT = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "too_fat")
    );

    public static final ResourceKey<DamageType> MINIMUM_WAGE = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "minimum_wage")
    );

    public static final ResourceKey<DamageType> UNEMPLOYED = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "unemployed")
    );

    public static final ResourceKey<DamageType> TOO_SKINNY = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "too_skinny")
    );

    public static final ResourceKey<DamageType> TOO_STUPID = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "too_stupid")
    );

    public static final ResourceKey<DamageType> SUCKS_AT_GAME = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "sucks_at_game")
    );

    public static DamageSource source(ServerLevel level, Entity attacker, ResourceKey<DamageType> key) {
        return new DamageSource(
            level.registryAccess()
                .lookupOrThrow(Registries.DAMAGE_TYPE)
                .getOrThrow(key),
            attacker,
            attacker
        );
    }
}
