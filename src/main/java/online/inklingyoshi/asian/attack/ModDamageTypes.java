package online.inklingyoshi.asian.attack;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import online.inklingyoshi.asian.EmotionalDamage;

public final class ModDamageTypes {
    private ModDamageTypes() {}

    public static final ResourceKey<DamageType> EMOTIONAL_DAMAGE = key("emotional_damage");
    public static final ResourceKey<DamageType> TOO_FAT = key("too_fat");
    public static final ResourceKey<DamageType> MINIMUM_WAGE = key("minimum_wage");
    public static final ResourceKey<DamageType> UNEMPLOYED = key("unemployed");
    public static final ResourceKey<DamageType> TOO_SKINNY = key("too_skinny");
    public static final ResourceKey<DamageType> TOO_STUPID = key("too_stupid");
    public static final ResourceKey<DamageType> SUCKS_AT_GAME = key("sucks_at_game");
    public static final ResourceKey<DamageType> HIT_BY_BLOCK = key("hit_by_block");
    public static final ResourceKey<DamageType> POISON_GRASS = key("poison_grass");
    public static final ResourceKey<DamageType> HIT_BY_LEAF = key("hit_by_leaf");
    public static final ResourceKey<DamageType> SOCIAL_ANXIETY = key("social_anxiety");
    public static final ResourceKey<DamageType> ART_DEGREE = key("art_degree");
    public static final ResourceKey<DamageType> MUSICIAN = key("musician");
    public static final ResourceKey<DamageType> FAILURE_SKILL_ISSUE = key("failure_skill_issue");
    public static final ResourceKey<DamageType> TOO_LAZY = key("too_lazy");
    public static final ResourceKey<DamageType> CHEATER = key("cheater");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(
            Registries.DAMAGE_TYPE,
            Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, name)
        );
    }

    public static DamageSource source(ServerLevel level, Entity attacker, ResourceKey<DamageType> key) {
        return new DamageSource(getHolder(level, key), attacker, attacker);
    }

    public static DamageSource blockSource(ServerLevel level, LivingEntity victim, Block block) {
        Holder<DamageType> holder = getHolder(level, HIT_BY_BLOCK);
        return new BlockDamageSource(holder, victim, block.getName());
    }

    public static DamageSource simpleSource(ServerLevel level, ResourceKey<DamageType> key) {
        return new DamageSource(getHolder(level, key));
    }

    private static Holder<DamageType> getHolder(ServerLevel level, ResourceKey<DamageType> key) {
        return level.registryAccess()
            .lookupOrThrow(Registries.DAMAGE_TYPE)
            .getOrThrow(key);
    }
}
