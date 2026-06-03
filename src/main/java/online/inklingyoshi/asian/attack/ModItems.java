package online.inklingyoshi.asian.attack;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import online.inklingyoshi.asian.EmotionalDamage;

public final class ModItems {
    private ModItems() {}

    public static final SlipperItem SLIPPER = (SlipperItem) register(
        "slipper",
        new SlipperItem(new Item.Properties()
            .attributes(SlipperItem.createAttributes())
            .stacksTo(1))
    );

    private static Item register(String name, Item item) {
        return net.minecraft.core.Registry.register(
            BuiltInRegistries.ITEM,
            Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, name),
            item
        );
    }

    public static void register() {
    }
}
