package online.inklingyoshi.asian.attack;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import java.util.UUID;

public class ThrownItemProjectile extends AbstractArrow {

    private static final EntityDataAccessor<ItemStack> DATA_ITEM =
        SynchedEntityData.defineId(ThrownItemProjectile.class, EntityDataSerializers.ITEM_STACK);

    private static final EntityDataAccessor<Boolean> HAS_TARGET =
        SynchedEntityData.defineId(ThrownItemProjectile.class, EntityDataSerializers.BOOLEAN);

    private UUID lockedTarget;

    public ThrownItemProjectile(EntityType<? extends ThrownItemProjectile> type, Level level) {
        super(type, level);
    }

    public ThrownItemProjectile(ServerLevel level, LivingEntity owner, ItemStack item) {
        super(ModEntities.THROWN_ITEM, owner, level, item.copy(), item.copy());
        this.entityData.set(DATA_ITEM, item.copy());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ITEM, ItemStack.EMPTY);
        builder.define(HAS_TARGET, false);
    }

    private void setHasTarget(boolean value) {
        entityData.set(HAS_TARGET, value);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        ItemStack item = getCarriedItem();
        return item.isEmpty() ? ItemStack.EMPTY : item.copy();
    }

    public ItemStack getCarriedItem() {
        return this.entityData.get(DATA_ITEM);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        dropItemAndDiscard();
    }

    @Override
    public void tick() {
        if (!level().isClientSide()) {
            setNoGravity(lockedTarget != null);
        }
        super.tick();
        if (tickCount > 300) {
            dropItemAndDiscard();
            return;
        }
        if (inGroundTime > 20) {
            dropItemAndDiscard();
            return;
        }

        if (!isInGround()) {
            Entity owner = getOwner();
            if (owner instanceof Player player && PlayerAbilityTracker.hasHomingUnlock(player)) {
                if (owner instanceof IPlayerLockedTarget tracker) {
                    UUID playerTarget = tracker.emotionalDamage$getLockedTarget();
                    if (playerTarget != null && lockedTarget == null) {
                        lockedTarget = playerTarget;
                        setHasTarget(true);
                    }
                }
                if (lockedTarget != null && level() instanceof ServerLevel serverLevel) {
                    Entity target = serverLevel.getEntity(lockedTarget);
                    if (target instanceof LivingEntity living && living.isAlive()) {
                        steerToward(living);
                    } else {
                        lockedTarget = null;
                        setHasTarget(false);
                    }
                }
            }
        }
    }

    private void steerToward(LivingEntity target) {
        Vec3 velocity = getDeltaMovement();
        double speed = velocity.length();
        if (speed < 0.8) {
            speed = 0.8;
        }
        if (speed > 1.25) {
            speed = 1.25;
        }

        Vec3 toTarget = target.getEyePosition().subtract(position()).normalize();
        Vec3 currentDir = velocity.normalize();
        double steerStrength = 0.95;
        Vec3 steeredDir = currentDir.add(toTarget.subtract(currentDir).scale(steerStrength)).normalize();

        setDeltaMovement(steeredDir.scale(speed));
    }

    private void dropItemAndDiscard() {
        ItemStack carried = getCarriedItem();
        if (!carried.isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(level(), getX(), getY(), getZ(), carried.copy());
            level().addFreshEntity(itemEntity);
        }
        this.discard();
    }
}
