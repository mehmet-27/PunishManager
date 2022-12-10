package com.mehmet_27.punishmanager.velocity.listeners;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.DiscordManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.objects.PunishmentRevoke;
import com.mehmet_27.punishmanager.utils.Utils;
import com.mehmet_27.punishmanager.velocity.PMVelocity;
import com.mehmet_27.punishmanager.velocity.events.PunishRevokeEvent;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import net.kyori.adventure.text.Component;

import java.util.Locale;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.NONE;

public class PunishRevokeListener {
    private final PMVelocity plugin;
    private final PunishManager punishManager = PunishManager.getInstance();
    private final DiscordManager discordManager = punishManager.getDiscordManager();

    public PunishRevokeListener(PMVelocity plugin) {
        this.plugin = plugin;
    }

    @Subscribe(order = PostOrder.FIRST)
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
            plugin.getServer().sendMessage(Component.text(announcement));
        }
        discordManager.sendRevokeEmbed(punishmentRevoke);
    }
}
