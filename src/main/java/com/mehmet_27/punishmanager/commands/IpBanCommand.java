package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.events.PlayerPunishEvent;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;

import java.util.UUID;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.IPBAN;
import static com.mehmet_27.punishmanager.utils.Utils.sendTextComponent;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.banip")
public class IpBanCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;
    @Dependency
    private PunishManager punishManager;
    @CommandCompletion("@players Reason")
    @Description("{@@ipban.description}")
    @CommandAlias("ipban")
    public void banIp(CommandSender sender, @Conditions("other_player") @Name("Player") OfflinePlayer player, @Optional @Name("Reason") String reason) {
        UUID uuid = player.getUniqueId();
        String playerName = player.getPlayerName();

        Punishment punishment = storageManager.getBan(playerName);
        if (punishment != null && punishment.isBanned()) {
            sendTextComponent(sender, playerName, "ipban.alreadyPunished");
            return;
        }

        String ip = Utils.getPlayerIp(playerName);
        punishment = new Punishment(playerName, uuid, ip, IPBAN, reason, sender.getName());
        punishManager.getProxy().getPluginManager().callEvent(new PlayerPunishEvent(punishment));
    }
}
