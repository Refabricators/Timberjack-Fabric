package org.refabricators.timberjack.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

import com.google.gson.Gson;

public class JsonOperations {
    public static Config config;
    private final static File settingsFile = new File(FabricLoader.getInstance().getConfigDir().toString() + "/timberjack.json");
    private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static Config loadConfigFromFile() {
        try {
            config = gson.fromJson(new String(Files.readAllBytes(settingsFile.toPath()), StandardCharsets.UTF_8),
                Config.class);
        } catch (Exception ex) {
            System.out.println("Error while loading config! Creating a new one!");
            config = new Config();
            writeConfig();
        }

        return null;
    }

    public static void writeConfig() {
        if (settingsFile.exists()) settingsFile.delete();
        try {
            Files.write(settingsFile.toPath(), gson.toJson(config).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
