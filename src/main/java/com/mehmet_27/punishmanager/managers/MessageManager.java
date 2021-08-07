package com.mehmet_27.punishmanager.managers;

import co.aikar.commands.annotation.Optional;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.config.Configuration;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MessageManager {
    private final ConfigManager configManager;
    private final Configuration messages;
    private final PunishmentManager punishmentManager;

    public MessageManager(PunishManager plugin) {
        configManager = plugin.getConfigManager();
        messages = plugin.getConfigManager().getMessages();
        punishmentManager = plugin.getPunishmentManager();
    }

    public List<String> getLayout(String path) {
        return messages.getStringList(path).stream().map(Utils::color).collect(Collectors.toList());
    }

    public String getMessage(String path, @Optional String player) {
        String language = !player.equals("CONSOLE") ? punishmentManager.getOfflinePlayer(player).getLanguage().split("_")[0] : "en";
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
