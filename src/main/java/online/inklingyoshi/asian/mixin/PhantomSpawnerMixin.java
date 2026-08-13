package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import online.inklingyoshi.asian.gamerule.ModGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin {
    private static boolean emotionalDamage$forceSpawn;

    @Inject(method = "tick", at = @At("HEAD"))
    private void checkForceSpawn(ServerLevel level, boolean arg, CallbackInfo ci) {
        emotionalDamage$forceSpawn = ModGameRules.enabled(level.getServer(), ModGameRules.PHANTOM_SPAWNER);
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 72000))
    private int modifySleepThreshold(int original) {
        return emotionalDamage$forceSpawn ? 0 : original;
    }
}
