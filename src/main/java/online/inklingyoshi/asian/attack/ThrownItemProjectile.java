package online.inklingyoshi.asian.attack;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

public class ThrownItemProjectile extends AbstractArrow {

    private ItemStack carriedItem;

    public ThrownItemProjectile(EntityType<? extends ThrownItemProjectile> type, Level level) {
        super(type, level);
    }

    private static final ItemStack DUMMY_WEAPON = new ItemStack(Items.TRIDENT);

    public ThrownItemProjectile(ServerLevel level, LivingEntity owner, ItemStack item) {
        super(ModEntities.THROWN_ITEM, owner, level, DUMMY_WEAPON, item.copy());
        this.carriedItem = item.copy();
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return carriedItem != null ? carriedItem.copy() : ItemStack.EMPTY;
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
        if (carriedItem != null && !carriedItem.isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(level(), getX(), getY(), getZ(), carriedItem.copy());
            level().addFreshEntity(itemEntity);
            carriedItem = ItemStack.EMPTY;
        }
        this.discard();
    }
}
