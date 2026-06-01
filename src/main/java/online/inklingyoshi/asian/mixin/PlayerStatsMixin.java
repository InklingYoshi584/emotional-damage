package online.inklingyoshi.asian.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import online.inklingyoshi.asian.attack.IPlayerStatsTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerStatsMixin implements IPlayerStatsTracker {

    @Unique
    private int emotionalDamage$dayEmeralds;

    @Unique
    private long emotionalDamage$lastDayChecked;

    @Unique
    private boolean emotionalDamage$missedFullCharge;

    @Override
    public int emotionalDamage$getDayEmeralds() {
        return emotionalDamage$dayEmeralds;
    }

    @Override
    public void emotionalDamage$addDayEmeralds(int amount) {
        emotionalDamage$dayEmeralds += amount;
    }

    @Override
    public void emotionalDamage$resetDayEmeralds() {
        emotionalDamage$dayEmeralds = 0;
    }

    @Override
    public long emotionalDamage$getLastDayChecked() {
        return emotionalDamage$lastDayChecked;
    }

    @Override
    public void emotionalDamage$setLastDayChecked(long day) {
        emotionalDamage$lastDayChecked = day;
    }

    @Override
    public boolean emotionalDamage$hasMissedFullCharge() {
        return emotionalDamage$missedFullCharge;
    }

    @Override
    public void emotionalDamage$setMissedFullCharge(boolean value) {
        emotionalDamage$missedFullCharge = value;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readStats(ValueInput input, CallbackInfo ci) {
        emotionalDamage$dayEmeralds = input.getIntOr("emotional-damage:dayEmeralds", 0);
        emotionalDamage$lastDayChecked = (long) input.getIntOr("emotional-damage:lastDayChecked", 0);
        emotionalDamage$missedFullCharge = input.getBooleanOr("emotional-damage:missedFullCharge", false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeStats(ValueOutput output, CallbackInfo ci) {
        if (emotionalDamage$dayEmeralds > 0) {
            output.putInt("emotional-damage:dayEmeralds", emotionalDamage$dayEmeralds);
        }
        if (emotionalDamage$lastDayChecked > 0) {
            output.putInt("emotional-damage:lastDayChecked", (int) emotionalDamage$lastDayChecked);
        }
        if (emotionalDamage$missedFullCharge) {
            output.putBoolean("emotional-damage:missedFullCharge", true);
        }
    }
}
