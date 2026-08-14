package online.inklingyoshi.asian.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemStack;
import online.inklingyoshi.asian.attack.ModItems;
import online.inklingyoshi.asian.attack.ThrownSlipper;

public class ThrownSlipperRenderer extends EntityRenderer<ThrownSlipper, ThrownSlipperRenderState> {

    private final ItemModelResolver itemModelResolver;

    public ThrownSlipperRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public ThrownSlipperRenderState createRenderState() {
        return new ThrownSlipperRenderState();
    }

    @Override
    public void extractRenderState(ThrownSlipper entity, ThrownSlipperRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        ItemStack slipper = new ItemStack(ModItems.SLIPPER);
        state.extractItemGroupRenderState(entity, slipper, itemModelResolver);
        var velocity = entity.getDeltaMovement();
        double hSpeed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        state.yRot = (float) Math.toDegrees(Math.atan2(-velocity.x, velocity.z));
        state.xRot = (float) Math.toDegrees(Math.atan2(velocity.y, hSpeed));
        state.spinAngle = entity.isStuckInGround()
            ? entity.getStuckSpinAngle()
            : (entity.tickCount + partialTicks) * 30.0f;
    }

    @Override
    public void submit(ThrownSlipperRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.item.isEmpty()) {
            super.submit(state, poseStack, collector, camera);
            return;
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(state.xRot + 90.0f));
        poseStack.mulPose(Axis.XP.rotationDegrees(state.spinAngle));

        state.item.submit(
            poseStack, collector,
            state.lightCoords,
            OverlayTexture.NO_OVERLAY,
            state.outlineColor
        );

        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }
}
