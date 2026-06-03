package online.inklingyoshi.asian.attack;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class SlipperHelper {
    public static final String XP_KEY = "slipperXp";

    private SlipperHelper() {}

    public static int getXp(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            CompoundTag tag = data.copyTag();
            return tag.getIntOr(XP_KEY, 0);
        }
        return 0;
    }

    public static void setXp(ItemStack stack, int xp) {
        CompoundTag tag;
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            tag = data.copyTag();
        } else {
            tag = new CompoundTag();
        }
        tag.putInt(XP_KEY, xp);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static void addXp(ItemStack stack, int amount) {
        setXp(stack, getXp(stack) + amount);
    }

    public static int getDamageForXp(int xp) {
        if (xp >= 600) return 7;
        if (xp >= 300) return 5;
        if (xp >= 200) return 4;
        if (xp >= 150) return 3;
        if (xp >= 50) return 2;
        return 1;
    }

    public static boolean hasLoyalty(int xp) {
        return xp >= 600;
    }
}
