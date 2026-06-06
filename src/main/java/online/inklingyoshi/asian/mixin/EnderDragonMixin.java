package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonSittingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import online.inklingyoshi.asian.attack.ModDamageTypes;
import online.inklingyoshi.asian.difficulty.ModDifficulty;
import online.inklingyoshi.asian.util.DifficultyHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragon.class)
public class EnderDragonMixin {

    @Unique
    private boolean emotionalDamage$hasPerched;

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void checkPerchKill(CallbackInfo ci) {
        EnderDragon self = (EnderDragon) (Object) this;
        if (!(self.level() instanceof ServerLevel serverLevel)) return;

        if (DifficultyHelper.getModDifficulty(serverLevel.getServer()) != ModDifficulty.ASIAN_UPPER) return;

        DragonPhaseInstance currentPhase = self.getPhaseManager().getCurrentPhase();

        if (currentPhase instanceof AbstractDragonSittingPhase) {
            emotionalDamage$hasPerched = true;
            return;
        }

        if (emotionalDamage$hasPerched && self.isAlive()) {
            for (ServerPlayer player : serverLevel.players()) {
                player.hurt(ModDamageTypes.simpleSource(serverLevel, ModDamageTypes.SUCKS_AT_GAME), 10000.0f);
            }
            self.setHealth(self.getMaxHealth());
            net.minecraft.world.level.dimension.end.EnderDragonFight fight = self.getDragonFight();
            if (fight != null) {
                fight.resetSpikeCrystals();
            }
            emotionalDamage$hasPerched = false;
        }
    }
}
