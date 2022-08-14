package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;

import java.util.UUID;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.unmute")
public class UnMuteCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @CommandCompletion("@players")
    @Description("{@@unmute.description}")
    @CommandAlias("unmute")
    public void unMute(CommandIssuer sender, @Name("Player") OfflinePlayer player) {
        String playerName = player.getName();
        UUID uuid = player.getUniqueId();
        UUID operatorUuid = sender.isPlayer() ? sender.getUniqueId() : null;
        Punishment punishment = storageManager.getMute(uuid);

        if (punishment == null || !punishment.isMuted()) {
            Utils.sendText(operatorUuid, playerName, "unmute.notPunished");
            return;
        }
        storageManager.unPunishPlayer(punishment);
        Utils.sendText(operatorUuid, playerName, "unmute.done");
    }
}
