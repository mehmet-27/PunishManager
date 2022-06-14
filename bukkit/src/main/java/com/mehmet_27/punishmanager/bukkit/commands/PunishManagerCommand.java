package com.mehmet_27.punishmanager.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bukkit.PMBukkit;
import com.mehmet_27.punishmanager.bukkit.inventory.InventoryDrawer;
import com.mehmet_27.punishmanager.bukkit.inventory.inventories.MainFrame;
import com.mehmet_27.punishmanager.bukkit.utils.BukkitUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("punishmanager")
@Description("The main command of the plugin.")
public class PunishManagerCommand extends BaseCommand {

    @Dependency
    private PMBukkit plugin;

    @Default
    @Description("{@@punishmanager.description}")
    public void punishManager(Player sender) {
        //Main GUI
        InventoryDrawer.open(new MainFrame(sender));
    }

    @Subcommand("reload")
    @Description("{@@punishmanager-reload.description}")
    @CommandPermission("punishmanager.command.punishmanager.reload")
    public void reload(CommandSender sender) {
        plugin.getConfigManager().setup();
        plugin.getCommandManager().updateDefaultLocale();
        BukkitUtils.sendText(sender, "punishmanager-reload.done");
    }

    @Subcommand("about")
    public void about(CommandSender sender) {
        PunishManager.getInstance().getMethods().sendMessage(sender, "&a&m+                                                        +");
        PunishManager.getInstance().getMethods().sendMessage(sender, String.format("&6&l%s", PunishManager.getInstance().getMethods().getPluginName()));
        PunishManager.getInstance().getMethods().sendMessage(sender, "&eThe best punishment plugin for your server.");
        PunishManager.getInstance().getMethods().sendMessage(sender, "&e");
        PunishManager.getInstance().getMethods().sendMessage(sender, "&eDeveloper: &bMehmet_27");
        PunishManager.getInstance().getMethods().sendMessage(sender, "&eVersion: &b" + PunishManager.getInstance().getMethods().getPluginVersion());
        PunishManager.getInstance().getMethods().sendMessage(sender, "&ePlatform: &b" + PunishManager.getInstance().getMethods().getPlatform().getFriendlyName());
        PunishManager.getInstance().getMethods().sendMessage(sender, "&eStorage Provider: &b" + PunishManager.getInstance().getStorageManager().getStorageProvider());
        PunishManager.getInstance().getMethods().sendMessage(sender, "&e");
        PunishManager.getInstance().getMethods().sendMessage(sender, "&eContributors: &7Minat0_, RoinujNosde");
        PunishManager.getInstance().getMethods().sendMessage(sender, "&a&m+                                                        +");
        PunishManager.getInstance().getMethods().sendMessage(sender, "&eUse &a/punishmanager help &efor help.");
    }

    @Subcommand("help")
    @Description("{@@punishmanager-help.description}")
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
