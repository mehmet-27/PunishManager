package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.ConfigurationAdapter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public interface ConfigManager {

    void setup();

    ConfigurationAdapter getConfig();

    Map<Locale, Object> getLocales();

    List<Locale> getAvailableLocales();

    List<File> getLocaleFiles();

    String getMessage(String path);

    String getMessage(String path, String playerName);

    String getMessage(String path, String playerName, Function<String, String> placeholders);

    List<String> getStringList(String path);

    List<String> getStringList(String path, String playerName);

    Set<File> getEmbedFiles();

    Map<String, String> getEmbeds();

    String fileToString(File file, String charset) throws IOException;

    String getEmbed(String path);

    Locale getDefaultLocale();

    List<String> getExemptPlayers();

    void copyFileFromResources(File file);
}
