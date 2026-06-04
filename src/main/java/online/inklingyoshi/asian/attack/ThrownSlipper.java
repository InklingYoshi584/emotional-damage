package online.inklingyoshi.asian.attack;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownSlipper extends AbstractArrow {

    private boolean dealtDamage;
    private boolean loyaltyReturned;
    private boolean homingActivated;

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

        if (!dealtDamage && !isInGround() && SlipperHelper.hasHoming(SlipperHelper.getXp(getWeaponItem()))) {
            if (!homingActivated) {
                if (getDeltaMovement().y < 0 || hasEntityWithinOneBlock()) {
                    homingActivated = true;
                }
            }
            if (homingActivated) {
                handleHoming();
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

    private boolean hasEntityWithinOneBlock() {
        Entity owner = getOwner();
        AABB box = getBoundingBox().inflate(1.0);
        for (Entity entity : level().getEntitiesOfClass(Entity.class, box, e ->
                e instanceof LivingEntity && e.isAlive() && e != owner)) {
            return true;
        }
        return false;
    }

    private void handleHoming() {
        Entity owner = getOwner();
        AABB searchBox = getBoundingBox().inflate(20.0);
        LivingEntity target = null;
        double closestDist = Double.MAX_VALUE;

        for (Entity entity : level().getEntitiesOfClass(Entity.class, searchBox, e ->
                e instanceof LivingEntity && e.isAlive() && e != owner)) {
            double dist = distanceToSqr(entity);
            if (dist < closestDist) {
                closestDist = dist;
                target = (LivingEntity) entity;
            }
        }

        if (target == null) {
            return;
        }

        setNoPhysics(true);
        Vec3 diff = target.getEyePosition().subtract(position());
        double riseSpeed = 0.015;
        double returnSpeed = 0.05;
        setPosRaw(
            getX() + diff.x * riseSpeed,
            getY() + diff.y * riseSpeed,
            getZ() + diff.z * riseSpeed
        );
        setDeltaMovement(getDeltaMovement().scale(0.95).add(diff.normalize().scale(returnSpeed)));
    }
}
