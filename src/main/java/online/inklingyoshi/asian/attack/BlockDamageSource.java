package online.inklingyoshi.asian.attack;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

public class BlockDamageSource extends DamageSource {
    private final Component blockName;

    public BlockDamageSource(Holder<DamageType> type, LivingEntity victim, Component blockName) {
        super(type, victim, victim);
        this.blockName = blockName;
    }

    @Override
    public Component getLocalizedDeathMessage(LivingEntity victim) {
        return Component.translatable("death.attack.hitByBlock", victim.getDisplayName(), blockName);
    }
}
