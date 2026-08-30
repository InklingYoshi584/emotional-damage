package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import online.inklingyoshi.asian.attack.StealTaunt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Steal taunt trigger: a piglin becoming angry at (targeting) a player.
 * 26.1 piglins have no numeric anger level; PiglinAi.setAngerTarget is the
 * single leaf where the ANGRY_AT brain memory is assigned, so this catches
 * every "piglin now angry at X" transition (retaliation, provocation, etc.).
 */
@Mixin(PiglinAi.class)
public class StealTauntPiglinMixin {

    @Inject(method = "setAngerTarget", at = @At("HEAD"))
    private static void onPiglinAnger(ServerLevel level, AbstractPiglin piglin, LivingEntity target,
            CallbackInfo ci) {
        if (level.isClientSide()) return;
        if (target instanceof ServerPlayer serverPlayer) {
            StealTaunt.triggerTaunt(level, serverPlayer);
        }
    }
}
