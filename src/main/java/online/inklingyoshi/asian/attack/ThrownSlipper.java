package online.inklingyoshi.asian.attack;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class ThrownSlipper extends AbstractArrow {

    private boolean dealtDamage;

    public ThrownSlipper(EntityType<? extends ThrownSlipper> type, Level level) {
        super(type, level);
    }

    public ThrownSlipper(Level level, LivingEntity owner, ItemStack slipperStack) {
        super(ModEntities.THROWN_SLIPPER, owner, level, ItemStack.EMPTY, slipperStack);
        this.pickup = Pickup.ALLOWED;
    }

    public ThrownSlipper(Level level, double x, double y, double z, ItemStack slipperStack) {
        super(ModEntities.THROWN_SLIPPER, x, y, z, level, ItemStack.EMPTY, slipperStack);
        this.pickup = Pickup.ALLOWED;
    }

    private ItemStack getSlipperStack() {
        return getPickupItemStackOrigin();
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.SLIPPER);
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.WOOD_HIT;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        dealtDamage = true;
        ItemStack stack = getSlipperStack();
        if (!stack.isEmpty()) {
            int oldXp = SlipperHelper.getXp(stack);
            SlipperHelper.addXp(stack, 1);
            if (oldXp < 1000 && SlipperHelper.getXp(stack) >= 1000) {
                if (getOwner() instanceof Player player) {
                    PlayerAbilityTracker.setThrowUnlock(player, true);
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > 1200) {
            discard();
            return;
        }

        if (dealtDamage && SlipperHelper.hasLoyalty(SlipperHelper.getXp(getSlipperStack()))) {
            if (getOwner() instanceof LivingEntity owner && owner.isAlive()) {
                if (getOwner() instanceof ServerPlayer sp && sp.isSpectator()) return;
                ItemStack stack = getSlipperStack();
                if (!stack.isEmpty()) {
                    if (owner instanceof Player player) {
                        player.getInventory().add(stack);
                    }
                }
                discard();
            }
        }
    }
}
