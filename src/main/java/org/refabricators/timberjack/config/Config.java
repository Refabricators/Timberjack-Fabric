package org.refabricators.timberjack.config;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;

public class Config {
    
    int maxLogsProcessed = 2000;
    boolean canFellLargeTrees = false;
    boolean aggressiveHouseProtection = true;
    boolean sneakingPreventsFelling = true;
    ArrayList<String> logBlacklist = new ArrayList<String>(Arrays.asList("natura:redwood_logs", 
        "biomesoplenty:log_0#4", "forestry:logs.6#0", "forestry:logs.fireproof.6#0"));

    public static Config instance = JsonOperations.loadConfigFromFile();

    public static int getMaxLogsProcessed() {
        return instance.maxLogsProcessed;
    }

    public static boolean canFellLargeTrees() {
        return instance.canFellLargeTrees;
    }

    public static boolean aggressiveHouseProtection() {
        return instance.aggressiveHouseProtection;
    }

    public static boolean sneakingPreventsFelling() {
        return instance.sneakingPreventsFelling;
    }

    public static boolean canFellLog(BlockState state) {
        String name = Registries.BLOCK.getId(state.getBlock()).toString();
        return !instance.logBlacklist.contains(name);
    }

}
