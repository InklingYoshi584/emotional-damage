package online.inklingyoshi.asian.attack;

public interface IGunPlayer {
    GunChallengeState emotionalDamage$getGunState();
    void emotionalDamage$setGunState(GunChallengeState state);
    char[] emotionalDamage$getGunButtons();
    void emotionalDamage$setGunButtons(char[] buttons);
    int emotionalDamage$getGunStep();
    void emotionalDamage$setGunStep(int step);
    int emotionalDamage$getGunTimer();
    void emotionalDamage$setGunTimer(int timer);
    int emotionalDamage$getGunAction();
    void emotionalDamage$setGunAction(int action);
}
