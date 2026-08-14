package online.inklingyoshi.asian.client;

import online.inklingyoshi.asian.network.GunPackets;

public final class ClientGunTracker {
    private ClientGunTracker() {}

    public static boolean isActive = false;
    public static int expectedKeyCode = 0;
    public static char buttonChar = 0;
    public static boolean inAction = false;
    public static String actionText = "";
    public static float timerFraction = 0f;

    public static void setButton(char b, int code) {
        isActive = true;
        inAction = false;
        buttonChar = b;
        expectedKeyCode = code;
        timerFraction = 0f;
    }

    public static void setAction(int id) {
        inAction = true;
        actionText = GunPackets.getActionText(id);
        timerFraction = 0f;
    }

    public static void clear() {
        isActive = false;
        inAction = false;
        buttonChar = 0;
        actionText = "";
        timerFraction = 0f;
    }
}
