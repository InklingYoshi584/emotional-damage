package online.inklingyoshi.asian.client.render;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Unit;

public class ThrownSlipperModel extends Model<Unit> {

    public ThrownSlipperModel(ModelPart root) {
        super(root, RenderTypes::entitySolid);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild("slipper",
            CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-2.5f, -0.5f, -6.0f, 5.0f, 1.0f, 12.0f),
            PartPose.ZERO);
        return LayerDefinition.create(mesh, 16, 16);
    }
}
