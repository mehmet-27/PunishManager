package com.mehmet_27.punishmanager.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.Utils.Utils;
import com.mehmet_27.punishmanager.managers.DiscordManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.md_5.bungee.api.CommandSender;

import java.util.UUID;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.unmute")
public class UnMuteCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @CommandCompletion("@players")
    @Description("{@@unmute.description}")
    @CommandAlias("unmute")
    public void unMute(CommandSender sender, @Name("Player") OfflinePlayer player) {
        String playerName = player.getName();
        UUID uuid = player.getUniqueId();
        Punishment punishment = storageManager.getMute(uuid);

        if (punishment == null || !punishment.isMuted()) {
            Utils.sendText(sender, playerName, "unmute.notPunished");
            return;
        }
        storageManager.unPunishPlayer(punishment);
        PunishManager.getInstance().getDiscordManager().updateRole(punishment, DiscordManager.DiscordAction.REMOVE);
        Utils.sendText(sender, playerName, "unmute.done");
    }
}
