package com.mehmet_27.punishmanager.bukkit.listeners;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bukkit.PMBukkit;
import com.mehmet_27.punishmanager.bukkit.events.PunishRevokeEvent;
import com.mehmet_27.punishmanager.managers.DiscordManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.objects.PunishmentRevoke;
import com.mehmet_27.punishmanager.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Locale;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.NONE;

public class PunishRevokeListener implements Listener {
    private final PMBukkit plugin;
    private final PunishManager punishManager = PunishManager.getInstance();
    private final DiscordManager discordManager = punishManager.getDiscordManager();

    public PunishRevokeListener(PMBukkit plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPunish(PunishRevokeEvent event) {
        PunishmentRevoke punishmentRevoke = event.getPunishmentRevoke();

        if (punishmentRevoke.getRevokeType().equals(PunishmentRevoke.RevokeType.UNBAN)){
            Punishment punishment = punishManager.getStorageManager().getBan(punishmentRevoke.getUuid());
            if (punishment == null || !punishment.isBanned()) {
                Utils.sendText(punishmentRevoke.getOperatorUUID(), punishmentRevoke.getPlayerName(), "unban.notPunished");
                return;
            }
            punishManager.getStorageManager().unPunishPlayer(punishment);
        }
        if (punishmentRevoke.getRevokeType().equals(PunishmentRevoke.RevokeType.UNMUTE)){
            Punishment punishment = punishManager.getStorageManager().getMute(punishmentRevoke.getUuid());
            if (punishment == null || !punishment.isMuted()) {
                Utils.sendText(punishmentRevoke.getOperatorUUID(), punishmentRevoke.getPlayerName(), "unmute.notPunished");
                return;
            }
            punishManager.getStorageManager().unPunishPlayer(punishment);
        }
        if (punishmentRevoke.getRevokeType().equals(PunishmentRevoke.RevokeType.UNPUNISH)){
            Punishment punishment = punishManager.getStorageManager().getPunishment(punishmentRevoke.getUuid());
            if (punishment == null || punishment.getPunishType().equals(NONE)) {
                Utils.sendText(punishmentRevoke.getOperatorUUID(), punishmentRevoke.getPlayerName(), "unpunish.notPunished");
                return;
            }
            punishManager.getStorageManager().removePlayerAllPunishes(punishment);
        }

        //TODO: punishManager.getStorageManager().addPunishRevokeToHistory(punishmentRevoke);

        //Sending successfully punish revoked message to operator
        String path = punishmentRevoke.getRevokeType().name().toLowerCase(Locale.ENGLISH) + ".done";
        Utils.sendText(punishmentRevoke.getOperatorUUID(), punishmentRevoke.getPlayerName(), path);

        //Sending to punish announcement
        String announcement = event.getAnnounceMessage();
        if (announcement != null && !announcement.isEmpty()) {
            announcement = Utils.replacePunishmentRevokePlaceholders(announcement, punishmentRevoke);
            plugin.getServer().broadcastMessage(announcement);
        }
        discordManager.sendRevokeEmbed(punishmentRevoke);
    }
}
