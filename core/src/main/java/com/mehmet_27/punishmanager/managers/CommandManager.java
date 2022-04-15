package com.mehmet_27.punishmanager.managers;

import java.io.File;
import java.util.List;

public interface CommandManager {

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
