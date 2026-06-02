package online.inklingyoshi.asian.mixin;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import online.inklingyoshi.asian.attack.IPlayerStatsTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class MerchantMenuMixin {

    @Shadow
    public abstract Slot getSlot(int index);

    @Inject(method = "clicked", at = @At("HEAD"))
    private void detectTradeEmeralds(int slotIndex, int button, ContainerInput input, Player player, CallbackInfo ci) {
        if (!((Object) this instanceof MerchantMenu)) {
            return;
        }
        if (slotIndex != 2) {
            return;
        }
        if (!(player instanceof IPlayerStatsTracker tracker)) {
            return;
        }
        Slot resultSlot = getSlot(2);
        ItemStack stack = resultSlot.getItem();
        if (stack.getItem() == Items.EMERALD && stack.getCount() > 0) {
            tracker.emotionalDamage$addDayEmeralds(stack.getCount());
        }
        if (!stack.isEmpty() && player instanceof online.inklingyoshi.asian.attack.IPlayerTradeMarker marker) {
            marker.emotionalDamage$markTrade();
        }
    }
}
