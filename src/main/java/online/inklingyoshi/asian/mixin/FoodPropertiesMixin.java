package online.inklingyoshi.asian.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import online.inklingyoshi.asian.attack.IPlayerFoodTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodProperties.class)
public class FoodPropertiesMixin {

    @Inject(method = "onConsume", at = @At("HEAD"))
    private void trackWastedHunger(Level level, LivingEntity entity, ItemStack stack, Consumable consumable, CallbackInfo ci) {
        if (entity instanceof Player player && player instanceof IPlayerFoodTracker tracker) {
            int currentFood = player.getFoodData().getFoodLevel();
            int nutrition = ((FoodProperties) (Object) this).nutrition();
            int wasted = Math.max(0, nutrition - Math.max(0, 20 - currentFood));
            if (wasted > 0) {
                tracker.emotionalDamage$addWastedHunger(wasted);
            }
        }
    }
}
