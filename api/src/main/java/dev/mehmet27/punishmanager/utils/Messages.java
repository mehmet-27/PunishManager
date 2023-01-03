package dev.mehmet27.punishmanager.utils;

import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.ConfigManager;

import java.util.List;
import java.util.UUID;

public enum Messages {
    GUI_BACKBUTTON_NAME("gui.backbutton.name"),
    GUI_MAIN_TITLE("gui.main.title"),
    GUI_MAIN_LANGUAGESELECTOR_NAME("gui.main.languageselector.name"),
    GUI_MAIN_LANGUAGESELECTOR_LORE("gui.main.languageselector.lore"),
    GUI_MAIN_ADMINPANEL_NAME("gui.main.adminpanel.name"),
    GUI_MAIN_ADMINPANEL_LORE("gui.main.adminpanel.lore"),
    GUI_MAIN_MANAGEPUNISHMENTS_NAME("gui.main.managepunishments.name"),
    GUI_MAIN_MANAGEPUNISHMENTS_LORE("gui.main.managepunishments.lore"),
    GUI_ADMINPANEL_TITLE("gui.adminpanel.title"),
    GUI_ADMINPANEL_RELOAD_NAME("gui.adminpanel.reload.name"),
    GUI_ADMINPANEL_DEFAULTLANGUAGESELECTOR_NAME("gui.adminpanel.defaultlanguageselector.name"),
    GUI_MANAGEPUNISHMENTS_TITLE("gui.managepunishments.title"),
    GUI_MANAGEPUNISHMENTS_PUNISHMENT_NAME("gui.managepunishments.punishment.name"),
    GUI_MANAGEPUNISHMENTS_PUNISHMENT_LORE("gui.managepunishments.punishment.lore"),
    GUI_MANAGEPUNISHMENTS_NEXT_NAME("gui.managepunishments.next.name"),
    GUI_MANAGEPUNISHMENTS_PREVIOUS_NAME("gui.managepunishments.previous.name"),
    GUI_LANGUAGESELECTOR_TITLE("gui.languageselector.title"),
    GUI_DEFAULTLANGUAGESELECTOR_TITLE("gui.defaultlanguageselector.title"),
    GUI_CONFIRMATION_TITLE("gui.confirmation.title"),
    GUI_CONFIRMATION_CONFIRM_NAME("gui.confirmation.confirm.name"),
    GUI_CONFIRMATION_RETURN_NAME("gui.confirmation.return.name"),
    GUI_PUNISH_TITLE("gui.punish.title"),
    GUI_PUNISH_INFO_NAME("gui.punish.info.name"),
    GUI_PUNISH_TEMPLATE_NAME("gui.punish.template.name"),
    GUI_PUNISH_TEMPLATE_LORE("gui.punish.template.lore");

    private final String path;

    Messages(String path) {
        this.path = path;
    }

    public String getString(UUID uuid) {
        ConfigManager configManager = PunishManager.getInstance().getConfigManager();
        return configManager.getMessage(path, uuid);
    }

    public String getString() {
        ConfigManager configManager = PunishManager.getInstance().getConfigManager();
        return configManager.getMessage(path);
    }

    public List<String> getStringList(UUID uuid) {
        ConfigManager configManager = PunishManager.getInstance().getConfigManager();
        return configManager.getStringList(path, uuid);
    }

    public String getPath() {
        return path;
    }
}
