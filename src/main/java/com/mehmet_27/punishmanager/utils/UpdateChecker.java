package com.mehmet_27.punishmanager.utils;

import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    private final Plugin plugin;
    private final int resourceId;

    public UpdateChecker(Plugin plugin, int resourceId){
        this.plugin = plugin;
        this.resourceId = resourceId;

        plugin.getLogger().info("Checking for new updates");
        getVersion(version -> {
            if (plugin.getDescription().getVersion().equals(version)){
                plugin.getLogger().info("No new update found");
            }else {
                plugin.getLogger().info("New version found: " + version);
                plugin.getLogger().info("Download here: https://www.spigotmc.org/resources/96062/");
            }
        });
    }

    public void getVersion(final Consumer<String> consumer){
        try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)){
            if (scanner.hasNext()){
                consumer.accept(scanner.next());
            }
        } catch (IOException e){
            this.plugin.getLogger().info("There was a problem searching for updates: " + e.getMessage());
        }
    }
}
