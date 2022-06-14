package com.mehmet_27.punishmanager.bukkit.listeners;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bukkit.PMBukkit;
import com.mehmet_27.punishmanager.bukkit.events.PunishEvent;
import com.mehmet_27.punishmanager.bukkit.utils.BukkitUtils;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.DiscordManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
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
    private final ConfigManager configManager;
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
        BukkitUtils.sendText(operator, punishment.getPlayerName(), path);

        //Sends the punish message
        Player player = plugin.getServer().getPlayer(punishment.getUuid());

        if (player != null && player.isOnline()) {
            if (punishment.isBanned()) {
                player.kickPlayer(BukkitUtils.getLayout(punishment));
            } else if (punishment.isMuted()) {
                player.sendMessage(BukkitUtils.getLayout(punishment));
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
