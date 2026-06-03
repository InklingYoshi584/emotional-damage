package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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

    @Override
    public boolean emotionalDamage$hasThrowUnlock() {
        return emotionalDamage$throwUnlocked;
    }

    @Override
    public void emotionalDamage$setThrowUnlock(boolean value) {
        emotionalDamage$throwUnlocked = value;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readThrowUnlock(ValueInput input, CallbackInfo ci) {
        emotionalDamage$throwUnlocked = input.getBooleanOr("emotional-damage:throwUnlocked", false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeThrowUnlock(ValueOutput output, CallbackInfo ci) {
        if (emotionalDamage$throwUnlocked) {
            output.putBoolean("emotional-damage:throwUnlocked", true);
        }
    }
}
