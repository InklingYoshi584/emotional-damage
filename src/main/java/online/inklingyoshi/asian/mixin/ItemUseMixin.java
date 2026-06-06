package online.inklingyoshi.asian.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import online.inklingyoshi.asian.EmotionalDamage;
import online.inklingyoshi.asian.attack.ModEntities;
import online.inklingyoshi.asian.attack.PlayerAbilityTracker;
import online.inklingyoshi.asian.attack.SlipperItem;
import online.inklingyoshi.asian.attack.ThrownItemProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemUseMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void throwNonUseable(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (level.isClientSide()) return;
        if (!PlayerAbilityTracker.hasThrowUnlock(player)) return;

        ItemStack self = (ItemStack) (Object) this;
        EmotionalDamage.LOGGER.info("ItemUseMixin: trying to throw {}, throwable={}", self, isThrowable(self));
        if (!isThrowable(self)) return;

        ServerLevel serverLevel = (ServerLevel) level;
        ItemStack thrownStack = self.copy();
        thrownStack.setCount(1);

        ThrownItemProjectile proj = new ThrownItemProjectile(serverLevel, player, thrownStack);
        proj.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 1.0F);
        serverLevel.addFreshEntity(proj);

        if (!player.getAbilities().instabuild) {
            self.shrink(1);
        }

        cir.setReturnValue(InteractionResult.CONSUME);
    }

    private static boolean isThrowable(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.getItem() instanceof SlipperItem) return false;
        if (stack.get(DataComponents.FOOD) != null) return false;
        return !(stack.getItem() instanceof BlockItem);
    }
}
