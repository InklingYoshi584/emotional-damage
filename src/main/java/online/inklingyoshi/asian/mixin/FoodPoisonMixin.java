package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import online.inklingyoshi.asian.attack.DelayedKillSystem;
import online.inklingyoshi.asian.attack.ModDamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class FoodPoisonMixin {

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    private void onFinishUsing(Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        if (level.isClientSide()) return;
        if (!(entity instanceof ServerPlayer player)) return;

        ItemStack self = (ItemStack) (Object) this;
        if (self.getItem() == Items.MILK_BUCKET) {
            int delay = 3600 + player.getRandom().nextInt(2401);
            DelayedKillSystem.schedule(player, ModDamageTypes.LACTOSE_INTOLERANCE, delay);
        } else if (isRawMeat(self)) {
            int delay = 3600 + player.getRandom().nextInt(2401);
            DelayedKillSystem.schedule(player, ModDamageTypes.PARASITES, delay);
        }
    }

    private static boolean isRawMeat(ItemStack stack) {
        return stack.getItem() == Items.BEEF
            || stack.getItem() == Items.CHICKEN
            || stack.getItem() == Items.PORKCHOP
            || stack.getItem() == Items.RABBIT
            || stack.getItem() == Items.MUTTON;
    }
}
