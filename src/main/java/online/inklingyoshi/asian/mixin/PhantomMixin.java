package online.inklingyoshi.asian.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Phantom.class)
public class PhantomMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void setHomelessName(EntityType<? extends Phantom> type, Level level, CallbackInfo ci) {
        Phantom self = (Phantom) (Object) this;
        self.setCustomName(Component.literal("Proof that you are homeless"));
        self.setCustomNameVisible(true);
    }
}
