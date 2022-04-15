package com.mehmet_27.punishmanager.dependencies;

import com.mehmet_27.punishmanager.PunishManager;

import java.nio.file.Files;
import java.nio.file.Path;

public class DependencyManager {

    private final PunishManager punishManager = PunishManager.getInstance();

    public Path downloadDependency(Dependency dependency, Path file) {
        // if the file already exists, don't attempt to re-download it.
        if (Files.exists(file)) {
            return file;
        }

        // attempt to download the dependency from each repo in order.
        for (Repository repo : Repository.values()) {
            try {
                punishManager.getLogger().info("Downloading " + dependency.getFileName());
                repo.download(dependency, file);
                punishManager.getLogger().info("Successfully downloaded: " + dependency.getFileName());
                if (dependency.equals(Dependency.PROTOCOLIZE_BUNGEECORD)){
                    punishManager.getLogger().severe("Protocolize has been downloaded, please restart the server for it to load.");
                }
                return file;
            } catch (DependencyDownloadException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
