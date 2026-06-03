package online.inklingyoshi.asian.attack;

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

    public ThrownSlipper(Level level, LivingEntity owner, ItemStack weaponStack) {
        super(ModEntities.THROWN_SLIPPER, owner, level, weaponStack, ItemStack.EMPTY);
    }

    public ThrownSlipper(Level level, double x, double y, double z, ItemStack weaponStack) {
        super(ModEntities.THROWN_SLIPPER, x, y, z, level, weaponStack, ItemStack.EMPTY);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.SLIPPER);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        dealtDamage = true;
        ItemStack stack = getWeaponItem();
        if (!stack.isEmpty()) {
            SlipperHelper.addXp(stack, 1);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > 1200) {
            discard();
            return;
        }

        if (dealtDamage && SlipperHelper.hasLoyalty(SlipperHelper.getXp(getWeaponItem()))) {
            if (getOwner() instanceof LivingEntity owner && owner.isAlive()) {
                if (getOwner() instanceof ServerPlayer sp && sp.isSpectator()) return;
                ItemStack stack = getWeaponItem();
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
