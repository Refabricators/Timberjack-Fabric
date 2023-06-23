package org.refabricators.timberjack.felling;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import java.util.HashSet;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.refabricators.timberjack.config.Config;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import com.google.common.collect.MapMaker;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FellingManager {
    public static final Map<World, FellingManager> fellingManagers = new MapMaker().weakKeys().makeMap();
    private final Collection<Tree> fellQueue = new LinkedList<>();
    private final World world;

    public FellingManager(World world) {
        this.world = world;
    }

    public boolean isEmpty() {
        return fellQueue.isEmpty();
    }

    public int treesQueuedToFell() {
        return fellQueue.size();
    }

    public int logsQueuedToFell() {
        int logs = 0;
        for (Tree tree : fellQueue) {
            logs += tree.logsQueuedToFell();
        }
        return logs;
    }

    public void tick() {
        //            long worldTime = event.world.getTotalWorldTime();
        //            if (worldTime % 4 != 0)
        //                return;
        //        if(!fellQueue.isEmpty())
        //            System.out.printf("Felling %d trees\n", fellQueue.size());
        fellQueue.removeIf(tree -> !tree.hasLogsToFell());
        fellQueue.forEach(tree -> {
        tree.prepForFelling();
        Iterator<BlockPos> it = tree.logsToFell.iterator();
        if (it.hasNext()) {
            BlockPos log = it.next();
            tree.fellLog(log);
            it.remove();
        }
    });
}

public void onChop(BlockPos pos, Direction fellingDirection, Block logBlock) {
    Tree tree = new Tree(pos, fellingDirection, logBlock);
    tree.buildTree();
    tree.queueForFelling();
}

public class Tree {

    private Collection<Branch> branches = new ConcurrentLinkedQueue<>();
    private Block logBlock;
    private HashSet<BlockPos> logs = new HashSet<>();
    LinkedList<BlockPos> logsToFell = new LinkedList<>();
    private LinkedList<BlockPos> newLogsToFell = new LinkedList<>();
    private final BlockPos choppedBlock;
    private Vec3d centroid = Vec3d.ZERO;
    private boolean isTreehouse;
    private final Direction fellingDirection;

    Tree(BlockPos choppedBlock, Direction fellingDirection, Block logBlock) {
        this.choppedBlock = choppedBlock;
        this.fellingDirection = fellingDirection;
        this.logBlock = logBlock;
        makeBranch(choppedBlock, logBlock);
    }

    boolean contains(BlockPos pos) {
        return logs.contains(pos);
    }
    int size() {
        return logs.size();
    }

    int logsQueuedToFell() {
        return logsToFell.size() + newLogsToFell.size();
    }

    void addLogsToFell(Collection<BlockPos> logs) {
        newLogsToFell.addAll(logs);
    }

    void prepForFelling() {
        if (!newLogsToFell.isEmpty()) {
            logsToFell.addAll(newLogsToFell);
            updateCentroid();
            logsToFell.sort((o1, o2) -> {
                int yCompare = Integer.compare(o1.getY(), o2.getY());
                if (yCompare != 0)
                    return yCompare;
                int distCompare = Double.compare(centroid.squaredDistanceTo(o2.getX(), o2.getY(), o2.getZ()),
                        centroid.squaredDistanceTo(o1.getX(), o1.getY(), o1.getZ()));
                if (distCompare != 0)
                    return distCompare;
                return o1.compareTo(o2);
            });
            newLogsToFell.clear();
        }
    }

    void updateCentroid() {
        double x = 0;
        double y = 0;
        double z = 0;
        for (BlockPos pos : logs) {
            x += pos.getX();
            y += pos.getY();
            z += pos.getZ();
        }
        int size = logs.size();
        x /= size;
        y /= size;
        z /= size;
        centroid = new Vec3d(x, y, z);
    }

    boolean hasLogsToFell() {
        return !isTreehouse && (!logsToFell.isEmpty() || !newLogsToFell.isEmpty());
    }

    private void buildTree() {
        TimberjackUtils.iterateBlocks(1, choppedBlock, targetPos -> {
            if (!contains(targetPos)) {
                BlockState targetState = world.getBlockState(targetPos);
                if (TimberjackUtils.isWood(targetState)) {
                    scanNewBranch(targetPos.toImmutable(), logBlock);
                }
            }
        });
    }

    private void scanNewBranch(BlockPos pos, Block logBlock) {
        Branch branch = makeBranch(pos, logBlock);
        branch.scan();
    }

    private void queueForFelling() {
        fellQueue.add(this);
    }

    private void fellLog(BlockPos logPos) {
        TimberjackUtils.spawnFallingLog(world, logPos, centroid, fellingDirection, logBlock);
        TimberjackUtils.iterateBlocks(4, logPos, targetPos -> {
            BlockState targetState = world.getBlockState(targetPos);
            if (TimberjackUtils.isLeaves(targetState)) {
                TimberjackUtils.spawnFallingLeaves(world, targetPos, logPos, centroid, targetState, fellingDirection, targetState.getBlock());
            } else if (TimberjackUtils.isWood(targetState) && !contains(targetPos)) {
                scanNewBranch(targetPos.toImmutable(), logBlock);
            }
        });
    }

    Branch makeBranch(BlockPos pos, Block logBlock) {
        Branch branch = new Branch(this, pos, logBlock);
        branches.add(branch);
        return branch;
    }

}

public class Branch {

    private HashSet<BlockPos> logs = new HashSet<>();
    private Block logBlock;
    private final Tree tree;
    private final BlockPos start;
    private boolean hasLeaves;
    private boolean rooted;

    Branch(Tree tree, BlockPos start, Block logBlock) {
        this.logBlock = logBlock;
        this.tree = tree;
        this.start = start;
        addLog(new BlockPos.Mutable(start.getX(), start.getY(), start.getZ()));
    }

    private void scan() {
        expandLogs(start);
        if (hasLeaves && !rooted && (tree.size() < Config.getMaxLogsProcessed() || Config.canFellLargeTrees()))
            tree.addLogsToFell(logs);
    }

    private BlockPos addLog(BlockPos.Mutable targetPos) {
        BlockPos immutable = targetPos.toImmutable();
        logs.add(immutable);
        tree.logs.add(immutable);
        if (!rooted) {
            targetPos.move(Direction.DOWN);
            if (!tree.contains(targetPos)) {
                BlockState targetState = world.getBlockState(targetPos);
                if (TimberjackUtils.isDirt(targetState)) {
                    rooted = true;
                }
            }
        }
        return immutable;
    }

    private void expandLogs(BlockPos root) {
        if (tree.size() >= Config.getMaxLogsProcessed())
            return;

        ArrayDeque<BlockPos> logsToExpand = new ArrayDeque<>();
        logsToExpand.add(root);
        BlockPos nextBlock;
        while ((nextBlock = logsToExpand.poll()) != null && !tree.isTreehouse) {
            TimberjackUtils.iterateBlocks(1, nextBlock, targetPos -> {
                if (!tree.contains(targetPos)) {
                    BlockState targetState = world.getBlockState(targetPos);
                    if (TimberjackUtils.isWood(targetState)) {
                        if (tree.size() < Config.getMaxLogsProcessed()) {
                            logsToExpand.addLast(addLog(targetPos));
                        }
                    } else if (!hasLeaves && TimberjackUtils.isLeaves(targetState)) {
                        hasLeaves = true;
                    } else if (TimberjackUtils.isHouse(targetState, world, targetPos)) {
                        tree.isTreehouse = true;
                    }
                }
            });
        }
    }

}


}
