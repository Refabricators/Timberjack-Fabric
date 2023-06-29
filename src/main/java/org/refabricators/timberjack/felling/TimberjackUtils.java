package org.refabricators.timberjack.felling;

import java.util.HashSet;
import java.util.function.Consumer;

import org.joml.Vector3d;
import org.refabricators.timberjack.Timberjack;
import org.refabricators.timberjack.config.Config;
import org.refabricators.timberjack.entity.TimberEntity;

import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.GrindstoneBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.SmithingTableBlock;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class TimberjackUtils {
    private static HashSet<Class<? extends Block>> houseBlocks = new HashSet<>();

    static {
        houseBlocks.add(DoorBlock.class);
        houseBlocks.add(BedBlock.class);
        houseBlocks.add(CraftingTableBlock.class);
        houseBlocks.add(AnvilBlock.class);
        houseBlocks.add(GrindstoneBlock.class);
        houseBlocks.add(SmithingTableBlock.class);
        houseBlocks.add(BrewingStandBlock.class);
        houseBlocks.add(SignBlock.class);
        houseBlocks.add(FurnaceBlock.class);
        houseBlocks.add(TrapdoorBlock.class);
        houseBlocks.add(GlassBlock.class);
        houseBlocks.add(StainedGlassBlock.class);
        houseBlocks.add(PaneBlock.class);
        houseBlocks.add(StainedGlassPaneBlock.class);

        houseBlocks.add(RedstoneWireBlock.class);
        houseBlocks.add(RedstoneTorchBlock.class);
        houseBlocks.add(ComparatorBlock.class);
        houseBlocks.add(RepeaterBlock.class);
        houseBlocks.add(ObserverBlock.class);
        houseBlocks.add(HopperBlock.class);
        houseBlocks.add(ChestBlock.class);

        houseBlocks.add(PistonBlock.class);
        houseBlocks.add(PistonExtensionBlock.class);
        houseBlocks.add(PistonHeadBlock.class);

        houseBlocks.add(RedstoneLampBlock.class);
        houseBlocks.add(CarpetBlock.class);
    }

    static void iterateBlocks(int range, BlockPos center, Consumer<BlockPos.Mutable> action) {
        BlockPos.Mutable targetPos = new BlockPos.Mutable();
        for (int y = -range; y <= range; ++y) {
            for (int x = -range; x <= range; ++x) {
                for (int z = -range; z <= range; ++z) {
                    targetPos.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    action.accept(targetPos);
                }
            }
        }
    }

    public static boolean isWood(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.OAK_LOG || block == Blocks.BIRCH_LOG || block == Blocks.SPRUCE_LOG
        || block == Blocks.ACACIA_LOG || block == Blocks.DARK_OAK_LOG || block == Blocks.JUNGLE_LOG
        || block == Blocks.CHERRY_LOG;
    }

    static boolean isLeaves(BlockState state) {
        return state.getBlock() instanceof LeavesBlock;
    }

    static boolean isDirt(BlockState state) {
        return state.isIn(BlockTags.DIRT) || state.isOf(Blocks.FARMLAND);
    }

    static boolean isHouse(BlockState state, World world, BlockPos pos) {
        if (!Config.aggressiveHouseProtection())
            return false;
        Class<?> blockClass = state.getBlock().getClass();
        while (blockClass != Block.class) {
            if (houseBlocks.contains(blockClass))
                return true;
            blockClass = blockClass.getSuperclass();
        }
        return false;
    }

    static void spawnFallingLog(World world, BlockPos logPos, Vec3d centroid, Direction fellingDirection, Block logBlock) {
        spawnFalling(world, logPos, centroid, world.getBlockState(logPos), fellingDirection, true, logBlock);
    }

    static void spawnFallingLeaves(World world, BlockPos.Mutable pos, BlockPos logPos, Vec3d centroid, BlockState state, Direction fellingDirection, Block logBlock) {
        pos.move(Direction.DOWN);
        BlockState belowState = world.getBlockState(pos);
        boolean canFall = belowState.isAir()
                || belowState.getBlock() instanceof Fertilizable
                || belowState.isReplaceable()
                || logPos.equals(pos);
        pos.move(Direction.UP);

        if (canFall) spawnFalling(world, pos, centroid, state, fellingDirection, false, logBlock);
    }

    private static void spawnFalling(World world, BlockPos pos, Vec3d centroid, BlockState state, Direction fellingDirection, boolean log, Block logBlock) {
        world.breakBlock(pos, false);
        TimberEntity entity = new TimberEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, state, fellingDirection, log, Timberjack.TIMBER_ENTITY, logBlock);
        Vec3d vector = new Vec3d(pos.getX(), 0, pos.getZ());
        vector = vector.subtract(centroid.x, 0, centroid.z);

        Vector3d vector3d = new Vector3d(vector.getX(), vector.getY(), vector.getZ());
        vector3d = vector3d.normalize().normalize(0.5);
        vector = new Vec3d(vector3d.x, vector3d.y, vector3d.z);

        if (fellingDirection.getAxis() != Direction.Axis.Y) {
            Vec3i vectori = fellingDirection.getVector();
            Vec3d vectord = new Vec3d(vectori.getX(), vectori.getY(), vectori.getZ());
            vector = vector.add(vectord);
        }
            
        vector = vector.normalize();
        entity.setMovementMultiplier(vector.x * 0.3 + (world.getRandom().nextFloat() - 0.5) * 0.15, entity.getMovementMultiplier().getY(), vector.z * 0.3 + (world.getRandom().nextFloat() - 0.5) * 0.15);
        if(log) entity.setHurtEntities(2.0f, 40);
        if(!log) entity.setHurtEntities(0f, 0);
        world.spawnEntity(entity);
    }



}
