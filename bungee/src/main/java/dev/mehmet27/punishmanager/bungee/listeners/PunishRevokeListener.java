package dev.mehmet27.punishmanager.bungee.listeners;

import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.bungee.PMBungee;
import dev.mehmet27.punishmanager.bungee.events.PunishRevokeEvent;
import dev.mehmet27.punishmanager.managers.DiscordManager;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.objects.PunishmentRevoke;
import dev.mehmet27.punishmanager.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Locale;

import static dev.mehmet27.punishmanager.objects.Punishment.PunishType.NONE;

public class PunishRevokeListener implements Listener {
    private final PMBungee plugin;
    private final PunishManager punishManager = PunishManager.getInstance();
    private final DiscordManager discordManager = punishManager.getDiscordManager();

    public PunishRevokeListener(PMBungee plugin) {
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
            plugin.getProxy().broadcast(new TextComponent(announcement));
        }
        discordManager.sendRevokeEmbed(punishmentRevoke);
    }
}
