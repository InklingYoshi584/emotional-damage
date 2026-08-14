package online.inklingyoshi.asian.client.mixin;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import online.inklingyoshi.asian.attack.GunItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemInHandRenderer.class)
public class GunHeldPoseMixin {

    @Redirect(method = "renderArmWithItem",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private boolean treatGunAsCrossbow(ItemStack stack, Object item) {
        if (!(item instanceof net.minecraft.world.item.Item i)) return false;
        return stack.getItem() == i || (i == Items.CROSSBOW && stack.getItem() instanceof GunItem);
    }

    @Redirect(method = "renderArmWithItem",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CrossbowItem;isCharged(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean treatGunAsChargedCrossbow(ItemStack stack) {
        return CrossbowItem.isCharged(stack) || stack.getItem() instanceof GunItem;
    }
}
