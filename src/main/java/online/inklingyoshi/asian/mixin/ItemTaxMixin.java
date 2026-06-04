package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import online.inklingyoshi.asian.util.DifficultyHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemTaxMixin {

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void taxItemPickup(Player player, CallbackInfo ci) {
        if (!(player instanceof ServerPlayer sp)) return;

        if (!DifficultyHelper.isAsianOrHigher(sp.level().getServer())) return;

        if (sp.getRandom().nextInt(50) == 0) {
            ItemEntity self = (ItemEntity) (Object) this;
            self.discard();
            ci.cancel();
        }
    }
}
