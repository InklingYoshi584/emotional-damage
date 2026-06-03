package online.inklingyoshi.asian.attack;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

public class ThrownItemProjectile extends AbstractArrow {

    private ItemStack carriedItem = ItemStack.EMPTY;

    public ThrownItemProjectile(EntityType<? extends ThrownItemProjectile> type, Level level) {
        super(type, level);
    }

    public ThrownItemProjectile(ServerLevel level, LivingEntity owner, ItemStack item) {
        super(ModEntities.THROWN_ITEM, owner, level, ItemStack.EMPTY, item.copy());
        this.carriedItem = item.copy();
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return carriedItem.copy();
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > 300) {
            dropItemAndDiscard();
            return;
        }
        if (inGroundTime > 20) {
            dropItemAndDiscard();
        }
    }

    private void dropItemAndDiscard() {
        if (!carriedItem.isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(level(), getX(), getY(), getZ(), carriedItem.copy());
            level().addFreshEntity(itemEntity);
            carriedItem = ItemStack.EMPTY;
        }
        this.discard();
    }
}
