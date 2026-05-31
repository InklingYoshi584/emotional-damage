package online.inklingyoshi.asian.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.DifficultyButtons;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import online.inklingyoshi.asian.difficulty.ClientModDifficulty;
import online.inklingyoshi.asian.difficulty.ModDifficulty;
import online.inklingyoshi.asian.network.ModDifficultyPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(DifficultyButtons.class)
public class DifficultyButtonsMixin {
    @Unique
    private static final Map<DifficultyButtons, CycleButton<Integer>> MOD_BUTTONS = new HashMap<>();

    @Unique
    private static final Component[] LABELS = {
        Difficulty.PEACEFUL.getDisplayName(),
        Difficulty.EASY.getDisplayName(),
        Difficulty.NORMAL.getDisplayName(),
        Difficulty.HARD.getDisplayName(),
        Component.literal("asian"),
        Component.literal("ASIAN")
    };

    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private static void onCreate(Minecraft minecraft, Level level, Screen screen, CallbackInfoReturnable<DifficultyButtons> cir) {
        int initialIndex = getInitialIndex(level);

        CycleButton<Integer> diffButton = CycleButton.builder(
            (Integer value) -> LABELS[value],
            initialIndex
        )
        .withValues(0, 1, 2, 3, 4, 5)
        .create(0, 0, 150, 20, Component.translatable("options.difficulty"),
            (btn, value) -> {
                if (minecraft.getConnection() == null) return;
                if (value < 4) {
                    ClientModDifficulty.pending = null;
                    minecraft.getConnection().send(new ServerboundChangeDifficultyPacket(Difficulty.byId(value)));
                    ClientPlayNetworking.send(new ModDifficultyPayload(ModDifficulty.NORMAL));
                } else {
                    ModDifficulty modDiff = value == 4 ? ModDifficulty.ASIAN_LOWER : ModDifficulty.ASIAN_UPPER;
                    ClientModDifficulty.pending = modDiff;
                    minecraft.getConnection().send(new ServerboundChangeDifficultyPacket(Difficulty.HARD));
                    ClientPlayNetworking.send(new ModDifficultyPayload(modDiff));
                }
            });

        LockIconButton lockButton = new LockIconButton(0, 0, btn -> {
            minecraft.setScreen(new ConfirmScreen(
                confirmed -> {
                    if (confirmed) {
                        if (minecraft.getConnection() != null) {
                            minecraft.getConnection().send(new ServerboundLockDifficultyPacket(true));
                        }
                        ((LockIconButton)btn).setLocked(true);
                        btn.active = false;
                        diffButton.active = false;
                    }
                    minecraft.setScreen(screen);
                },
                Component.translatable("difficulty.lock.title"),
                Component.translatable("difficulty.lock.question",
                    level.getLevelData().getDifficulty().getDisplayName())
            ));
        });

        boolean locked = isDifficultyLocked(level);
        boolean hasPerm = playerHasPermissionToChangeDifficulty(minecraft);
        lockButton.setLocked(locked);
        lockButton.active = !locked && hasPerm;
        diffButton.active = !locked && hasPerm;

        diffButton.setWidth(diffButton.getWidth() - lockButton.getWidth());

        EqualSpacingLayout layout = new EqualSpacingLayout(150, 0, EqualSpacingLayout.Orientation.HORIZONTAL);
        layout.addChild(diffButton);
        layout.addChild(lockButton);

        @SuppressWarnings({"rawtypes", "unchecked"})
        CycleButton<Difficulty> castButton = (CycleButton<Difficulty>)(CycleButton)diffButton;
        DifficultyButtons result = new DifficultyButtons(layout, castButton, lockButton, level);
        MOD_BUTTONS.put(result, diffButton);
        cir.setReturnValue(result);
    }

    @Inject(method = "refresh", at = @At("HEAD"), cancellable = true)
    private void onRefresh(Minecraft minecraft, CallbackInfo ci) {
        DifficultyButtons self = (DifficultyButtons)(Object)this;
        int index = getCurrentIndex(self.level());
        CycleButton<Integer> modBtn = MOD_BUTTONS.get(self);
        if (modBtn != null) {
            modBtn.setValue(index);
        } else {
            @SuppressWarnings({"rawtypes", "unchecked"})
            CycleButton<Integer> btn = (CycleButton<Integer>)(CycleButton)self.difficultyButton();
            btn.setValue(index);
        }

        boolean locked = isDifficultyLocked(self.level());
        boolean hasPerm = playerHasPermissionToChangeDifficulty(minecraft);
        self.lockButton().setLocked(locked);
        self.lockButton().active = !locked && hasPerm;
        self.difficultyButton().active = !locked && hasPerm;
        ci.cancel();
    }

    @Unique
    private static int getIndex(ModDifficulty mod) {
        return 4 + (mod == ModDifficulty.ASIAN_UPPER ? 1 : 0);
    }

    @Unique
    private static int getInitialIndex(Level level) {
        ModDifficulty mod = ClientModDifficulty.current;
        if (mod != ModDifficulty.NORMAL) return getIndex(mod);
        return level.getDifficulty().getId();
    }

    @Unique
    private static int getCurrentIndex(Level level) {
        ModDifficulty effective = ClientModDifficulty.pending != null
            ? ClientModDifficulty.pending : ClientModDifficulty.current;
        if (effective != ModDifficulty.NORMAL) return getIndex(effective);
        return level.getDifficulty().getId();
    }

    @Unique
    private static boolean isDifficultyLocked(Level level) {
        return level.getLevelData().isDifficultyLocked() || level.getLevelData().isHardcore();
    }

    @Unique
    private static boolean playerHasPermissionToChangeDifficulty(Minecraft minecraft) {
        return minecraft.hasSingleplayerServer();
    }
}
