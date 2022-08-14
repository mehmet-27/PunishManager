package com.mehmet_27.punishmanager.bukkit.listeners;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bukkit.PMBukkit;
import com.mehmet_27.punishmanager.bukkit.events.PunishEvent;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.DiscordManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Locale;
import java.util.UUID;

public class PunishListener implements Listener {

    private final PMBukkit plugin;
    private final PunishManager punishManager = PunishManager.getInstance();
    private final ConfigManager configManager;
    private final DiscordManager discordManager = punishManager.getDiscordManager();

    public PunishListener(PMBukkit plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPunish(PunishEvent event) {
        Punishment punishment = event.getPunishment();

        if (!punishment.getPunishType().equals(Punishment.PunishType.KICK)) {
            if (punishment.getPunishType().isBan()) {
                Punishment oldBan = punishManager.getStorageManager().getBan(punishment.getUuid());
                if (oldBan != null) {
                    if (oldBan.isBanned()) {
                        Utils.sendText(punishment.getOperatorUUID(), oldBan.getPlayerName(),
                                oldBan.getPunishType().toString().toLowerCase(Locale.ENGLISH) + ".alreadyPunished");
                        return;
                    }
                }
            }
            if (punishment.getPunishType().isMute()) {
                Punishment oldMute = punishManager.getStorageManager().getMute(punishment.getUuid());
                if (oldMute != null) {
                    if (oldMute.isMuted()) {
                        Utils.sendText(punishment.getOperatorUUID(), oldMute.getPlayerName(),
                                oldMute.getPunishType().toString().toLowerCase(Locale.ENGLISH) + ".alreadyPunished");
                        return;
                    }
                }
            }
        }

        if (configManager.getExemptPlayers().contains(punishment.getPlayerName())) {
            UUID operatorUUID = punishment.getOperatorUUID();
            punishManager.getMethods().sendMessage(operatorUUID, configManager.getMessage("main.exempt-player", operatorUUID));
            return;
        }

        //Adding punish to database
        if (!punishment.getPunishType().equals(Punishment.PunishType.KICK)) {
            punishManager.getStorageManager().addPunishToPunishments(punishment);
        }
        punishManager.getStorageManager().addPunishToHistory(punishment);

        //Sending successfully punished message to operator
        String path = punishment.getPunishType().name().toLowerCase(Locale.ENGLISH) + ".punished";
        Utils.sendText(punishment.getOperatorUUID(), punishment.getPlayerName(), path);

        //Sends the punish message
        Player player = plugin.getServer().getPlayer(punishment.getUuid());

        if (player != null && player.isOnline()) {
            if (punishment.isBanned()) {
                player.kickPlayer(Utils.getLayout(punishment));
            } else if (punishment.isMuted()) {
                player.sendMessage(Utils.getLayout(punishment));
            } else if (punishment.getPunishType().equals(Punishment.PunishType.KICK)) {
                player.kickPlayer(Utils.getLayout(punishment));
            }
        }

        //Sending to punish announcement
        String announcement = event.getAnnounceMessage();
        if (announcement == null || announcement.isEmpty()) return;
        announcement = Utils.replacePunishmentPlaceholders(announcement, punishment);
        plugin.getServer().broadcastMessage(announcement);

        discordManager.sendEmbed(punishment);
    }
}
