package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.PunishManager;

import java.util.UUID;

@CommandAlias("%punishmanager")
@Description("The main command of the plugin.")
public class PunishManagerCommand extends BaseCommand {

    @Dependency
    private PunishManager punishManager;


    @Default
    @Description("{@@punishmanager.description}")
    @Conditions("requireProtocolize")
    public void punishManager(CommandIssuer sender) {
        //Main GUI
        if (!sender.isPlayer()) return;
        Object player = punishManager.getMethods().getPlayer(sender.getUniqueId());
        punishManager.getMethods().openMainInventory(player);
    }

    @Subcommand("%about")
    public void about(CommandIssuer sender) {
        UUID uuid = sender.isPlayer() ? sender.getUniqueId() : null;
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&a&m+                                                        +");
        PunishManager.getInstance().getMethods().sendMessage(uuid, String.format("&6&l%s", PunishManager.getInstance().getMethods().getPluginName()));
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&eThe best punishment plugin for your server.");
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&e");
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&eDeveloper: &bMehmet_27");
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&eVersion: &b" + PunishManager.getInstance().getMethods().getPluginVersion());
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&ePlatform: &b" + PunishManager.getInstance().getMethods().getPlatform().getFriendlyName());
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&eStorage Provider: &b" + PunishManager.getInstance().getStorageManager().getStorageProvider());
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&e");
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&eContributors: &7Minat0_, RoinujNosde");
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&a&m+                                                        +");
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&eUse &a/punishmanager help &efor help.");
    }

    @Subcommand("%help")
    @Description("{@@punishmanager.help.description}")
    public void help(CommandIssuer sender, CommandHelp help) {
        help.showHelp();
    }
}
