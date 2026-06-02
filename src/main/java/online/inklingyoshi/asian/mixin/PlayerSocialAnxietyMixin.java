package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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

@Mixin(Player.class)
public abstract class PlayerSocialAnxietyMixin implements IPlayerTradeMarker {

    @Unique
    private boolean emotionalDamage$tradedInMenu;

    @Inject(method = "openMenu", at = @At("HEAD"))
    private void onOpenMenu(net.minecraft.world.MenuProvider menu, CallbackInfoReturnable<java.util.OptionalInt> cir) {
        emotionalDamage$tradedInMenu = false;
    }

    @Inject(method = "closeContainer", at = @At("HEAD"))
    private void onCloseContainer(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (!(self instanceof ServerPlayer player)) return;

        if (!(player.containerMenu instanceof MerchantMenu)) return;

        if (DifficultyHelper.getModDifficulty(((ServerLevel) player.level()).getServer()) != ModDifficulty.ASIAN_UPPER) return;

        if (!emotionalDamage$tradedInMenu) {
            player.hurt(ModDamageTypes.simpleSource((ServerLevel) player.level(), ModDamageTypes.SOCIAL_ANXIETY), Float.MAX_VALUE);
        }
    }

    @Override
    public void emotionalDamage$markTrade() {
        emotionalDamage$tradedInMenu = true;
    }
}
