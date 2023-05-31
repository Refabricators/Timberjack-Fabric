package org.refabricators.timberjack.entity;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.Material;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class TimberEntity extends FallingBlockEntity {

    public static TrackedData<BlockPos> ORIGIN = DataTracker.registerData(TimberEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private BlockState fallingBlock = Blocks.SAND.getDefaultState();
    private boolean hurtEntities;
    private boolean log;
    private int fallHurtMax = 40;
    private float fallHurtAmount = 2.0F;
    private NbtCompound tileEntityData;
    private ArrayList<ItemStack> drops = new ArrayList<>();
    private Direction fellingDirection = Direction.UP;
    private boolean isDead = false;


    public TimberEntity(EntityType<? extends FallingBlockEntity> type, World world) {
        super(type, world);
       
    }

    public TimberEntity(World worldIn, double x, double y, double z, BlockState fallingBlockState, Direction fellingDirection, boolean log, EntityType<? extends FallingBlockEntity> type) {
        super(type, worldIn);
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

    protected void entityInit() {
        
    }

    @Override
    public boolean isCollidable() {
        return this.isDead;
    }

    @Override
    public void tick() {
        Block block = this.fallingBlock.getBlock();

        if (this.fallingBlock.getMaterial() == Material.AIR) {
            this.setRemoved(RemovalReason.DISCARDED);
            this.isDead = true;
        } else {
            this.prevX = this.getX();
            this.prevY = this.getY();
            this.prevZ = this.getZ();

            if (this.timeFalling++ == 0) {
                BlockPos currentPos = new BlockPos(this.getBlockPos());

                BlockState state = this.world.getBlockState(currentPos);
                if (state.getBlock() == block) {
                    if (!this.world.isClient() && !log) {
                        drops.addAll(Block.getDroppedStacks(state, (ServerWorld) world, currentPos, null));
                    }
                    this.world.setBlockState(currentPos, Blocks.AIR.getDefaultState());
                } else if (!this.world.isClient()) {
                    this.setRemoved(RemovalReason.DISCARDED);
                    this.isDead = true;
                    return;
                }
            }

            if (!this.hasNoGravity()) {
                this.movementMultiplier = new Vec3d(movementMultiplier.x, movementMultiplier.y - 0.03D, movementMultiplier.z);
            }

            this.move(MovementType.SELF, this.movementMultiplier);
//            this.motionX *= 0.9800000190734863D;
            this.movementMultiplier = new Vec3d(movementMultiplier.x, movementMultiplier.y * 0.98D, movementMultiplier.z);
//            this.motionZ *= 0.9800000190734863D;

            if (!this.world.isClient()) {
                BlockPos currentPos = new BlockPos(getBlockPos());

                BlockPos belowPos = new BlockPos(this.getBlockX(), Double.valueOf(this.getBlockY() - 0.001).intValue(), this.getBlockZ());
                if (this.onGround && isBlocked(belowPos)) {
                    if (canBreakThrough(belowPos)) {
                        world.breakBlock(belowPos, doTileDrops());
                        return;
                    }

                    BlockState occupiedState = this.world.getBlockState(currentPos);

                    this.movementMultiplier = new Vec3d(0.699999988079071D, 0.699999988079071D, -0.5D);

                    if (occupiedState.getBlock() != Blocks.PISTON_HEAD) {
                        this.setRemoved(RemovalReason.DISCARDED);
                        this.isDead = true;

                        if (canPlaceBlock(occupiedState, currentPos) && isBlocked(currentPos.down())
                                && placeBlock(occupiedState, currentPos)) {

                            if (this.tileEntityData != null) {
                                NbtCompound nbt = new NbtCompound();
                                this.writeCustomDataToNbt(nbt);
                                    

                                for (String s : this.tileEntityData.getKeys()) {
                                    NbtElement nbtbase = this.tileEntityData.get(s);

                                    if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s)) {
                                        nbt.put(s, nbtbase.copy());
                                    }
                                }

                                    this.readCustomDataFromNbt(nbt);            
                            }

                            BlockState state = world.getBlockState(currentPos);
                            if (log) {
                                rotateLog(state, currentPos);
                            }

                        } else if (this.dropItem && doTileDrops()) {
                            dropItems();
                        }
                    }
                } else if (this.timeFalling > 100 && (currentPos.getY() < 1 || currentPos.getY() > 256) || this.timeFalling > 400) {
                    if (this.dropItem && doTileDrops()) {
                        dropItems();
                    }

                    this.setRemoved(RemovalReason.DISCARDED);
                    this.isDead = true;
                }
            }
        }
    }

    private boolean placeBlock(BlockState occupiedState, BlockPos currentPos) {
        world.breakBlock(currentPos, doTileDrops());
        return world.setBlockState(currentPos, this.fallingBlock, 3);
    }

    private boolean canPlaceBlock(BlockState occupiedState, BlockPos currentPos) {
        return (world.isInBuildLimit(currentPos) && World.isValid(currentPos))
                || (log && occupiedState.getMaterial() == Material.LEAVES);
    }

    private void rotateLog(BlockState state, BlockPos pos) {
        if (isLog(state.getBlock())) {
            Axis axis = state.get(PillarBlock.AXIS);

            switch (fellingDirection.getAxis()) {
                case X:
                    axis = Direction.Axis.X;
                    break;
                case Z:
                    axis = Direction.Axis.Z;
                    break;
                default:
                    axis = Direction.Axis.Y;
            }
            if (axis != Direction.Axis.Y) {
                BlockState newState = state.with(PillarBlock.AXIS, axis);
                world.setBlockState(pos, newState);
            }
        }
    }

    private boolean doTileDrops() {
        return world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS);
    }

    private boolean isBlocked(BlockPos pos) {
        BlockState state = this.world.getBlockState(pos);
        return !FallingBlock.canFallThrough(state);
    }

    private boolean canBreakThrough(BlockPos pos) {
        BlockState state = this.world.getBlockState(pos);
        Material material = state.getMaterial();
        if (material == Material.PLANT)
            return true;
        if (!log)
            return false;
        return material == Material.LEAVES || material == Material.PLANT || state.getBlock().getDefaultState().getMaterial() == Material.LEAVES;
    }

    private void dropItems() {
        ArrayList<ItemStack> itemsToDrop = new ArrayList<>();
        if (log) {
            itemsToDrop.add(new ItemStack(Items.STICK, this.world.getRandom().nextInt(4) + 1));
        } else {
            itemsToDrop.addAll(this.drops);
        }
        itemsToDrop.forEach(d -> this.dropStack(d, 0.0F));
    }

    public void fall(float distance, float damageMultiplier) {
        if (this.hurtEntities) {
            int i = MathHelper.ceil(distance - 1.0F);

            if (i > 0) {
                ArrayList<Entity> list = Lists.newArrayList(this.world.getOtherEntities(this, this.getBoundingBox()));

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
        this.isDead = true;
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
        nbt.putBoolean("hurtEntities", collidedSoftly);
    }

    @Override
    public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        super.updatePositionAndAngles(x, y, z, yaw, pitch);
    }
    
}
