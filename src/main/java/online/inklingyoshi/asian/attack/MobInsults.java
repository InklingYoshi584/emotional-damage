package online.inklingyoshi.asian.attack;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import online.inklingyoshi.asian.difficulty.ModDifficulty;
import online.inklingyoshi.asian.util.DifficultyHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MobInsults {
    private MobInsults() {}

    private static final List<MobInsult> BASIC = new ArrayList<>();
    private static final List<MobInsult> EXTENDED = new ArrayList<>();
    public static float MAX_RANGE = 32f;

    public static void register(MobInsult insult) {
        BASIC.add(insult);
        EXTENDED.add(insult);
    }

    public static void registerExtendedOnly(MobInsult insult) {
        EXTENDED.add(insult);
    }

    public static List<MobInsult> getAvailable(ModDifficulty diff) {
        if (diff == ModDifficulty.ASIAN_UPPER) {
            return EXTENDED.isEmpty() ? BASIC : EXTENDED;
        }
        if (diff == ModDifficulty.ASIAN_LOWER) {
            return Collections.unmodifiableList(BASIC);
        }
        return Collections.emptyList();
    }

    static {
        registerDefaultInsults();
    }

    private static void registerDefaultInsults() {
        register(new MobInsult(
            "Why you so fat?",
            null,
            0,
            16f,
            (mob, player, serverLevel) -> {
                if (player instanceof IPlayerFoodTracker tracker) {
                    int wasted = tracker.emotionalDamage$getWastedHunger();
                    ModDifficulty diff = DifficultyHelper.getModDifficulty(serverLevel.getServer());
                    return diff == ModDifficulty.ASIAN_UPPER ? wasted > 0 : wasted > 6;
                }
                return false;
            },
            ModDamageTypes.EMOTIONAL_DAMAGE
        ));

        register(new MobInsult(
            "Smells like minimum wage",
            null,
            0,
            16f,
            (mob, player, serverLevel) -> {
                if (player instanceof IPlayerStatsTracker tracker) {
                    checkEmeraldDayReset(serverLevel, tracker);
                    int emeralds = tracker.emotionalDamage$getDayEmeralds();
                    ModDifficulty diff = DifficultyHelper.getModDifficulty(serverLevel.getServer());
                    return diff == ModDifficulty.ASIAN_UPPER ? emeralds < 10 : emeralds < 5;
                }
                return false;
            },
            ModDamageTypes.EMOTIONAL_DAMAGE
        ));

        register(new MobInsult(
            "When you getting a job?",
            null,
            0,
            16f,
            (mob, player, serverLevel) -> {
                if (player instanceof IPlayerStatsTracker tracker) {
                    checkEmeraldDayReset(serverLevel, tracker);
                    return tracker.emotionalDamage$getDayEmeralds() == 0;
                }
                return false;
            },
            ModDamageTypes.EMOTIONAL_DAMAGE
        ));

        register(new MobInsult(
            "You so skinny.",
            null,
            0,
            16f,
            (mob, player, serverLevel) -> {
                int hunger = player.getFoodData().getFoodLevel();
                ModDifficulty diff = DifficultyHelper.getModDifficulty(serverLevel.getServer());
                return diff == ModDifficulty.ASIAN_UPPER ? hunger < 20 : hunger < 16;
            },
            ModDamageTypes.EMOTIONAL_DAMAGE
        ));

        register(new MobInsult(
            "You so stoopid you don't have stone tools",
            null,
            0,
            16f,
            (mob, player, serverLevel) -> {
                if (serverLevel.getLevelData().getGameTime() < 24000) return false;
                ModDifficulty diff = DifficultyHelper.getModDifficulty(serverLevel.getServer());
                if (diff == ModDifficulty.ASIAN_UPPER) {
                    return !hasFullDiamondPlus(player);
                }
                return !hasStoneOrBetter(player);
            },
            ModDamageTypes.EMOTIONAL_DAMAGE
        ));

        registerExtendedOnly(new MobInsult(
            "Did you just not hit a full-charged attack?",
            null,
            0,
            16f,
            (mob, player, serverLevel) -> {
                if (player instanceof IPlayerStatsTracker tracker) {
                    return tracker.emotionalDamage$hasMissedFullCharge();
                }
                return false;
            },
            ModDamageTypes.EMOTIONAL_DAMAGE
        ));
    }

    private static void checkEmeraldDayReset(net.minecraft.server.level.ServerLevel serverLevel, IPlayerStatsTracker tracker) {
        long currentDay = serverLevel.getLevelData().getGameTime() / 24000;
        if (currentDay != tracker.emotionalDamage$getLastDayChecked()) {
            tracker.emotionalDamage$resetDayEmeralds();
            tracker.emotionalDamage$setLastDayChecked(currentDay);
        }
    }

    private static boolean hasStoneOrBetter(Player player) {
        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            if (isStoneTierOrBetter(stack)) return true;
        }
        return false;
    }

    private static boolean isStoneTierOrBetter(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.STONE_PICKAXE || item == Items.STONE_AXE || item == Items.STONE_SWORD
            || item == Items.STONE_SHOVEL || item == Items.STONE_HOE
            || item == Items.IRON_PICKAXE || item == Items.IRON_AXE || item == Items.IRON_SWORD
            || item == Items.IRON_SHOVEL || item == Items.IRON_HOE
            || item == Items.GOLDEN_PICKAXE || item == Items.GOLDEN_AXE || item == Items.GOLDEN_SWORD
            || item == Items.GOLDEN_SHOVEL || item == Items.GOLDEN_HOE
            || item == Items.DIAMOND_PICKAXE || item == Items.DIAMOND_AXE || item == Items.DIAMOND_SWORD
            || item == Items.DIAMOND_SHOVEL || item == Items.DIAMOND_HOE
            || item == Items.NETHERITE_PICKAXE || item == Items.NETHERITE_AXE || item == Items.NETHERITE_SWORD
            || item == Items.NETHERITE_SHOVEL || item == Items.NETHERITE_HOE;
    }

    private static boolean hasFullDiamondPlus(Player player) {
        return isDiamondOrBetter(player.getItemBySlot(EquipmentSlot.HEAD))
            && isDiamondOrBetter(player.getItemBySlot(EquipmentSlot.CHEST))
            && isDiamondOrBetter(player.getItemBySlot(EquipmentSlot.LEGS))
            && isDiamondOrBetter(player.getItemBySlot(EquipmentSlot.FEET))
            && hasDiamondOrBetterTool(player, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE)
            && hasDiamondOrBetterTool(player, Items.DIAMOND_AXE, Items.NETHERITE_AXE)
            && hasDiamondOrBetterTool(player, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
    }

    private static boolean isDiamondOrBetter(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.DIAMOND_HELMET || item == Items.DIAMOND_CHESTPLATE
            || item == Items.DIAMOND_LEGGINGS || item == Items.DIAMOND_BOOTS
            || item == Items.NETHERITE_HELMET || item == Items.NETHERITE_CHESTPLATE
            || item == Items.NETHERITE_LEGGINGS || item == Items.NETHERITE_BOOTS;
    }

    private static boolean hasDiamondOrBetterTool(Player player, Item diamond, Item netherite) {
        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            Item item = stack.getItem();
            if (item == diamond || item == netherite) return true;
        }
        return false;
    }
}
