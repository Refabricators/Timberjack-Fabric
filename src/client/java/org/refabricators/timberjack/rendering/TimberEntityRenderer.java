package org.refabricators.timberjack.rendering;

import org.refabricators.timberjack.Timberjack;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class TimberEntityRenderer extends EntityRenderer<Entity> {

    public TimberEntityRenderer(Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(Entity entity) {
        return new Identifier(Timberjack.MOD_ID, "textures/timberentity.png");
    }
    
}
