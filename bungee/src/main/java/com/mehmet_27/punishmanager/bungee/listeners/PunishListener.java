package com.mehmet_27.punishmanager.bungee.listeners;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.PMBungee;
import com.mehmet_27.punishmanager.bungee.Utils.BungeeUtils;
import com.mehmet_27.punishmanager.bungee.events.PunishEvent;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.DiscordManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
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
    private final ConfigManager configManager;
    private final DiscordManager discordManager = punishManager.getDiscordManager();

    public PunishListener(PMBungee plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPunish(PunishEvent event) {
        Punishment punishment = event.getPunishment();
        CommandSender operator = "CONSOLE".equals(punishment.getOperator()) ? plugin.getProxy().getConsole() : plugin.getProxy().getPlayer(punishment.getOperator());

        if (!punishment.getPunishType().equals(Punishment.PunishType.KICK)) {
            if (punishment.getPunishType().isBan()) {
                Punishment oldBan = punishManager.getStorageManager().getBan(punishment.getUuid());
                if (oldBan != null) {
                    if (oldBan.isBanned()) {
                        BungeeUtils.sendText(operator, oldBan.getPlayerName(),
                                oldBan.getPunishType().toString().toLowerCase(Locale.ENGLISH) + ".alreadyPunished");
                        return;
                    }
                }
            }
            if (punishment.getPunishType().isMute()) {
                Punishment oldMute = punishManager.getStorageManager().getMute(punishment.getUuid());
                if (oldMute != null) {
                    if (oldMute.isMuted()) {
                        BungeeUtils.sendText(operator, oldMute.getPlayerName(),
                                oldMute.getPunishType().toString().toLowerCase(Locale.ENGLISH) + ".alreadyPunished");
                        return;
                    }
                }
            }
        }

        if (configManager.getExemptPlayers().contains(punishment.getPlayerName())) {
            operator.sendMessage(new TextComponent(configManager.getMessage("main.exempt-player", operator.getName())));
            return;
        }

        //Adding punish to database
        if (!punishment.getPunishType().equals(Punishment.PunishType.KICK)) {
            punishManager.getStorageManager().addPunishToPunishments(punishment);
        }
        punishManager.getStorageManager().addPunishToHistory(punishment);

        //Sending successfully punished message to operator
        String path = punishment.getPunishType().name().toLowerCase(Locale.ENGLISH) + ".punished";
        BungeeUtils.sendText(operator, punishment.getPlayerName(), path);

        //Sends the punish message
        ProxiedPlayer player = plugin.getProxy().getPlayer(punishment.getUuid());

        if (player != null && player.isConnected()) {
            if (punishment.isBanned()) {
                player.disconnect(BungeeUtils.getLayout(punishment));
            } else if (punishment.isMuted()) {
                player.sendMessage(BungeeUtils.getLayout(punishment));
            } else if (punishment.getPunishType().equals(Punishment.PunishType.KICK)) {
                player.disconnect(BungeeUtils.getLayout(punishment));
            }
        }

        //Sending to punish announcement
        String announcement = event.getAnnounceMessage();
        if (announcement == null || announcement.isEmpty()) return;
        announcement = Utils.replacePunishmentPlaceholders(announcement, punishment);
        plugin.getProxy().broadcast(new TextComponent(announcement));

        discordManager.sendEmbed(punishment);
    }
}
