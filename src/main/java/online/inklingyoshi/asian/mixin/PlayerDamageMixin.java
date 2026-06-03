package online.inklingyoshi.asian.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import online.inklingyoshi.asian.attack.SlipperHelper;
import online.inklingyoshi.asian.attack.SlipperItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerDamageMixin {

    @Inject(method = "attack", at = @At("TAIL"))
    private void applySlipperBonusDamage(Entity target, CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (!(self.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) return;

        ItemStack stack = self.getMainHandItem();
        if (!(stack.getItem() instanceof SlipperItem)) return;

        int xp = SlipperHelper.getXp(stack);
        int tierDamage = SlipperHelper.getDamageForXp(xp);

        if (tierDamage > 1) {
            float bonusDamage = tierDamage - 1.0F;
            target.invulnerableTime = 0;
            target.hurtServer(serverLevel, serverLevel.damageSources().playerAttack(self), bonusDamage);
        }
    }
}
