package org.refabricators.timberjack.events;

import java.util.ArrayList;

import org.refabricators.timberjack.config.Config;
import org.refabricators.timberjack.felling.FellingManager;
import org.refabricators.timberjack.felling.TimberjackUtils;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class BlockBreakEvent {
    public static void registerBlockBreakEvent() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            if ((!player.isSneaking() || !Config.sneakingPreventsFelling()) && TimberjackUtils.isWood(state)) {
                Direction fellingDirection;
                if (world.getRandom().nextFloat() < 0.1) {
                    fellingDirection = getRandomHorizontalFacing();
                } else {
                    fellingDirection = player.getHorizontalFacing();
                    if (fellingDirection.getAxis() == Direction.Axis.Y)
                        fellingDirection = getRandomHorizontalFacing();
                }
                FellingManager.fellingManagers.computeIfAbsent(world, FellingManager::new).onChop(pos, fellingDirection);
            }
        });
    }

    private static Direction getRandomHorizontalFacing() {
        ArrayList<Direction> horizontals = new ArrayList<Direction>();

        horizontals.add(Direction.NORTH);
        horizontals.add(Direction.SOUTH);
        horizontals.add(Direction.EAST);
        horizontals.add(Direction.WEST);

        int randomInt = Random.create().nextBetween(0, 3);

        for (Direction direction : horizontals) {
            if(direction.getHorizontal() == randomInt) return direction;
        }

        return null;
    }
}
