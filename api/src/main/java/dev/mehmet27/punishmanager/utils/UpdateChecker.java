package dev.mehmet27.punishmanager.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.mehmet27.punishmanager.MethodProvider;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.configuration.Configuration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class UpdateChecker {
    private final MethodProvider methods;
    private final Configuration config;
    private final String currentVersion;
    private String latestVersion;

    public UpdateChecker(MethodProvider methods) {
        this.methods = methods;
        this.config = PunishManager.getInstance().getConfigManager().getConfig();
        this.currentVersion = methods.getPluginVersion();
    }

    public void start() {
        if (config.getBoolean("updateChecker", true)){
            methods.scheduleAsync(this::check, 0, 12, TimeUnit.HOURS);
        }
    }

    public void check() {
        methods.getLogger().info("Checking for new updates...");
        try {
            URL url = new URL("https://api.spiget.org/v2/resources/96062/versions/latest");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());

            JsonElement parse = new JsonParser().parse(reader);
            if (parse.isJsonObject()) {
                latestVersion = parse.getAsJsonObject().get("name").getAsString();
            }

            if (currentVersion.equals(latestVersion)) {
                methods.getLogger().info("No new update found");
            } else {
                methods.getLogger().info("New version found: " + latestVersion);
                methods.getLogger().info("Download here: https://www.spigotmc.org/resources/96062/");
            }

            reader.close();
        } catch (IOException ex) {
            methods.getLogger().warning("There was a problem searching for updates!");
        }
    }

}
