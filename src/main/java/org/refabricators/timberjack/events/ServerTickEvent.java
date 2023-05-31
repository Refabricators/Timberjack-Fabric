package org.refabricators.timberjack.events;

import org.refabricators.timberjack.felling.FellingManager;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class ServerTickEvent {
    public static void registerServerTickEvent() {
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            if(world.isClient) return;

            FellingManager fellingManager = FellingManager.fellingManagers.get(world);
            if (fellingManager != null) {
                fellingManager.tick();
                if (fellingManager.isEmpty())
                    FellingManager.fellingManagers.remove(world);
            }
        });
    }
}
