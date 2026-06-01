package online.inklingyoshi.asian.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import online.inklingyoshi.asian.attack.IPlayerFoodTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerFoodTrackerMixin implements IPlayerFoodTracker {

    @Unique
    private int emotionalDamage$wastedHunger;

    @Override
    public int emotionalDamage$getWastedHunger() {
        return emotionalDamage$wastedHunger;
    }

    @Override
    public void emotionalDamage$addWastedHunger(int amount) {
        emotionalDamage$wastedHunger += amount;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readWastedHunger(ValueInput input, CallbackInfo ci) {
        emotionalDamage$wastedHunger = input.getIntOr("emotional-damage:wastedHunger", 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeWastedHunger(ValueOutput output, CallbackInfo ci) {
        if (emotionalDamage$wastedHunger > 0) {
            output.putInt("emotional-damage:wastedHunger", emotionalDamage$wastedHunger);
        }
    }
}
