package online.inklingyoshi.asian.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import online.inklingyoshi.asian.EmotionalDamage;
import online.inklingyoshi.asian.attack.ThrownSlipper;

public class ThrownSlipperRenderer extends EntityRenderer<ThrownSlipper, ThrownSlipperRenderState> {

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EmotionalDamage.MOD_ID, "item/slipper");

    private final ThrownSlipperModel model;

    public ThrownSlipperRenderer(EntityRendererProvider.Context context) {
        super(context);
        ModelPart root = ThrownSlipperModel.createLayer().bakeRoot();
        this.model = new ThrownSlipperModel(root);
    }

    @Override
    public ThrownSlipperRenderState createRenderState() {
        return new ThrownSlipperRenderState();
    }

    @Override
    public void extractRenderState(ThrownSlipper entity, ThrownSlipperRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        var velocity = entity.getDeltaMovement();
        double hSpeed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        state.yRot = (float) Math.toDegrees(Math.atan2(-velocity.x, velocity.z));
        state.xRot = (float) Math.toDegrees(Math.atan2(velocity.y, hSpeed));
        state.spinAngle = (entity.tickCount + partialTicks) * 30.0f;
    }

    @Override
    public void submit(ThrownSlipperRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(state.xRot + 90.0f));
        poseStack.mulPose(Axis.XP.rotationDegrees(state.spinAngle));

        collector.order(0).submitModel(
            model, Unit.INSTANCE, poseStack, TEXTURE,
            state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null
        );

        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }
}
