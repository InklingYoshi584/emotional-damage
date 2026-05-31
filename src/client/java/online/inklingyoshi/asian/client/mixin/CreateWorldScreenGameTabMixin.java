package online.inklingyoshi.asian.client.mixin;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import online.inklingyoshi.asian.difficulty.ModDifficulty;
import online.inklingyoshi.asian.difficulty.PendingModDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$GameTab")
public class CreateWorldScreenGameTabMixin {
    @Unique
    private static CreateWorldScreen activeScreen;

    @Unique
    private static final Component[] LABELS = {
        Difficulty.PEACEFUL.getDisplayName(),
        Difficulty.EASY.getDisplayName(),
        Difficulty.NORMAL.getDisplayName(),
        Difficulty.HARD.getDisplayName(),
        Component.literal("asian"),
        Component.literal("ASIAN")
    };

    @Inject(method = "<init>", at = @At("HEAD"))
    private static void captureScreen(CreateWorldScreen screen, CallbackInfo ci) {
        activeScreen = screen;
    }

    @Redirect(
        method = "<init>",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CycleButton;builder(Ljava/util/function/Function;Ljava/lang/Object;)Lnet/minecraft/client/gui/components/CycleButton$Builder;", ordinal = 1)
    )
    private CycleButton.Builder redirectBuilder(Function func, Object initialValue) {
        int idx = ((Difficulty) initialValue).ordinal();
        return CycleButton.builder((Integer i) -> LABELS[i], idx);
    }

    @Redirect(
        method = "<init>",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CycleButton$Builder;withValues([Ljava/lang/Object;)Lnet/minecraft/client/gui/components/CycleButton$Builder;", ordinal = 1)
    )
    private CycleButton.Builder redirectWithValues(CycleButton.Builder builder, Object[] values) {
        return builder.withValues(0, 1, 2, 3, 4, 5);
    }

    @Redirect(
        method = "<init>",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CycleButton$Builder;create(IIIILnet/minecraft/network/chat/Component;Lnet/minecraft/client/gui/components/CycleButton$OnValueChange;)Lnet/minecraft/client/gui/components/CycleButton;", ordinal = 1)
    )
    private CycleButton redirectCreate(CycleButton.Builder builder, int x, int y, int w, int h, Component name, CycleButton.OnValueChange onValueChange) {
        return builder.create(0, 0, 210, 20, name, (CycleButton.OnValueChange) (b, v) -> {
            int slot = (Integer) v;
            WorldCreationUiState uiState = activeScreen.getUiState();
            if (slot < 4) {
                PendingModDifficulty.pending = ModDifficulty.NORMAL;
                uiState.setDifficulty(Difficulty.byId(slot));
            } else {
                PendingModDifficulty.pending = slot == 4 ? ModDifficulty.ASIAN_LOWER : ModDifficulty.ASIAN_UPPER;
                uiState.setDifficulty(Difficulty.HARD);
            }
        });
    }

    @Inject(method = "lambda$new$5", at = @At("HEAD"), cancellable = true)
    private static void onStateUpdate(CycleButton button, CreateWorldScreen screen, WorldCreationUiState state, CallbackInfo ci) {
        int idx;
        if (PendingModDifficulty.pending != null && PendingModDifficulty.pending != ModDifficulty.NORMAL) {
            idx = 4 + (PendingModDifficulty.pending == ModDifficulty.ASIAN_UPPER ? 1 : 0);
        } else {
            idx = state.getDifficulty().ordinal();
        }
        button.setValue(idx);
        button.active = !state.isHardcore();
        button.setTooltip(Tooltip.create(state.getDifficulty().getInfo()));
        ci.cancel();
    }
}
