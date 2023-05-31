package org.refabricators.timberjack.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

import com.google.gson.Gson;

public class JsonOperations {
    public static Config config;
    private final static String configFilePath = FabricLoader.getInstance().getConfigDir().toString() + "/timberjack.json";
    private final static Gson gson = new GsonBuilder().registerTypeAdapter(Config.class, new ConfigTypeAdapter()).setPrettyPrinting().create();

    public static Config loadConfigFromFile() {
        try {
            config = gson.fromJson(configFilePath,
                Config.class);
        } catch (Exception ex) {
            System.out.println("Error while loading config! Creating a new one!");
            config = new Config();
            writeConfig();
        }

        return null;
    }

    public static void writeConfig() {
        File settingsFile = new File(configFilePath);
        if (settingsFile.exists()) settingsFile.delete();
        try {
            Files.write(Path.of(configFilePath), gson.toJson(config).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
