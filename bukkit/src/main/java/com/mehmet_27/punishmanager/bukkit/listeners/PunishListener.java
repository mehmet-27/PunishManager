package com.mehmet_27.punishmanager.bukkit.listeners;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bukkit.PMBukkit;
import com.mehmet_27.punishmanager.bukkit.events.PunishEvent;
import com.mehmet_27.punishmanager.bukkit.managers.BukkitConfigManager;
import com.mehmet_27.punishmanager.bukkit.utils.Utils;
import com.mehmet_27.punishmanager.managers.DiscordManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.UtilsCore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Locale;
import java.util.Objects;

public class PunishListener implements Listener {

    private final PMBukkit plugin;
    private final PunishManager punishManager = PunishManager.getInstance();
    private final BukkitConfigManager configManager;
    private final DiscordManager discordManager = punishManager.getDiscordManager();

    public PunishListener(PMBukkit plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPunish(PunishEvent event) {
        Punishment punishment = event.getPunishment();
        CommandSender operator = "CONSOLE".equals(punishment.getOperator()) ? plugin.getServer().getConsoleSender() : plugin.getServer().getPlayer(punishment.getOperator());

        if (configManager.getExemptPlayers().contains(punishment.getPlayerName())) {
            Objects.requireNonNull(operator).sendMessage(configManager.getMessage("main.exempt-player", operator.getName()));
            return;
        }

        //Adding punish to database
        punishManager.getStorageManager().addPunishToPunishments(punishment);
        punishManager.getStorageManager().addPunishToHistory(punishment);

        //Sending successfully punished message to operator
        String path = punishment.getPunishType().name().toLowerCase(Locale.ENGLISH) + ".punished";
        Utils.sendText(operator, punishment.getPlayerName(), path);

        //Sends the punish message
        Player player = plugin.getServer().getPlayer(punishment.getUuid());

        if (player != null && player.isOnline()) {
            if (punishment.isBanned()) {
                player.kickPlayer(Utils.getLayout(punishment));
            } else if (punishment.isMuted()) {
                player.sendMessage(Utils.getLayout(punishment));
            }
        }

        //Sending to punish announcement
        String announcement = event.getAnnounceMessage();
        if (announcement == null || announcement.isEmpty()) return;
        announcement = UtilsCore.replacePunishmentPlaceholders(announcement, punishment);
        plugin.getServer().broadcastMessage(announcement);

        //Give "punished role" on Discord
        if (punishment.getPunishType().isMute()) {
            discordManager.updateRole(punishment, DiscordManager.DiscordAction.ADD);
        }
        discordManager.sendEmbed(punishment);
    }
}
