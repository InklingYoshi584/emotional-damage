package online.inklingyoshi.asian.client.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import online.inklingyoshi.asian.attack.GunItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class GunHoldPoseMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void maintainGunPose(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;

        ItemStack mainHand = player.getMainHandItem();
        boolean holdingGun = mainHand.getItem() instanceof GunItem;

        if (holdingGun) {
            if (!player.isUsingItem()) {
                player.startUsingItem(InteractionHand.MAIN_HAND);
            }
        } else if (player.isUsingItem()
                && player.getUsedItemHand() == InteractionHand.MAIN_HAND
                && player.getUseItem().getItem() instanceof GunItem) {
            player.stopUsingItem();
        }
    }
}
