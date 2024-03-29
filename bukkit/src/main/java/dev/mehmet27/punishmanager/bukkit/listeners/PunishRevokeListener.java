package dev.mehmet27.punishmanager.bukkit.listeners;

import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.bukkit.events.PunishRevokeEvent;
import dev.mehmet27.punishmanager.managers.DiscordManager;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.objects.PunishmentRevoke;
import dev.mehmet27.punishmanager.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Locale;

public class PunishRevokeListener implements Listener {
    private final PunishManager punishManager = PunishManager.getInstance();
    private final DiscordManager discordManager = punishManager.getDiscordManager();

    @EventHandler(priority = EventPriority.LOW)
    public void onPunish(PunishRevokeEvent event) {
        PunishmentRevoke punishmentRevoke = event.getPunishmentRevoke();

        if (punishmentRevoke.getRevokeType().equals(PunishmentRevoke.RevokeType.UNBAN)) {
            Punishment punishment = punishManager.getStorageManager().getBan(punishmentRevoke.getUuid());
            if (punishment == null || !punishment.isBanned()) {
                Utils.sendText(punishmentRevoke.getOperatorUUID(), punishmentRevoke.getPlayerName(), "unban.notPunished");
                return;
            }
            punishManager.getStorageManager().unPunishPlayer(punishment);
        }
        if (punishmentRevoke.getRevokeType().equals(PunishmentRevoke.RevokeType.UNMUTE)) {
            Punishment punishment = punishManager.getStorageManager().getMute(punishmentRevoke.getUuid());
            if (punishment == null || !punishment.isMuted()) {
                Utils.sendText(punishmentRevoke.getOperatorUUID(), punishmentRevoke.getPlayerName(), "unmute.notPunished");
                return;
            }
            punishManager.getStorageManager().unPunishPlayer(punishment);
        }
        if (punishmentRevoke.getRevokeType().equals(PunishmentRevoke.RevokeType.UNPUNISH)) {
            Punishment punishment = punishManager.getStorageManager().getPunishment(punishmentRevoke.getUuid());
            if (punishment == null || punishment.getPunishType().equals(Punishment.PunishType.NONE)) {
                Utils.sendText(punishmentRevoke.getOperatorUUID(), punishmentRevoke.getPlayerName(), "unpunish.notPunished");
                return;
            }
            punishManager.getStorageManager().removePlayerAllPunishes(punishment);
        }

        if (punishmentRevoke.getRevokeType().equals(PunishmentRevoke.RevokeType.UNBAN)) {
            punishManager.getBannedIps().remove(punishmentRevoke.getPlayerName());
        }

        //TODO: punishManager.getStorageManager().addPunishRevokeToHistory(punishmentRevoke);

        //Sending successfully punish revoked message to operator
        String path = punishmentRevoke.getRevokeType().name().toLowerCase(Locale.ENGLISH) + ".done";
        Utils.sendText(punishmentRevoke.getOperatorUUID(), punishmentRevoke.getPlayerName(), path);

        discordManager.sendRevokeEmbed(punishmentRevoke);
    }
}
