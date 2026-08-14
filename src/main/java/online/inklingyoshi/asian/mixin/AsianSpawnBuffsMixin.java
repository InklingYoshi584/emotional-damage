package online.inklingyoshi.asian.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import online.inklingyoshi.asian.EmotionalDamage;
import online.inklingyoshi.asian.difficulty.ModDifficulty;
import online.inklingyoshi.asian.util.DifficultyHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class AsianSpawnBuffsMixin {

    @Unique
    private boolean emotionalDamage$asianBuffsApplied;

    @Inject(method = "tick", at = @At("HEAD"))
    private void applyAsianBuffsOnce(CallbackInfo ci) {
        if (emotionalDamage$asianBuffsApplied) return;
        Mob self = (Mob) (Object) this;
        if (!(self instanceof Enemy)) return;
        if (!(self.level() instanceof ServerLevel serverLevel)) return;
        if (DifficultyHelper.getModDifficulty(serverLevel.getServer()) != ModDifficulty.ASIAN_UPPER) return;
        emotionalDamage$asianBuffsApplied = true;

        equipBestGear(self);
        applyEffects(self);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            self.setDropChance(slot, 0.0f);
        }
        self.setBaby(true);
        if (self instanceof Creeper creeper) {
            creeper.getEntityData().set(CreeperPoweredAccessor.emotionalDamage$getPoweredData(), true);
        }

        EmotionalDamage.LOGGER.info("Applied ASIAN spawn buffs to {}", self.getType().getDescriptionId());
    }

    private static void equipBestGear(Mob mob) {
        Holder<Enchantment> protection = enchantment(mob, Enchantments.PROTECTION);
        mob.setItemSlot(EquipmentSlot.HEAD, enchanted(new ItemStack(Items.NETHERITE_HELMET), protection, 4));
        mob.setItemSlot(EquipmentSlot.CHEST, enchanted(new ItemStack(Items.NETHERITE_CHESTPLATE), protection, 4));
        mob.setItemSlot(EquipmentSlot.LEGS, enchanted(new ItemStack(Items.NETHERITE_LEGGINGS), protection, 4));
        mob.setItemSlot(EquipmentSlot.FEET, enchanted(new ItemStack(Items.NETHERITE_BOOTS), protection, 4));

        ItemStack sword = new ItemStack(Items.NETHERITE_SWORD);
        sword.enchant(enchantment(mob, Enchantments.SHARPNESS), 5);
        sword.enchant(enchantment(mob, Enchantments.FIRE_ASPECT), 2);
        sword.enchant(enchantment(mob, Enchantments.UNBREAKING), 3);
        mob.setItemSlot(EquipmentSlot.MAINHAND, sword);
    }

    private static ItemStack enchanted(ItemStack stack, Holder<Enchantment> enchantment, int level) {
        stack.enchant(enchantment, level);
        return stack;
    }

    private static Holder<Enchantment> enchantment(Mob mob, ResourceKey<Enchantment> key) {
        return mob.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
    }

    private static void applyEffects(Mob mob) {
        mob.addEffect(new MobEffectInstance(MobEffects.SPEED, MobEffectInstance.INFINITE_DURATION, 1));
        mob.addEffect(new MobEffectInstance(MobEffects.HASTE, MobEffectInstance.INFINITE_DURATION, 1));
        mob.addEffect(new MobEffectInstance(MobEffects.STRENGTH, MobEffectInstance.INFINITE_DURATION, 1));
        mob.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, MobEffectInstance.INFINITE_DURATION, 1));
        mob.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, MobEffectInstance.INFINITE_DURATION, 1));
        mob.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, MobEffectInstance.INFINITE_DURATION, 0));
        mob.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, MobEffectInstance.INFINITE_DURATION, 0));
        mob.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, MobEffectInstance.INFINITE_DURATION, 0));
        mob.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, MobEffectInstance.INFINITE_DURATION, 0));
        mob.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, MobEffectInstance.INFINITE_DURATION, 0));
        mob.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, MobEffectInstance.INFINITE_DURATION, 0));
        mob.addEffect(new MobEffectInstance(MobEffects.LUCK, MobEffectInstance.INFINITE_DURATION, 0));
        mob.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, MobEffectInstance.INFINITE_DURATION, 0));
        mob.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, MobEffectInstance.INFINITE_DURATION, 0));
    }
}
