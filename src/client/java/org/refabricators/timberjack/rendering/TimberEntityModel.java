package org.refabricators.timberjack.rendering;

import org.refabricators.timberjack.entity.TimberEntity;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;

public class TimberEntityModel extends EntityModel<TimberEntity> {

    private final ModelPart base;

    public TimberEntityModel(ModelPart modelPart) {
        this.base = modelPart.getChild(EntityModelPartNames.BODY);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
    	ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.BODY, 

        ModelPartBuilder.create().uv(0, 0)
        .cuboid(-6F, 12F, -6F, 12F, 12F, 12F), 

        ModelTransform.pivot(0F, 0F, 0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(TimberEntity var1, float var2, float var3, float var4, float var5, float var6) {
        
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, 
    float blue, float alpha) {
                ImmutableList.of(this.base).forEach((modelRenderer) -> {
                    modelRenderer.render(matrices, vertices, light, overlay, red, green, blue, alpha);
                });
    }
    
    
}
