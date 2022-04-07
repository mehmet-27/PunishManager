package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.md_5.bungee.api.CommandSender;

import java.util.UUID;

import static com.mehmet_27.punishmanager.utils.Utils.sendTextComponent;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.unban")
public class UnBanCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @CommandCompletion("@players")
    @Description("{@@unban.description}")
    @CommandAlias("unban")
    public void unBan(CommandSender sender, @Name("Player") OfflinePlayer player) {
        String playerName = player.getPlayerName();
        UUID uuid = player.getUniqueId();
        Punishment punishment = storageManager.getBan(uuid);

        if (punishment == null || !punishment.isBanned()) {
            sendTextComponent(sender, playerName, "unban.notPunished");
            return;
        }

        storageManager.unPunishPlayer(punishment);
        sendTextComponent(sender, playerName, "unban.done");
    }
}
