package com.mehmet_27.punishmanager;

import java.util.List;

public interface ConfigurationAdapter {

    void reload();

    String getString(String path);

    String getString(String path, String def);

    boolean getBoolean(String path);

    boolean getBoolean(String path, boolean def);

    int getInteger(String path);

    int getInteger(String path, int def);

    long getLong(String path);

    long getLong(String path, long def);

    List<String> getStringList(String path);

    boolean contains(String path);
}
