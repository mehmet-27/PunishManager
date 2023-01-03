package dev.mehmet27.punishmanager.utils;

import dev.mehmet27.punishmanager.PunishManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class FileUtils {

    public static Path createDirectoriesIfNotExists(Path path) {
        if (Files.exists(path) && (Files.isDirectory(path) || Files.isSymbolicLink(path))) {
            return path;
        }
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create directory: " + path, e);
        }
        return path;
    }

    private static Set<Class<?>> getClasses(String packageName) {
        Set<Class<?>> classes = new LinkedHashSet<>();

        Predicate<? super Path> filter = entry -> {
            String path = entry.getFileName().toString();
            return !path.contains("$") && path.endsWith(".class");
        };

        for (Path filesPath : getFilesPath(packageName, filter)) {
            String fileName = filesPath.toString().replace("/", ".").split(".class")[0];
            if (fileName.startsWith(".")){
                fileName = fileName.substring(1);
            }
            try {

                Class<?> clazz = Class.forName(fileName);
                classes.add(clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return classes;
    }

    private static Set<Path> getFilesPath(String path, Predicate<? super Path> filter) {
        Set<Path> files = new LinkedHashSet<>();
        String packagePath = path.replace(".", "/");
        try {
            URI uri = PunishManager.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            FileSystem fileSystem = FileSystems.newFileSystem(URI.create("jar:" + uri), Collections.emptyMap());
            files = Files.walk(fileSystem.getPath(packagePath)).
                    filter(Objects::nonNull).
                    filter(filter).
                    collect(Collectors.toSet());
            fileSystem.close();
        } catch (URISyntaxException | IOException ex) {
            PunishManager.getInstance().getLogger().
                    log(Level.SEVERE, "An error occurred while trying to load files: " + ex.getMessage(), ex);
        }

        return files;
    }

    @SuppressWarnings("unchecked")
    public static <T> Set<Class<? extends T>> getClassesBySubType(String packageName, Class<?> type) {
        return getClasses(packageName).stream().
                filter(type::isAssignableFrom).
                map(aClass -> ((Class<? extends T>) aClass)).
                collect(Collectors.toSet());
    }

    public static Set<File> getFilesIn(String path, Predicate<? super Path> filter) {
        Set<File> files = new LinkedHashSet<>();
        String packagePath = path.replace(".", "/");
        try {
            URI uri = PunishManager.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            FileSystem fileSystem = FileSystems.newFileSystem(URI.create("jar:" + uri), Collections.emptyMap());
            files = Files.walk(fileSystem.getPath(packagePath)).
                    filter(Objects::nonNull).
                    filter(filter).
                    map(p -> new File(p.toString())).
                    collect(Collectors.toSet());
            fileSystem.close();
        } catch (URISyntaxException | IOException ex) {
            PunishManager.getInstance().getLogger().
                    log(Level.SEVERE, "An error occurred while trying to load files: " + ex.getMessage(), ex);
        }
        return files;
    }
}
