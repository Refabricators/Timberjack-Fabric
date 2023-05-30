package org.refabricators.timberjack;

import org.refabricators.timberjack.rendering.TimberEntityRenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class TimberjackClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Timberjack.TIMBER_ENTITY, TimberEntityRenderer::new);
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
    }
}