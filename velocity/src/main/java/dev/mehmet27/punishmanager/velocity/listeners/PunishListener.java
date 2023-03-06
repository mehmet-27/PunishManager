package dev.mehmet27.punishmanager.velocity.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.ConfigManager;
import dev.mehmet27.punishmanager.managers.DiscordManager;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.utils.Utils;
import dev.mehmet27.punishmanager.velocity.PMVelocity;
import dev.mehmet27.punishmanager.velocity.events.PunishEvent;
import net.kyori.adventure.text.Component;

import java.util.Locale;
import java.util.UUID;

public class PunishListener {

    private final PMVelocity plugin;
    private final PunishManager punishManager = PunishManager.getInstance();
    private final ConfigManager configManager;
    private final DiscordManager discordManager = punishManager.getDiscordManager();

    public PunishListener(PMVelocity plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    @Subscribe(order = PostOrder.FIRST)
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
        Player player = plugin.getServer().getPlayer(punishment.getUuid()).orElse(null);

        if (player != null && player.isActive()) {
            if (punishment.isBanned()) {
                player.disconnect(Component.text(Utils.getLayout(punishment)));
            } else if (punishment.isMuted()) {
                player.sendMessage(Component.text((Utils.getLayout(punishment))));
            } else if (punishment.getPunishType().equals(Punishment.PunishType.KICK)) {
                player.disconnect(Component.text((Utils.getLayout(punishment))));
            }
        } else {
            if (punishment.getPunishType().equals(Punishment.PunishType.IPBAN)) {
                if (!punishManager.getBannedIps().contains(punishment.getIp())) {
                    punishManager.getBannedIps().add(punishment.getIp());
                }
                plugin.getServer().getAllPlayers().forEach(ipPlayer -> {
                    if (ipPlayer.getRemoteAddress().getHostName().equals(punishment.getIp())) {
                        ipPlayer.disconnect(Component.text(Utils.getLayout(punishment)));
                    }
                });
            }
        }

        //Sending to punish announcement
        String announcement = event.getAnnounceMessage();
        if (announcement == null || announcement.isEmpty()) return;
        announcement = Utils.replacePunishmentPlaceholders(announcement, punishment);
        plugin.getServer().sendMessage((Component.text(announcement)));

        discordManager.sendEmbed(punishment);
    }
}
