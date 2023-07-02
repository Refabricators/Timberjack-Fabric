package org.refabricators.timberjack.entity;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;
import org.refabricators.timberjack.mixin.FallingBlockEntityAccessor;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TimberEntity extends FallingBlockEntity {

    public static TrackedData<BlockPos> ORIGIN = DataTracker.registerData(TimberEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private BlockState fallingBlock = this.getBlockState();
    private boolean hurtEntities;
    @SuppressWarnings("unused") private boolean log;
    private int fallHurtMax = 40;
    private float fallHurtAmount = 2.0F;
    @SuppressWarnings("unused") private Direction fellingDirection = Direction.UP;


    public TimberEntity(EntityType<? extends FallingBlockEntity> type, World world) {
        super(type, world);
       
    }

    public TimberEntity(World worldIn, double x, double y, double z, BlockState fallingBlockState, Direction fellingDirection, boolean log, EntityType<? extends FallingBlockEntity> type, Block logBlock) {
        super(type, worldIn);
        ((FallingBlockEntityAccessor)this).setBlock(logBlock.getDefaultState());
        this.fallingBlock = fallingBlockState;
        this.fellingDirection = fellingDirection;
        this.log = log;
        this.setPosition(x, y + (double) ((1.0F - this.getDimensions(this.getPose()).height) / 2.0F), z);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.movementMultiplier = new Vec3d(0.0D, 0.0D, 0.0D);
        this.setOrigin(this.getBlockPos());
    }

    @Override
    public void onDestroyedOnLanding(Block block, BlockPos pos) {
       if(block instanceof LeavesBlock) Block.dropStacks(block.getDefaultState(), this.getWorld(), pos);
    }

    public Vec3d getMovementMultiplier() {
        return this.movementMultiplier;
    }

    public void setMovementMultiplier(Vec3d movementMultiplier) {
        this.movementMultiplier = movementMultiplier;
    }

    public void setMovementMultiplier(double x, double y, double z) {
        Vec3d movementMultiplier = new Vec3d(x, y, z);
        this.setMovementMultiplier(movementMultiplier);
    }

    {
        this.intersectionChecked = true;
    }

    public void setOrigin(BlockPos pos) {
        this.dataTracker.set(ORIGIN, pos);
    }

    public BlockPos getOrigin() {
        return this.dataTracker.get(ORIGIN);
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    public void fall(float distance, float damageMultiplier) {
        if (this.hurtEntities) {
            int i = MathHelper.ceil(distance - 1.0F);

            if (i > 0) {
                ArrayList<Entity> list = Lists.newArrayList(this.getWorld().getOtherEntities(this, this.getBoundingBox()));

                for (Entity entity : list) {
                    entity.damage(entity.getWorld().getDamageSources().fallingBlock(this), (float) Math.min(MathHelper.floor((float) i * this.fallHurtAmount), this.fallHurtMax));
                }
            }
        }
    }

    public boolean isLog(Block block) {
        return block == Blocks.OAK_LOG || block == Blocks.BIRCH_LOG || block == Blocks.SPRUCE_LOG
        || block == Blocks.ACACIA_LOG || block == Blocks.DARK_OAK_LOG || block == Blocks.JUNGLE_LOG;
    }

    public void setDead() {
        this.setRemoved(RemovalReason.DISCARDED);
    }


    
    @Override
    protected void initDataTracker() {
       super.initDataTracker();
       this.dataTracker.startTracking(ORIGIN, BlockPos.ORIGIN);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound var1) {
        
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        
    }

    @Nullable
    public BlockState getBlock() {
        return this.fallingBlock;
    }

    @Override
    public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        super.updatePositionAndAngles(x, y, z, yaw, pitch);
    }
    
}
