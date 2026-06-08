package online.inklingyoshi.asian.attack;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import online.inklingyoshi.asian.EmotionalDamage;
import online.inklingyoshi.asian.mixin.ItemPropertiesAccessor;

public final class ModItems {
    private ModItems() {}

    public static final SlipperItem SLIPPER;
    public static final GunItem GUN;

    static {
        ResourceKey<Item> slipperKey = ResourceKey.create(
            BuiltInRegistries.ITEM.key(),
            Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "slipper")
        );
        Item.Properties slipperProps = new Item.Properties()
            .attributes(SlipperItem.createAttributes())
            .stacksTo(1);
        ((ItemPropertiesAccessor) (Object) slipperProps).setId(slipperKey);
        SLIPPER = new SlipperItem(slipperProps);

        net.minecraft.core.Registry.register(BuiltInRegistries.ITEM, slipperKey, SLIPPER);

        ResourceKey<Item> gunKey = ResourceKey.create(
            BuiltInRegistries.ITEM.key(),
            Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "gun")
        );
        Item.Properties gunProps = new Item.Properties().stacksTo(1);
        ((ItemPropertiesAccessor) (Object) gunProps).setId(gunKey);
        GUN = new GunItem(gunProps);

        net.minecraft.core.Registry.register(BuiltInRegistries.ITEM, gunKey, GUN);
    }

    public static void register() {
    }
}