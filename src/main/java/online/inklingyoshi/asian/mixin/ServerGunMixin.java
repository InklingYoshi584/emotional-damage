package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import online.inklingyoshi.asian.attack.GunChallengeState;
import online.inklingyoshi.asian.attack.IGunPlayer;
import online.inklingyoshi.asian.attack.ModDamageTypes;
import online.inklingyoshi.asian.network.GunPackets;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerGunMixin implements IGunPlayer {

    @Unique
    private GunChallengeState gunState = GunChallengeState.IDLE;

    @Unique
    private char[] gunButtons = new char[3];

    @Unique
    private int gunStep = 0;

    @Unique
    private int gunTimer = 0;

    @Unique
    private int gunAction = 0;

    @Override
    public GunChallengeState emotionalDamage$getGunState() { return gunState; }

    @Override
    public void emotionalDamage$setGunState(GunChallengeState state) { this.gunState = state; }

    @Override
    public char[] emotionalDamage$getGunButtons() { return gunButtons; }

    @Override
    public void emotionalDamage$setGunButtons(char[] buttons) {
        this.gunButtons = buttons.clone();
    }

    @Override
    public int emotionalDamage$getGunStep() { return gunStep; }

    @Override
    public void emotionalDamage$setGunStep(int step) { this.gunStep = step; }

    @Override
    public int emotionalDamage$getGunTimer() { return gunTimer; }

    @Override
    public void emotionalDamage$setGunTimer(int timer) { this.gunTimer = timer; }

    @Override
    public int emotionalDamage$getGunAction() { return gunAction; }

    @Override
    public void emotionalDamage$setGunAction(int action) { this.gunAction = action; }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (gunState == GunChallengeState.IDLE) return;

        ServerPlayer self = (ServerPlayer) (Object) this;
        gunTimer++;

        if (gunTimer > 20) {
            killAndReset(self);
        }
    }

    @Unique
    private void killAndReset(ServerPlayer player) {
        ResourceKey<DamageType> damageKey;
        if (gunState == GunChallengeState.BUTTON) {
            damageKey = ModDamageTypes.TOO_SLOW;
        } else {
            damageKey = GunPackets.getActionDamageType(gunAction);
        }

        ServerLevel level = (ServerLevel) player.level();
        DamageSource source = ModDamageTypes.source(level, player, damageKey);
        player.hurt(source, 10000.0f);

        gunState = GunChallengeState.IDLE;
    }
}
