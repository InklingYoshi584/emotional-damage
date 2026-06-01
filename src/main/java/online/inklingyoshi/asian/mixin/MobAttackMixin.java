package online.inklingyoshi.asian.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.monster.Enemy;
import online.inklingyoshi.asian.attack.MobAttackGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobAttackMixin {

    @Shadow
    protected GoalSelector goalSelector;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectAttackGoal(CallbackInfo ci) {
        Mob self = (Mob) (Object) this;
        if (self instanceof Enemy) {
            goalSelector.addGoal(2, new MobAttackGoal(self));
        }
    }
}
