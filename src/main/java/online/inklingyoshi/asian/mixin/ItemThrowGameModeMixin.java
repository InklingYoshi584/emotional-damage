package online.inklingyoshi.asian.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

@Mixin(ServerPlayerGameMode.class)
public class ItemThrowGameModeMixin {

    @Inject(method = "useItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"), cancellable = true)
    private void interceptThrow(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        EmotionalDamage.LOGGER.info("ItemThrowGameModeMixin fired! hasUnlock={}", PlayerAbilityTracker.hasThrowUnlock(player));
        if (!PlayerAbilityTracker.hasThrowUnlock(player)) return;

        EmotionalDamage.LOGGER.info("ItemThrowGameModeMixin: trying to throw {}, throwable={}", stack, isThrowable(stack));
        if (!isThrowable(stack)) return;

        ServerLevel serverLevel = (ServerLevel) level;
        ItemStack thrownStack = stack.copy();
        thrownStack.setCount(1);

        ThrownItemProjectile proj = new ThrownItemProjectile(serverLevel, player, thrownStack);
        proj.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 1.0F);
        serverLevel.addFreshEntity(proj);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        cir.setReturnValue(InteractionResult.CONSUME);
    }

    private static boolean isThrowable(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.getItem() instanceof SlipperItem) return false;
        if (stack.has(DataComponents.FOOD)) return false;
        if (stack.has(DataComponents.CONSUMABLE)) return false;
        if (stack.has(DataComponents.EQUIPPABLE)) return false;
        if (stack.has(DataComponents.KINETIC_WEAPON)) return false;
        return !(stack.getItem() instanceof BlockItem);
    }
}
