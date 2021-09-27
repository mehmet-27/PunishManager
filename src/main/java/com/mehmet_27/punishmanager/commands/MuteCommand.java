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

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.MUTE;
import static com.mehmet_27.punishmanager.utils.Utils.sendTextComponent;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.mute")
public class MuteCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;
    @Dependency
    private PunishManager punishManager;

    @CommandCompletion("@players Reason")
    @Description("{@@mute.description}")
    @CommandAlias("mute")
    public void mute(CommandSender sender, @Conditions("other_player") @Name("Player") OfflinePlayer player, @Optional @Name("Reason") String reason) {
        UUID uuid = player.getUniqueId();
        String playerName = player.getPlayerName();

        Punishment punishment = storageManager.getMute(playerName);
        if (punishment != null && punishment.isMuted()) {
            sendTextComponent(sender, playerName, "mute.alreadyPunished");
            return;
        }

        String ip = Utils.getPlayerIp(playerName);
        punishment = new Punishment(playerName, uuid, ip, MUTE, reason, sender.getName());
        punishManager.getProxy().getPluginManager().callEvent(new PlayerPunishEvent(punishment));
    }
}
