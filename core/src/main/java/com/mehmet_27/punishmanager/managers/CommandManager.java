package com.mehmet_27.punishmanager.managers;

import co.aikar.commands.CommandIssuer;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public interface CommandManager {

    Set<Class<?>> getClasses(String packageName);

    <T> Set<Class<? extends T>> getClassesBySubType(String packageName, Class<?> type);

    Set<Path> getFilesPath(String path, Predicate<? super Path> filter);

    void registerDependencies();

    void registerCommands();

    void registerConditions();

    void registerContexts();

    void registerCompletions();

    void loadLocaleFiles(List<File> files);

    void updateDefaultLocale();

    //TODO same method name with acf methods String getMessage(CommandIssuer issuer, String key);

    void setup();
}
