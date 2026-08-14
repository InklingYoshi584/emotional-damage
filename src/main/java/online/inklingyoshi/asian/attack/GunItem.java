package online.inklingyoshi.asian.attack;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemUseAnimation;
import online.inklingyoshi.asian.EmotionalDamage;
import online.inklingyoshi.asian.network.GunPackets;

import java.util.Random;


public class GunItem extends Item {

    public GunItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.CROSSBOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, net.minecraft.world.entity.LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.CONSUME;
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;
        if (serverPlayer instanceof IGunPlayer gp && gp.emotionalDamage$getGunState() != GunChallengeState.IDLE) {
            return InteractionResult.FAIL;
        }

        Random random = new Random();
        char[] buttons = new char[3];
        for (int i = 0; i < 3; i++) {
            buttons[i] = (char) ('A' + random.nextInt(26));
        }

        GunPackets.startChallenge(serverPlayer, buttons);

        EmotionalDamage.LOGGER.info("Gun challenge started for {}: {}{}{}",
            player.getName().getString(), buttons[0], buttons[1], buttons[2]);

        return InteractionResult.CONSUME;
    }
}
