package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MerchantMenu;
import online.inklingyoshi.asian.attack.IPlayerTradeMarker;
import online.inklingyoshi.asian.attack.ModDamageTypes;
import online.inklingyoshi.asian.difficulty.ModDifficulty;
import online.inklingyoshi.asian.util.DifficultyHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class PlayerSocialAnxietyMixin implements IPlayerTradeMarker {

    @Unique
    private boolean emotionalDamage$tradedInMenu;

    @Inject(method = "openMenu", at = @At("HEAD"))
    private void onOpenMenu(net.minecraft.world.MenuProvider menu, CallbackInfoReturnable<java.util.OptionalInt> cir) {
        emotionalDamage$tradedInMenu = false;
    }

    @Inject(method = "closeContainer", at = @At("HEAD"))
    private void onCloseContainer(CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;

        if (!(self.containerMenu instanceof MerchantMenu)) return;

        if (DifficultyHelper.getModDifficulty(self.level().getServer()) != ModDifficulty.ASIAN_UPPER) return;

        if (!emotionalDamage$tradedInMenu) {
            self.hurt(ModDamageTypes.simpleSource(self.level(), ModDamageTypes.SOCIAL_ANXIETY), Float.MAX_VALUE);
        }
    }

    @Override
    public void emotionalDamage$markTrade() {
        emotionalDamage$tradedInMenu = true;
    }
}
