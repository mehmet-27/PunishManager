package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.utils.ProtocolizeUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static com.mehmet_27.punishmanager.utils.Utils.sendColoredTextComponent;

@CommandAlias("punishmanager")
@Description("The main command of the plugin.")
public class PunishManagerCommand extends BaseCommand {

    @Dependency
    private PunishManager punishManager;

    @Default
    @Description("{@@punishmanager.description}")
    public void punishManager(CommandSender sender) {
        sendColoredTextComponent(sender, "&a&m+                                                        +");
        sendColoredTextComponent(sender, String.format("&6&l%s &7%s", punishManager.getDescription().getName(), punishManager.getDescription().getVersion()));
        sendColoredTextComponent(sender, "&eThe best punishment plugin for your server.");
        sendColoredTextComponent(sender, "&eDeveloped by &bMehmet_27.");
        sendColoredTextComponent(sender, "&eContributors: &7Minat0_, RoinujNosde");
        sendColoredTextComponent(sender, "&a&m+                                                        +");
        sendColoredTextComponent(sender, "&eUse &a/punishmanager help &efor help.");
    }

    @Subcommand("reload")
    @Description("{@@punishmanager-reload.description}")
    @CommandPermission("punishmanager.command.punishmanager.reload")
    public void reload(CommandSender sender) {
        punishManager.getConfigManager().setup();
        punishManager.getCommandManager().updateDefaultLocale();
        String done = punishManager.getConfigManager().getMessage("punishmanager-reload.done", sender.getName());
        sendColoredTextComponent(sender, String.format("&6%s &7» " + done , punishManager.getDescription().getName()));
    }

    @Subcommand("gui")
    @CommandPermission("punishmanager.command.punishmanager.gui")
    @Conditions("requireProtocolize")
    public void gui(ProxiedPlayer sender) {
        //Main GUI
        ProtocolizeUtils.openMainInventory(sender);
    }

    @Subcommand("help")
    @Description("{@@punishmanager-help.description}")
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
