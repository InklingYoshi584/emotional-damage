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

    public static DamageSource emotionalDamageSource(ServerLevel level, Entity attacker) {
        return new DamageSource(
            level.registryAccess()
                .lookupOrThrow(Registries.DAMAGE_TYPE)
                .getOrThrow(EMOTIONAL_DAMAGE),
            attacker,
            attacker
        );
    }
}
