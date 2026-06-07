package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import online.inklingyoshi.asian.EmotionalDamage;
import online.inklingyoshi.asian.attack.IPlayerAbilityTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class PlayerAbilityMixin implements IPlayerAbilityTracker {

    @Unique
    private boolean emotionalDamage$throwUnlocked;

    @Unique
    private boolean emotionalDamage$homingUnlocked;

    @Override
    public boolean emotionalDamage$hasThrowUnlock() {
        return emotionalDamage$throwUnlocked;
    }

    @Override
    public void emotionalDamage$setThrowUnlock(boolean value) {
        emotionalDamage$throwUnlocked = value;
    }

    @Override
    public boolean emotionalDamage$hasHomingUnlock() {
        return emotionalDamage$homingUnlocked;
    }

    @Override
    public void emotionalDamage$setHomingUnlock(boolean value) {
        emotionalDamage$homingUnlocked = value;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAbilities(ValueInput input, CallbackInfo ci) {
        emotionalDamage$throwUnlocked = input.getBooleanOr("emotional-damage:throwUnlocked", false);
        emotionalDamage$homingUnlocked = input.getBooleanOr("emotional-damage:homingUnlocked", false);
        EmotionalDamage.LOGGER.info("Read abilities: throw={}, homing={}", emotionalDamage$throwUnlocked, emotionalDamage$homingUnlocked);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeAbilities(ValueOutput output, CallbackInfo ci) {
        EmotionalDamage.LOGGER.info("Write abilities: throw={}, homing={}", emotionalDamage$throwUnlocked, emotionalDamage$homingUnlocked);
        if (emotionalDamage$throwUnlocked) {
            output.putBoolean("emotional-damage:throwUnlocked", true);
        }
        if (emotionalDamage$homingUnlocked) {
            output.putBoolean("emotional-damage:homingUnlocked", true);
        }
    }

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void restoreAbilities(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        if (oldPlayer instanceof IPlayerAbilityTracker tracker) {
            emotionalDamage$throwUnlocked = tracker.emotionalDamage$hasThrowUnlock();
            emotionalDamage$homingUnlocked = tracker.emotionalDamage$hasHomingUnlock();
        }
    }
}
