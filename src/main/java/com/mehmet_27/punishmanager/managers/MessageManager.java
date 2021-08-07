package com.mehmet_27.punishmanager.managers;

import co.aikar.commands.annotation.Optional;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.config.Configuration;

import java.util.List;
import java.util.stream.Collectors;

public class MessageManager {
    private final ConfigManager configManager;
    private final PunishmentManager punishmentManager;

    public MessageManager(PunishManager plugin) {
        configManager = plugin.getConfigManager();
        punishmentManager = plugin.getPunishmentManager();
    }

    public List<String> getLayout(String path, String playerName) {
        String language = punishmentManager.getOfflinePlayer(playerName).getLanguage();
        if (configManager.getLocales().containsKey(language)) {
            List<String> messages = configManager.getLocales().get(language).getStringList(path);
            if (messages.size() != 0) {
                return configManager.getLocales().get(language).getStringList(path).stream().map(Utils::color).collect(Collectors.toList());
            } else {
                return configManager.getLocales().get("en").getStringList(path).stream().map(Utils::color).collect(Collectors.toList());
            }
        } else {
            return configManager.getLocales().get("en").getStringList(path).stream().map(Utils::color).collect(Collectors.toList());
        }
    }

    public String getMessage(String path, @Optional String playerName) {
        String language = !"CONSOLE".equals(playerName) ? punishmentManager.getOfflinePlayer(playerName).getLanguage() : "en";
        if (configManager.getLocales().containsKey(language)) {
            String msg = configManager.getLocales().get(language).getString(path);
            if (msg != null && msg.length() != 0) {
                return Utils.color(configManager.getLocales().get(language).getString(path));
            } else {
                return Utils.color(configManager.getLocales().get("en").getString(path));
            }
        } else {
            return Utils.color(configManager.getLocales().get("en").getString(path));
        }
    }
}
