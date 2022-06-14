package com.mehmet_27.punishmanager.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.bukkit.utils.BukkitUtils;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import org.bukkit.command.CommandSender;

import java.util.UUID;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.NONE;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.unpunish")
public class UnPunishCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @CommandCompletion("@players")
    @Description("{@@unpunish.description}")
    @CommandAlias("unpunish")
    public void unPunish(CommandSender sender, @Name("Player") OfflinePlayer player) {
        String playerName = player.getName();
        UUID uuid = player.getUniqueId();

        Punishment punishment = storageManager.getPunishment(uuid);
        if (punishment == null || punishment.getPunishType().equals(NONE)) {
            BukkitUtils.sendText(sender, playerName, "unpunish.notPunished");
            return;
        }
        storageManager.removePlayerAllPunishes(punishment);
        BukkitUtils.sendText(sender, playerName, "unpunish.done");
    }
}
