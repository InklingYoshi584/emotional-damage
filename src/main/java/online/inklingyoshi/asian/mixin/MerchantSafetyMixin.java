package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;
import online.inklingyoshi.asian.attack.IMerchantTradeTracker;
import online.inklingyoshi.asian.attack.ModDamageTypes;
import online.inklingyoshi.asian.difficulty.ModDifficulty;
import online.inklingyoshi.asian.util.DifficultyHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantMenu.class)
public class MerchantSafetyMixin implements IMerchantTradeTracker {

    @Unique
    private boolean emotionalDamage$traded;

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/trading/Merchant;)V", at = @At("RETURN"))
    private void onOpen(int containerId, Inventory inv, Merchant merchant, CallbackInfo ci) {
        emotionalDamage$traded = false;
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void onClose(Player player, CallbackInfo ci) {
        if (!(player instanceof ServerPlayer sp)) return;

        if (DifficultyHelper.getModDifficulty(((ServerLevel) sp.level()).getServer()) != ModDifficulty.ASIAN_UPPER) return;

        if (!emotionalDamage$traded) {
            sp.hurt(ModDamageTypes.simpleSource((ServerLevel) sp.level(), ModDamageTypes.SOCIAL_ANXIETY), Float.MAX_VALUE);
        }
    }

    @Override
    public void emotionalDamage$markTrade() {
        emotionalDamage$traded = true;
    }
}
