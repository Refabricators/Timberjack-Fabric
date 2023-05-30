package org.refabricators.timberjack;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import org.refabricators.timberjack.entity.TimberEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Timberjack implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final String MOD_ID = "timberjack-refabricated";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final EntityType<Entity> TIMBER_ENTITY = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(MOD_ID, "timber-entity"),
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, TimberEntity::new)
                .dimensions(EntityDimensions.fixed(0.98f, 0.98f)).build()
        );

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Hello Fabric world!");
    }
}