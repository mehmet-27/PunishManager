package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.config.Configuration;

import java.util.List;
import java.util.stream.Collectors;

public class MessageManager {
    private final Configuration messages;

    public MessageManager(PunishManager plugin) {
        messages = plugin.getConfigManager().getMessages();
    }

    public List<String> getLayout(String path) {
        return messages.getStringList(path).stream().map(Utils::color).collect(Collectors.toList());
    }

    public String getMessage(String path) {
        return Utils.color(messages.getString(path));
    }
}
