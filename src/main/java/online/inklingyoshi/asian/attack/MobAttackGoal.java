package online.inklingyoshi.asian.attack;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import online.inklingyoshi.asian.difficulty.ModDifficulty;
import online.inklingyoshi.asian.util.DifficultyHelper;
import java.util.EnumSet;
import java.util.List;

public class MobAttackGoal extends Goal {
    private static final int BASE_COOLDOWN_MIN = 100;
    private static final int BASE_COOLDOWN_MAX = 200;
    private static final float ACTIVATION_CHANCE_LOWER = 0.15f;
    private static final float ACTIVATION_CHANCE_UPPER = 0.30f;

    private final Mob mob;
    private int cooldownTicks;
    private int soundDelayTicks;
    private MobInsult pendingInsult;

    public MobAttackGoal(Mob mob) {
        this.mob = mob;
        this.cooldownTicks = 0;
        this.soundDelayTicks = 0;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (cooldownTicks > 0) {
            cooldownTicks--;
            return false;
        }

        if (!(mob.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        if (!DifficultyHelper.isAsianOrHigher(serverLevel.getServer())) {
            return false;
        }

        double maxRangeSq = MobInsults.MAX_RANGE * MobInsults.MAX_RANGE;
        boolean anyoneNearby = false;
        for (Player player : serverLevel.players()) {
            if (player.position().distanceToSqr(mob.position()) <= maxRangeSq) {
                anyoneNearby = true;
                break;
            }
        }
        if (!anyoneNearby) {
            return false;
        }

        ModDifficulty diff = DifficultyHelper.getModDifficulty(serverLevel.getServer());
        float chance = diff == ModDifficulty.ASIAN_UPPER
            ? ACTIVATION_CHANCE_UPPER
            : ACTIVATION_CHANCE_LOWER;
        return mob.getRandom().nextFloat() < chance;
    }

    @Override
    public void start() {
        ServerLevel serverLevel = (ServerLevel) mob.level();
        ModDifficulty diff = DifficultyHelper.getModDifficulty(serverLevel.getServer());

        List<MobInsult> available = MobInsults.getAvailable(diff);
        if (available.isEmpty()) {
            return;
        }

        pendingInsult = available.get(mob.getRandom().nextInt(available.size()));

        String chatMsg = "[" + mob.getName().getString() + "] " + pendingInsult.text();
        Component message = Component.literal(chatMsg).withStyle(ChatFormatting.RED);

        float rangeSq = pendingInsult.range() * pendingInsult.range();
        for (Player player : serverLevel.players()) {
            if (player.position().distanceToSqr(mob.position()) <= rangeSq) {
                player.sendSystemMessage(message);
            }
        }

        if (pendingInsult.sound() != null) {
            Vec3 pos = mob.position();
            serverLevel.playSound(
                null, pos.x, pos.y, pos.z,
                pendingInsult.sound(),
                SoundSource.HOSTILE,
                1.0f, 1.0f
            );
            soundDelayTicks = pendingInsult.soundDurationTicks();
        }
    }

    @Override
    public void tick() {
        if (pendingInsult == null) {
            return;
        }

        if (soundDelayTicks > 0) {
            soundDelayTicks--;
            return;
        }

        ServerLevel serverLevel = (ServerLevel) mob.level();
        float rangeSq = pendingInsult.range() * pendingInsult.range();

        for (Player player : serverLevel.players()) {
            if (player.position().distanceToSqr(mob.position()) <= rangeSq) {
                try {
                    if (pendingInsult.criteria().test(mob, player, serverLevel)) {
                        DamageSource source = ModDamageTypes.emotionalDamageSource(serverLevel, mob);
                        player.hurt(source, Float.MAX_VALUE);
                    }
                } catch (Exception e) {
                    org.slf4j.LoggerFactory.getLogger("emotional-damage").warn("Error evaluating insult criteria", e);
                }
            }
        }

        cooldownTicks = BASE_COOLDOWN_MIN
            + mob.getRandom().nextInt(BASE_COOLDOWN_MAX - BASE_COOLDOWN_MIN);
        pendingInsult = null;
        soundDelayTicks = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return pendingInsult != null;
    }

    @Override
    public void stop() {
        pendingInsult = null;
        soundDelayTicks = 0;
    }
}
