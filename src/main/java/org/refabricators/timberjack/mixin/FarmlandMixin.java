package org.refabricators.timberjack.mixin;

import org.jetbrains.annotations.Nullable;
import org.refabricators.timberjack.entity.TimberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandMixin {
    
    @Inject(method = "setToDirt", at = @At("HEAD"), cancellable = true)
    private void dontTrample(@Nullable Entity entity, BlockState state, World world, BlockPos pos, CallbackInfo ci) {
        if(entity instanceof TimberEntity) ci.cancel();
    }
}
