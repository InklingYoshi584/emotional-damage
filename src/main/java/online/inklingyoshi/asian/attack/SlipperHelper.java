package online.inklingyoshi.asian.attack;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SlipperHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger("emotional-damage");

    private SlipperHelper() {}

    public static int getXp(ItemStack stack) {
        if (stack == null) return 0;
        return stack.getOrDefault(DataComponents.REPAIR_COST, 0);
    }

    public static void setXp(ItemStack stack, int xp) {
        if (stack == null) return;
        stack.set(DataComponents.REPAIR_COST, xp);
        LOGGER.info("Slipper XP set to {}", xp);
    }

    public static void addXp(ItemStack stack, int amount) {
        if (stack == null) return;
        int old = getXp(stack);
        setXp(stack, old + amount);
        LOGGER.info("Slipper XP: {} -> {}", old, old + amount);
    }

    public static int getDamageForXp(int xp) {
        if (xp >= 1000) return 8;
        if (xp >= 800) return 8;
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

    public static boolean hasHoming(int xp) {
        return xp >= 800;
    }

    public static boolean canThrowItems(int xp) {
        return xp >= 1000;
    }
}
