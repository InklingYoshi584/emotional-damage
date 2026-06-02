package online.inklingyoshi.asian.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import online.inklingyoshi.asian.difficulty.ModDifficulty;
import online.inklingyoshi.asian.util.DifficultyHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerAttributeMixin {

    @Unique
    private ModDifficulty emotionalDamage$lastAttrDifficulty = ModDifficulty.NORMAL;

    @Inject(method = "tick", at = @At("HEAD"))
    private void syncAttributes(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (!(self instanceof ServerPlayer player)) return;

        ModDifficulty current = DifficultyHelper.getModDifficulty(((ServerLevel) player.level()).getServer());
        if (current == emotionalDamage$lastAttrDifficulty) return;

        applyAttributes(player, current);
        emotionalDamage$lastAttrDifficulty = current;
    }

    private void applyAttributes(ServerPlayer player, ModDifficulty diff) {
        double maxHealth = diff == ModDifficulty.ASIAN_UPPER ? 1 : 20;
        double safeFall = diff == ModDifficulty.ASIAN_UPPER ? 0 : 3;
        double fallMult = diff == ModDifficulty.ASIAN_UPPER ? 100 : 1;

        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
        player.getAttribute(Attributes.SAFE_FALL_DISTANCE).setBaseValue(safeFall);
        player.getAttribute(Attributes.FALL_DAMAGE_MULTIPLIER).setBaseValue(fallMult);

        if (maxHealth < player.getMaxHealth()) {
            player.setHealth((float) maxHealth);
        }
    }
}
