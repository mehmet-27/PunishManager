package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

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
        sendColoredTextComponent(sender, "&eDeveloped by Mehmet_27.");
        sendColoredTextComponent(sender, "&eContributors: &7Minat0_, RoinujNosde");
        sendColoredTextComponent(sender, "&6Contact");
        sendColoredTextComponent(sender, "&eDiscord: &7https://discord.gg/abRq8Fan5E");
        sendColoredTextComponent(sender, "&a&m+                                                        +");
        sendColoredTextComponent(sender, "&eUse &a/punishmanager help &efor help.");
    }

    @Subcommand("reload")
    @Description("{@@punishmanager_reload.description}")
    @CommandPermission("punishmanager.command.punishmanager.reload")
    public void reload(CommandSender sender) {
        punishManager.getConfigManager().setup();
        punishManager.getCommandManager().updateDefaultLocale();
        sender.sendMessage(new TextComponent(Utils.color("&6" + punishManager.getDescription().getName() + " &7» &aAll configuration files have been reloaded.")));
    }

    @Subcommand("help")
    @Description("{@@punishmanager_help.description}")
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
