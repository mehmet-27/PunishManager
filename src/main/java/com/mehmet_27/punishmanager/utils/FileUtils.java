package com.mehmet_27.punishmanager.utils;

import com.mehmet_27.punishmanager.PunishManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void copyFileFromResources(PunishManager plugin, File file) {
        try {
            if (!plugin.getDataFolder().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists() && !file.isDirectory()) {
                Files.copy(plugin.getResourceAsStream(file.getName()), file.toPath());
            }
        } catch (IOException ex) {
            plugin.getLogger().severe(String.format("Error while trying to load file {0}: " + ex.getMessage(), file.getName()));
        }
    }

    public static Path createDirectoriesIfNotExists(Path path) {
        if (Files.exists(path) && (Files.isDirectory(path) || Files.isSymbolicLink(path))) {
            return path;
        }
        try {
            Files.createDirectories(path);
        } catch (IOException e){
            throw new RuntimeException("Unable to create directory: " + path, e);
        }
        return path;
    }
}
