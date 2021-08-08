package com.mehmet_27.punishmanager.managers;

import co.aikar.commands.annotation.Optional;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.config.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MessageManager {
    private final PunishmentManager punishmentManager;
    private final Map<String, Configuration> locales;

    public MessageManager(PunishManager plugin) {
        ConfigManager configManager = plugin.getConfigManager();
        punishmentManager = plugin.getPunishmentManager();
        this.locales = configManager.getLocales();
    }

    public List<String> getLayout(String path, String playerName) {
        String language = punishmentManager.getOfflinePlayer(playerName).getLanguage();
        if (locales.containsKey(language)) {
            List<String> messages = locales.get(language).getStringList(path);
            if (messages.size() != 0) {
                return locales.get(language).getStringList(path).stream().map(Utils::color).collect(Collectors.toList());
            } else {
                return locales.get("en").getStringList(path).stream().map(Utils::color).collect(Collectors.toList());
            }
        } else {
            return locales.get("en").getStringList(path).stream().map(Utils::color).collect(Collectors.toList());
        }
    }

    public String getMessage(String path, @Optional String playerName) {
        String language = !"CONSOLE".equals(playerName) ? punishmentManager.getOfflinePlayer(playerName).getLanguage() : "en";
        if (locales.containsKey(language)) {
            String msg = locales.get(language).getString(path);
            if (msg != null && msg.length() != 0) {
                return Utils.color(locales.get(language).getString(path));
            } else {
                return Utils.color(locales.get("en").getString(path));
            }
        } else {
            return Utils.color(locales.get("en").getString(path));
        }
    }
}
