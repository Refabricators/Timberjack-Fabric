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
            Config.maxLogsProcessed = in.nextInt();
              break;
            case "canFellLargeTrees":
              Config.canFellLargeTrees = in.nextBoolean();
              break;
            case "aggressiveHouseProtection":
              Config.aggressiveHouseProtection = in.nextBoolean();
              break;
            case "sneakingPreventsFelling":
              Config.sneakingPreventsFelling = in.nextBoolean();
              break;
            case "authors":
                in.beginArray();
                while (in.hasNext()) {
                    Config.logBlacklist.add(in.nextString());
                }
                in.endArray();
              break;
            }
          }
          in.endObject();
          return config;
    }

    @Override
    public void write(final JsonWriter out, final Config config) throws IOException {
        out.beginObject();
        out.name("maxLogsProcessed").value(Config.getMaxLogsProcessed());
        out.name("canFellLargeTrees").value(Config.canFellLargeTrees());
        out.name("aggressiveHouseProtection").value(Config.aggressiveHouseProtection());
        out.name("sneakingPreventsFelling").value(Config.sneakingPreventsFelling());
        out.name("logBlacklist").beginArray();

        for (String logName : Config.logBlacklist) {
            out.value(logName);
        }
        out.endArray();
        out.endObject();
    }
    
}
