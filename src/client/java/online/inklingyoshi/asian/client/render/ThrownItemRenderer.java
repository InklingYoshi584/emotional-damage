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
import online.inklingyoshi.asian.attack.ThrownItemProjectile;

public class ThrownItemRenderer extends EntityRenderer<ThrownItemProjectile, ThrownItemRenderState> {

    private final ItemModelResolver itemModelResolver;

    public ThrownItemRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public ThrownItemRenderState createRenderState() {
        return new ThrownItemRenderState();
    }

    @Override
    public void extractRenderState(ThrownItemProjectile entity, ThrownItemRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        ItemStack carried = entity.getCarriedItem();
        if (!carried.isEmpty()) {
            state.extractItemGroupRenderState(entity, carried, itemModelResolver);
        }
        state.spinAngle = (entity.tickCount + partialTicks) * 30.0f;
    }

    @Override
    public void submit(ThrownItemRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.item.isEmpty()) {
            super.submit(state, poseStack, collector, camera);
            return;
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(state.spinAngle));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));

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
