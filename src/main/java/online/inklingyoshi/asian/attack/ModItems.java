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

    static {
        ResourceKey<Item> key = ResourceKey.create(
            BuiltInRegistries.ITEM.key(),
            Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "slipper")
        );
        Item.Properties props = new Item.Properties()
            .attributes(SlipperItem.createAttributes())
            .stacksTo(1);
        ((ItemPropertiesAccessor) (Object) props).setId(key);
        SLIPPER = new SlipperItem(props);

        net.minecraft.core.Registry.register(BuiltInRegistries.ITEM, key, SLIPPER);
    }

    public static void register() {
    }
}