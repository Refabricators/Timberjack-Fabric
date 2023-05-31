package org.refabricators.timberjack.config;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;

public class Config {
    
    static int maxLogsProcessed = 2000;
    static boolean canFellLargeTrees = false;
    static boolean aggressiveHouseProtection = true;
    static boolean sneakingPreventsFelling = true;
    static ArrayList<String> logBlacklist = new ArrayList<String>(Arrays.asList("natura:redwood_logs", 
        "biomesoplenty:log_0#4", "forestry:logs.6#0", "forestry:logs.fireproof.6#0"));

    public static Config instance = JsonOperations.loadConfigFromFile();

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
