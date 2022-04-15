package com.mehmet_27.punishmanager.bungee;

import com.mehmet_27.punishmanager.ConfigurationAdapter;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BungeeConfiguration implements ConfigurationAdapter {

    private final File file;
    private Configuration configuration;

    public BungeeConfiguration(File file){
        this.file = file;
        reload();
    }

    @Override
    public void reload() {
        try {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public String getString(String path) {
        return configuration.getString(path);
    }

    @Override
    public String getString(String path, String def) {
        return configuration.getString(path, def);
    }

    @Override
    public boolean getBoolean(String path) {
        return configuration.getBoolean(path);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return configuration.getBoolean(path, def);
    }

    @Override
    public int getInteger(String path) {
        return configuration.getInt(path);
    }

    @Override
    public int getInteger(String path, int def) {
        return configuration.getInt(path, def);
    }

    @Override
    public long getLong(String path) {
        return configuration.getLong(path);
    }

    @Override
    public long getLong(String path, long def) {
        return configuration.getLong(path, def);
    }

    @Override
    public List<String> getStringList(String path) {
        return configuration.getStringList(path);
    }

    @Override
    public boolean contains(String path) {
        return configuration.contains(path);
    }
}
