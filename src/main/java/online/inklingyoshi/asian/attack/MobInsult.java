package online.inklingyoshi.asian.attack;

import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;

public record MobInsult(
    String text,
    SoundEvent sound,
    int soundDurationTicks,
    float range,
    InsultCriteria criteria,
    ResourceKey<DamageType> damageType
) {}
