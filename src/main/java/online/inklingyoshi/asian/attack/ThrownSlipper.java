package online.inklingyoshi.asian.attack;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import java.util.UUID;

public class ThrownSlipper extends AbstractArrow {

    private boolean dealtDamage;
    private boolean loyaltyReturned;

    public ThrownSlipper(EntityType<? extends ThrownSlipper> type, Level level) {
        super(type, level);
    }

    public ThrownSlipper(Level level, LivingEntity owner, ItemStack slipperStack) {
        super(ModEntities.THROWN_SLIPPER, owner, level, slipperStack, slipperStack);
        this.pickup = Pickup.ALLOWED;
    }

    public ThrownSlipper(Level level, double x, double y, double z, ItemStack slipperStack) {
        super(ModEntities.THROWN_SLIPPER, x, y, z, level, slipperStack, slipperStack);
        this.pickup = Pickup.ALLOWED;
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
        markHit();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        markHit();
    }

    private void markHit() {
        dealtDamage = true;
        ItemStack stack = getWeaponItem();
        if (stack == null || stack.isEmpty()) {
            return;
        }
        int oldXp = SlipperHelper.getXp(stack);
        SlipperHelper.addXp(stack, 1);
        if (oldXp < 1000 && SlipperHelper.getXp(stack) >= 1000) {
            if (getOwner() instanceof Player player) {
                PlayerAbilityTracker.setThrowUnlock(player, true);
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

        if (!dealtDamage && !isInGround() && SlipperHelper.hasHoming(SlipperHelper.getXp(getWeaponItem()))) {
            Entity owner = getOwner();
            if (owner instanceof IPlayerLockedTarget tracker) {
                UUID targetId = tracker.emotionalDamage$getLockedTarget();
                if (targetId != null && level() instanceof ServerLevel serverLevel) {
                    Entity target = serverLevel.getEntity(targetId);
                    if (target instanceof LivingEntity living && living.isAlive()) {
                        steerToward(living);
                    }
                }
            }
        }

        if (!loyaltyReturned && dealtDamage && SlipperHelper.hasLoyalty(SlipperHelper.getXp(getWeaponItem()))) {
            loyaltyReturned = true;
            if (getOwner() instanceof LivingEntity owner && owner.isAlive()) {
                if (getOwner() instanceof ServerPlayer sp && sp.isSpectator()) return;
                ItemStack stack = getWeaponItem();
                if (!stack.isEmpty()) {
                    if (owner instanceof Player player) {
                        player.getInventory().add(stack);
                    }
                }
            }
            discard();
        }
    }

    private void steerToward(LivingEntity target) {
        Vec3 velocity = getDeltaMovement();
        double speed = velocity.length();
        if (speed < 1.0) {
            speed = 1.0;
        }

        Vec3 toTarget = target.getEyePosition().subtract(position()).normalize();
        Vec3 currentDir = velocity.normalize();
        double steerStrength = 0.95;
        Vec3 steeredDir = currentDir.add(toTarget.subtract(currentDir).scale(steerStrength)).normalize();

        setDeltaMovement(steeredDir.scale(speed));
    }
}
