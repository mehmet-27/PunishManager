package com.mehmet_27.punishmanager.bungee.listeners;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.PMBungee;
import com.mehmet_27.punishmanager.bungee.Utils.Utils;
import com.mehmet_27.punishmanager.bungee.events.PunishEvent;
import com.mehmet_27.punishmanager.bungee.managers.BungeeConfigManager;
import com.mehmet_27.punishmanager.managers.DiscordManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.UtilsCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Locale;

public class PunishListener implements Listener {

    private final PMBungee plugin;
    private final PunishManager punishManager = PunishManager.getInstance();
    private final BungeeConfigManager configManager;
    private final DiscordManager discordManager = punishManager.getDiscordManager();

    public PunishListener(PMBungee plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPunish(PunishEvent event) {
        Punishment punishment = event.getPunishment();
        CommandSender operator = "CONSOLE".equals(punishment.getOperator()) ? plugin.getProxy().getConsole() : plugin.getProxy().getPlayer(punishment.getOperator());

        if (configManager.getExemptPlayers().contains(punishment.getPlayerName())) {
            operator.sendMessage(new TextComponent(configManager.getMessage("main.exempt-player", operator.getName())));
            return;
        }

        //Adding punish to database
        punishManager.getStorageManager().addPunishToPunishments(punishment);
        punishManager.getStorageManager().addPunishToHistory(punishment);

        //Sending successfully punished message to operator
        String path = punishment.getPunishType().name().toLowerCase(Locale.ENGLISH) + ".punished";
        Utils.sendText(operator, punishment.getPlayerName(), path);

        //Sends the punish message
        ProxiedPlayer player = plugin.getProxy().getPlayer(punishment.getUuid());

        if (player != null && player.isConnected()) {
            if (punishment.isBanned()) {
                player.disconnect(Utils.getLayout(punishment));
            } else if (punishment.isMuted()) {
                player.sendMessage(Utils.getLayout(punishment));
            }
        }

        //Sending to punish announcement
        String announcement = event.getAnnounceMessage();
        if (announcement == null || announcement.isEmpty()) return;
        announcement = UtilsCore.replacePunishmentPlaceholders(announcement, punishment);
        plugin.getProxy().broadcast(new TextComponent(announcement));

        discordManager.sendEmbed(punishment);
    }
}
