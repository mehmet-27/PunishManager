package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MessageManager {
    private final Configuration messages = PunishManager.getInstance().getConfigManager().getMessages();

    public List<String> getLayout(String path) {
        List<String> messages = new ArrayList<>();
        for (String message : this.messages.getStringList(path + ".layout")) {
            messages.add(Utils.color(message));
        }
        return messages;
    }

    public String getPunishedMessage(String command) {
        String path = command.toLowerCase(Locale.ENGLISH) + ".punished";
        return Utils.color(messages.getString(path));
    }

    public String getAlreadyPunishedMessage(String command) {
        String path = command.toLowerCase(Locale.ENGLISH) + ".alreadyPunished";
        return Utils.color(messages.getString(path));
    }

    public String getNotPunishedMessage(String command) {
        String path = command.toLowerCase(Locale.ENGLISH) + ".notPunished";
        return Utils.color(messages.getString(path));
    }
    public String getUnPunishDoneMessage(String command) {
        String path = command.toLowerCase(Locale.ENGLISH) + ".done";
        return Utils.color(messages.getString(path));
    }
    public String getNotOnlineMessage(String command) {
        String path = command.toLowerCase(Locale.ENGLISH) + ".notOnline";
        return Utils.color(messages.getString(path));
    }
}
