package online.inklingyoshi.asian.attack;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlotGroup;

public class SlipperItem extends Item implements ProjectileItem {

    public SlipperItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        int useTime = getUseDuration(stack, entity) - timeLeft;
        if (useTime < 10) return false;

        if (!(level instanceof ServerLevel serverLevel)) return false;

        ItemStack thrownStack = stack.copy();
        thrownStack.setCount(1);

        ThrownSlipper thrown = new ThrownSlipper(level, entity, thrownStack);
        float damage = SlipperHelper.getDamageForXp(SlipperHelper.getXp(stack));
        thrown.setBaseDamage(damage);
        thrown.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, 2.5F, 1.0F);

        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        SlipperHelper.addXp(thrownStack, 1);

        serverLevel.addFreshEntity(thrown);
        serverLevel.playSound(null, thrown, SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
        return true;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        SlipperHelper.addXp(stack, 1);
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        ThrownSlipper slipper = new ThrownSlipper(level, pos.x(), pos.y(), pos.z(), stack.copy());
        float damage = SlipperHelper.getDamageForXp(SlipperHelper.getXp(stack));
        slipper.setBaseDamage(damage);
        return slipper;
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 0.0, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_ID, 0.0, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build();
    }
}
