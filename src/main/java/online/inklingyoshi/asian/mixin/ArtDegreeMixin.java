package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import online.inklingyoshi.asian.attack.ModDamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class ArtDegreeMixin {

    @Shadow
    public abstract Slot getSlot(int index);

    @Inject(method = "clicked", at = @At("HEAD"))
    private void detectPaintingCrafted(int slotIndex, int button, ContainerInput input, Player player, CallbackInfo ci) {
        if (!(player instanceof ServerPlayer sp)) return;
        if (slotIndex != 0) return;
        if (!((Object) this instanceof net.minecraft.world.inventory.CraftingMenu)
            && !((Object) this instanceof net.minecraft.world.inventory.InventoryMenu)) return;

        Slot slot = getSlot(0);
        ItemStack stack = slot.getItem();
        if (stack.getItem() == Items.PAINTING) {
            sp.hurt(ModDamageTypes.simpleSource((ServerLevel) sp.level(), ModDamageTypes.ART_DEGREE), Float.MAX_VALUE);
        }
    }
}
