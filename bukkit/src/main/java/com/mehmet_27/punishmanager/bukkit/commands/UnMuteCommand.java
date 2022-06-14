package com.mehmet_27.punishmanager.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.bukkit.utils.BukkitUtils;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import org.bukkit.command.CommandSender;

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
            BukkitUtils.sendText(sender, playerName, "unmute.notPunished");
            return;
        }
        storageManager.unPunishPlayer(punishment);
        BukkitUtils.sendText(sender, playerName, "unmute.done");
    }
}
