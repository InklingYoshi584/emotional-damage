package online.inklingyoshi.asian.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import online.inklingyoshi.asian.difficulty.ModDifficulty;
import online.inklingyoshi.asian.difficulty.ModDifficultyState;

public final class ModDifficultyCommand {
    private static final SuggestionProvider<CommandSourceStack> SUGGESTIONS =
        (ctx, builder) -> {
            builder.suggest("peaceful");
            builder.suggest("easy");
            builder.suggest("normal");
            builder.suggest("hard");
            builder.suggest("asian");
            builder.suggest("ASIAN");
            return builder.buildFuture();
        };

    private ModDifficultyCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("difficulty")
            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(Commands.argument("difficulty", StringArgumentType.word())
                .suggests(SUGGESTIONS)
                .executes(ModDifficultyCommand::executeSet)
            )
            .executes(ModDifficultyCommand::executeQuery)
        );
    }

    private static int executeSet(CommandContext<CommandSourceStack> ctx) {
        String value = StringArgumentType.getString(ctx, "difficulty");
        CommandSourceStack source = ctx.getSource();
        MinecraftServer server = source.getServer();

        Difficulty vanillaDiff = Difficulty.byName(value);
        if (vanillaDiff != null) {
            return setVanillaDifficulty(source, server, vanillaDiff);
        }

        ModDifficulty modDiff = ModDifficulty.byName(value);
        if (modDiff != ModDifficulty.NORMAL) {
            return setModDifficulty(source, server, modDiff);
        }

        source.sendFailure(Component.translatable("commands.difficulty.failure", value));
        return 0;
    }

    private static int setVanillaDifficulty(CommandSourceStack source, MinecraftServer server, Difficulty difficulty) {
        if (server.getWorldData().getDifficulty() == difficulty) {
            source.sendFailure(
                Component.translatable("commands.difficulty.failure", difficulty.getDisplayName())
            );
            return 0;
        }

        ModDifficultyState state = ModDifficultyState.getOrCreate(server);
        state.setDifficulty(ModDifficulty.NORMAL);
        server.setDifficulty(difficulty, true);

        source.sendSuccess(() ->
            Component.translatable("commands.difficulty.success", difficulty.getDisplayName()), true
        );
        return difficulty.getId();
    }

    private static int setModDifficulty(CommandSourceStack source, MinecraftServer server, ModDifficulty modDiff) {
        ModDifficultyState state = ModDifficultyState.getOrCreate(server);
        state.setDifficulty(modDiff);

        Difficulty forced = modDiff.getForcedVanillaDifficulty();
        if (forced != null) {
            server.setDifficulty(forced, true);
        }

        source.sendSuccess(() ->
            Component.literal("Set mod difficulty to ").append(modDiff.getDisplayName()), true
        );
        return modDiff.getLevel();
    }

    private static int executeQuery(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        MinecraftServer server = source.getServer();

        ModDifficulty modDiff = ModDifficultyState.getOrCreate(server).getDifficulty();
        Difficulty vanillaDiff = server.getWorldData().getDifficulty();

        source.sendSuccess(() ->
            Component.literal("Difficulty: ")
                .append(vanillaDiff.getDisplayName())
                .append(Component.literal(" | Mod: "))
                .append(modDiff.getDisplayName()), false
        );
        return vanillaDiff.getId();
    }
}
