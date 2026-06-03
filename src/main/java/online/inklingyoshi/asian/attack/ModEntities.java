package online.inklingyoshi.asian.attack;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import online.inklingyoshi.asian.EmotionalDamage;

public final class ModEntities {
    private ModEntities() {}

    public static final EntityType<ThrownSlipper> THROWN_SLIPPER = EntityType.Builder
        .<ThrownSlipper>of(ThrownSlipper::new, MobCategory.MISC)
        .sized(0.5F, 0.5F)
        .clientTrackingRange(4)
        .updateInterval(20)
        .build(ResourceKey.create(
            BuiltInRegistries.ENTITY_TYPE.key(),
            Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "thrown_slipper")
        ));

    public static final EntityType<ThrownItemProjectile> THROWN_ITEM = EntityType.Builder
        .<ThrownItemProjectile>of(ThrownItemProjectile::new, MobCategory.MISC)
        .sized(0.25F, 0.25F)
        .clientTrackingRange(4)
        .updateInterval(20)
        .build(ResourceKey.create(
            BuiltInRegistries.ENTITY_TYPE.key(),
            Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "thrown_item")
        ));

    public static void register() {
        Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "thrown_slipper"),
            THROWN_SLIPPER
        );
        Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "thrown_item"),
            THROWN_ITEM
        );
    }
}
