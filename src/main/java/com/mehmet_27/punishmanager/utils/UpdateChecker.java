package com.mehmet_27.punishmanager.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mehmet_27.punishmanager.PunishManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class UpdateChecker {
    private final PunishManager plugin;
    private final String currentVersion;
    private String latestVersion;

    public UpdateChecker(PunishManager plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
    }

    public void start() {
        if (plugin.getConfigManager().getConfig().getBoolean("updateChecker", true)){
            plugin.getProxy().getScheduler().schedule(plugin, this::check, 0, 12, TimeUnit.HOURS);
        }
    }

    public void check() {
        plugin.getLogger().info("Checking for new updates...");
        try {
            URL url = new URL("https://api.spiget.org/v2/resources/96062/versions/latest");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());

            JsonElement parse = new JsonParser().parse(reader);
            if (parse.isJsonObject()) {
                latestVersion = parse.getAsJsonObject().get("name").getAsString();
            }

            if (currentVersion.equals(latestVersion)) {
                plugin.getLogger().info("No new update found");
            } else {
                plugin.getLogger().info("New version found: " + latestVersion);
                plugin.getLogger().info("Download here: https://www.spigotmc.org/resources/96062/");
            }

            reader.close();
        } catch (IOException ex) {
            plugin.getLogger().warning("There was a problem searching for updates!");
        }
    }

}
