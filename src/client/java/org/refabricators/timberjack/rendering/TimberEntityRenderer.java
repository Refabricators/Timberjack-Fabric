package org.refabricators.timberjack.rendering;

import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;

public class TimberEntityRenderer extends FallingBlockEntityRenderer {

    public TimberEntityRenderer(Context ctx) {
        super(ctx);
        this.shadowRadius = 0.5f;
    }

    /* 
    @Override
    public void render(TimberEntity entity, float yaw, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light) {

                if(entity.getBlock() == null) return;
                BlockState blockState = entity.getBlock();

                if(!(blockState.getRenderType() == BlockRenderType.MODEL)) return;
                World world = entity.getEntityWorld();

                if(!(blockState != world.getBlockState(entity.getBlockPos()) && blockState.getRenderType() != BlockRenderType.INVISIBLE)) return;

                matrices.push();
                Tessellator tessellator =- Tessellator.getInstance();
                BufferBuilder vertexbuffer = tessellator.getBuffer();

                vertexbuffer.begin(DrawMode.QUADS, VertexFormats.LINES);

                BlockPos blockpos = new BlockPos(Double.valueOf(entity.getX()).intValue(), Double.valueOf(entity.getBoundingBox().maxY).intValue(), Double.valueOf(entity.getZ()).intValue());
                matrices.translate((float) (entity.getX() - (double) blockpos.getX() - 0.5D), (float) (entity.getY() - (double) blockpos.getY()), (float) (entity.getZ() - (double) blockpos.getZ() - 0.5D));
                
                BlockRenderManager renderManager = MinecraftClient.getInstance().getBlockRenderManager();
                renderManager.getModelRenderer().render(world, renderManager.getModel(blockState), blockState, blockpos, matrices, vertexbuffer, false, entity.getWorld().getRandom(), light, light);
                
                tessellator.draw();

                matrices.pop();

                super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);

    }
    */
    
}
