package org.refabricators.timberjack.events;

import org.refabricators.timberjack.entity.TimberEntity;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.Entity.RemovalReason;

public class EntityJoinWorldEvent {
    public static void registerEntityJoinWorldEvent() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if(!(entity instanceof FallingBlockEntity)) return;
            FallingBlockEntity falling = (FallingBlockEntity)entity;
            if(isLog(falling.getBlockState().getBlock()) || falling.getBlockState().getMaterial() == Material.LEAVES) {
                
                if (falling.timeFalling > 600) {
                    if(falling instanceof TimberEntity) ((TimberEntity)falling).setDead();
                    else falling.setRemoved(RemovalReason.DISCARDED);
                } 
                
                
            }
        });
    }

    public static boolean isLog(Block block) {
        return block == Blocks.OAK_LOG || block == Blocks.BIRCH_LOG || block == Blocks.SPRUCE_LOG
        || block == Blocks.ACACIA_LOG || block == Blocks.DARK_OAK_LOG || block == Blocks.JUNGLE_LOG;
    }
}
