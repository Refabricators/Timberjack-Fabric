package org.refabricators.timberjack.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.refabricators.timberjack.Timberjack;

import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import net.fabricmc.loader.api.FabricLoader;

import com.google.gson.Gson;

public class JsonOperations {
    public static Config config;
    private final static Path configPath = FabricLoader.getInstance().getConfigDir().resolve("timberjack.json");
    private final static Gson gson = new GsonBuilder().registerTypeAdapter(Config.class, new ConfigTypeAdapter()).setPrettyPrinting().create();

    public static Config loadConfigFromFile() {
        try (JsonReader reader = new JsonReader(Files.newBufferedReader(configPath))) { 

            if (Files.notExists(configPath)) writeConfig();
            config = gson.fromJson(reader, Config.class);
                 
        } catch (Exception e) {

            Timberjack.LOGGER.info("Error while loading config, maybe you should check the mod's config file to see if it has any syntax errors.");
            e.printStackTrace();
            throw new RuntimeException("(timberjack-refabricated) Error while loading config, maybe you should check the mod's config file to see if it has any syntax errors.");

        }
        return config;
    }

    public static void writeConfig() {
        try {

            Files.deleteIfExists(configPath);
            Files.write(configPath, gson.toJson(config).getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
