package online.inklingyoshi.asian.client.mixin;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import online.inklingyoshi.asian.client.ClientGunTracker;
import online.inklingyoshi.asian.network.GunPackets;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class GunKeyboardMixin {

    @Inject(method = "keyPress", at = @At("HEAD"))
    private void interceptGunKeys(long window, int action, KeyEvent event, CallbackInfo ci) {
        if (!ClientGunTracker.isActive || ClientGunTracker.inAction) return;
        if (action != GLFW.GLFW_PRESS) return;
        if (event.key() != ClientGunTracker.expectedKeyCode) return;

        ClientPlayNetworking.send(new GunPackets.KeyPressedC2SPayload());
    }
}
