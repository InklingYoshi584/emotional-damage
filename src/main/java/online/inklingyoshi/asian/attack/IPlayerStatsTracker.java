package online.inklingyoshi.asian.attack;

public interface IPlayerStatsTracker {
    int emotionalDamage$getDayEmeralds();
    void emotionalDamage$addDayEmeralds(int amount);
    void emotionalDamage$resetDayEmeralds();
    long emotionalDamage$getLastDayChecked();
    void emotionalDamage$setLastDayChecked(long day);
    boolean emotionalDamage$hasMissedFullCharge();
    void emotionalDamage$setMissedFullCharge(boolean value);
}
