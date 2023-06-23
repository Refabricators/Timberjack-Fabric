package org.refabricators.timberjack.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;

@Mixin(FallingBlockEntity.class)
public interface FallingBlockEntityAccessor {
    @Accessor("block")
    public BlockState getBlock();
    @Accessor("block")
    public void setBlock(BlockState block);
}
