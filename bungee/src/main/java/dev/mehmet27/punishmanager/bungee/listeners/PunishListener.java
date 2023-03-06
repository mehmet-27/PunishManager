package dev.mehmet27.punishmanager.bungee.listeners;

import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.bungee.PMBungee;
import dev.mehmet27.punishmanager.bungee.events.PunishEvent;
import dev.mehmet27.punishmanager.managers.ConfigManager;
import dev.mehmet27.punishmanager.managers.DiscordManager;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

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
        ProxiedPlayer player = plugin.getProxy().getPlayer(punishment.getUuid());

        if (player != null && player.isConnected()) {
            if (punishment.isBanned()) {
                player.disconnect(TextComponent.fromLegacyText(Utils.getLayout(punishment)));
            } else if (punishment.isMuted()) {
                player.sendMessage(TextComponent.fromLegacyText(Utils.getLayout(punishment)));
            } else if (punishment.getPunishType().equals(Punishment.PunishType.KICK)) {
                player.disconnect(TextComponent.fromLegacyText(Utils.getLayout(punishment)));
            }
        } else {
            if (punishment.getPunishType().equals(Punishment.PunishType.IPBAN)) {
                if (!punishManager.getBannedIps().contains(punishment.getIp())) {
                    punishManager.getBannedIps().add(punishment.getIp());
                }
                plugin.getProxy().getPlayers().forEach(ipPlayer -> {
                    if (ipPlayer.getSocketAddress().toString().substring(1).split(":")[0].equals(punishment.getIp())) {
                        ipPlayer.disconnect(TextComponent.fromLegacyText(Utils.getLayout(punishment)));
                    }
                });
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
