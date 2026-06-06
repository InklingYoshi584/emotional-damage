package online.inklingyoshi.asian.mixin;

import java.util.concurrent.atomic.AtomicBoolean;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import online.inklingyoshi.asian.attack.ModDamageTypes;
import online.inklingyoshi.asian.network.CheaterCrashPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndPortalBlock.class)
public class CheaterMixin {

    @Inject(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;showEndCredits()V"), cancellable = true)
    private void interceptEndCredits(net.minecraft.world.level.block.state.BlockState state, Level level, net.minecraft.core.BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier applier, boolean flag, CallbackInfo ci) {
        if (!(entity instanceof ServerPlayer player)) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        MinecraftServer server = serverLevel.getServer();
        boolean anyGameRuleModified = hasAnyGameRuleModified(serverLevel.getGameRules(), serverLevel);
        boolean cheatsEnabled = server.getPlayerList().isAllowCommandsForAllPlayers();

        if (anyGameRuleModified || cheatsEnabled) {
            ci.cancel();
            player.hurt(ModDamageTypes.simpleSource(player.level(), ModDamageTypes.CHEATER), 10000.0f);
            ServerPlayNetworking.send(player, new CheaterCrashPayload());

            String errorMsg = "You tried to beat the game as a CHEATER(FAILURE)";
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {}
                server.execute(() -> {
                    throw new RuntimeException(errorMsg);
                });
            }, "CheaterCrashThread").start();
        }
    }

    private static boolean hasAnyGameRuleModified(GameRules current, ServerLevel level) {
        GameRules defaults = new GameRules(level.enabledFeatures());
        AtomicBoolean modified = new AtomicBoolean(false);

        current.availableRules().forEach(rule -> {
            if (modified.get()) return;
            Object currentValue = current.get(rule);
            Object defaultValue = defaults.get(rule);
            if (!currentValue.equals(defaultValue)) {
                modified.set(true);
            }
        });

        return modified.get();
    }
}
