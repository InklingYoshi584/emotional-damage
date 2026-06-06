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
        PartDefinition root = mesh.getRoot();

        PartPose origin = PartPose.offset(-8.0f, 0.0f, -8.0f);

        root.addOrReplaceChild("sole",
            CubeListBuilder.create()
                .texOffs(6, 4)
                .addBox(5.0f, 0.0f, 2.0f, 5.0f, 1.0f, 12.0f),
            origin);

        root.addOrReplaceChild("toe",
            CubeListBuilder.create()
                .texOffs(0, 5)
                .addBox(5.0f, 1.0f, 4.0f, 5.0f, 1.0f, 3.0f),
            origin);

        root.addOrReplaceChild("heel",
            CubeListBuilder.create()
                .texOffs(0, 6)
                .addBox(5.0f, 1.0f, 4.0f, 5.0f, 1.0f, 3.0f),
            origin);

        return LayerDefinition.create(mesh, 16, 16);
    }
}
