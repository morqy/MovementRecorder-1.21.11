package xyz.yuro.movementrecorder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MovementRecorderConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("movementrecorder.json");

    public static boolean removeStartDelay = true;
    public static boolean removeEndDelay = true;
    public static int rotationType = 0; // 0 = Closest 90°, 1 = Recording's yaw, 2 = Player's yaw (relative)

    private static class ConfigData {
        boolean removeStartDelay = true;
        boolean removeEndDelay = true;
        int rotationType = 0;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                ConfigData data = GSON.fromJson(json, ConfigData.class);
                if (data != null) {
                    removeStartDelay = data.removeStartDelay;
                    removeEndDelay = data.removeEndDelay;
                    rotationType = data.rotationType;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }

    public static void save() {
        ConfigData data = new ConfigData();
        data.removeStartDelay = removeStartDelay;
        data.removeEndDelay = removeEndDelay;
        data.rotationType = rotationType;
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
