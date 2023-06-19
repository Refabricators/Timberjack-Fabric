package org.refabricators.timberjack.config;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ConfigTypeAdapter extends TypeAdapter<Config> {

    @Override
    public Config read(JsonReader in) throws IOException {
        Config config = new Config();
        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
            case "maxLogsProcessed":
            config.maxLogsProcessed = in.nextInt();
              break;
            case "canFellLargeTrees":
              config.canFellLargeTrees = in.nextBoolean();
              break;
            case "aggressiveHouseProtection":
              config.aggressiveHouseProtection = in.nextBoolean();
              break;
            case "sneakingPreventsFelling":
              config.sneakingPreventsFelling = in.nextBoolean();
              break;
            case "logBlacklist":
                in.beginArray();
                while (in.hasNext()) {
                    config.logBlacklist.add(in.nextString());
                }
                in.endArray();
              break;
            }
          }
          in.endObject();
          return config;
    }

    @Override
    public void write(final JsonWriter out, Config config) throws IOException {
        out.beginObject();
        out.name("maxLogsProcessed").value(config.maxLogsProcessed);
        out.name("canFellLargeTrees").value(config.canFellLargeTrees);
        out.name("aggressiveHouseProtection").value(config.aggressiveHouseProtection);
        out.name("sneakingPreventsFelling").value(config.sneakingPreventsFelling);
        out.name("logBlacklist").beginArray();

        for (String logName : config.logBlacklist) {
            out.value(logName);
        }
        out.endArray();
        out.endObject();
    }
    
}
