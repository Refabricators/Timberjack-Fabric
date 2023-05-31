package org.refabricators.timberjack.config;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;

public class Config {
    public static Config instance = JsonOperations.loadConfigFromFile();

    private static int maxLogsProcessed = 2000;
    private static boolean canFellLargeTrees;
    private static boolean aggressiveHouseProtection = true;
    private static boolean sneakingPreventsFelling = true;
    private static String[] logBlacklistArray = {
            "natura:redwood_logs",
            "biomesoplenty:log_0#4",
            "forestry:logs.6#0",
            "forestry:logs.fireproof.6#0",
    };
    private static Set<String> logBlacklist = Arrays.stream(logBlacklistArray).collect(Collectors.toSet());

    public static int getMaxLogsProcessed() {
        return maxLogsProcessed;
    }

    public static boolean canFellLargeTrees() {
        return canFellLargeTrees;
    }

    public static boolean aggressiveHouseProtection() {
        return aggressiveHouseProtection;
    }

    public static boolean sneakingPreventsFelling() {
        return sneakingPreventsFelling;
    }

    public static boolean canFellLog(BlockState state) {
        String name = Registries.BLOCK.getId(state.getBlock()).toString();
        return !logBlacklist.contains(name);
    }

}
