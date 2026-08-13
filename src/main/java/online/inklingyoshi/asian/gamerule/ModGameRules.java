package online.inklingyoshi.asian.gamerule;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import online.inklingyoshi.asian.EmotionalDamage;
import online.inklingyoshi.asian.util.DifficultyHelper;

public final class ModGameRules {
    private ModGameRules() {}

    public static final GameRule<Boolean> HIGH_JUMP = rule(GameRuleCategory.MOBS, "high_jump", true);
    public static final GameRule<Boolean> INSULTS = rule(GameRuleCategory.MOBS, "insults", true);
    public static final GameRule<Boolean> ITEM_TAX = rule(GameRuleCategory.PLAYER, "item_tax", true);
    public static final GameRule<Boolean> SOCIAL_ANXIETY = rule(GameRuleCategory.MOBS, "social_anxiety", true);
    public static final GameRule<Boolean> PHANTOM_SPAWNER = rule(GameRuleCategory.SPAWNING, "phantom_spawner", true);
    public static final GameRule<Boolean> POISON_GRASS = rule(GameRuleCategory.PLAYER, "poison_grass", true);
    public static final GameRule<Boolean> BLOCK_DAMAGE = rule(GameRuleCategory.PLAYER, "block_damage", true);
    public static final GameRule<Boolean> IDLE_DAMAGE = rule(GameRuleCategory.PLAYER, "idle_damage", true);
    public static final GameRule<Boolean> DRAGON_REVENGE = rule(GameRuleCategory.MOBS, "dragon_revenge", true);

    private static GameRule<Boolean> rule(GameRuleCategory category, String name, boolean defaultValue) {
        return GameRuleBuilder.forBoolean(defaultValue)
            .category(category)
            .buildAndRegister(Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, name));
    }

    /** Behavior gated on asian+ difficulty and the gamerule. No-op below asian. */
    public static boolean enabled(MinecraftServer server, GameRule<Boolean> rule) {
        return DifficultyHelper.isAsianOrHigher(server) && server.getGameRules().get(rule);
    }

    /** Forces static initialization so all rules register at mod init. */
    public static void register() {
    }
}
