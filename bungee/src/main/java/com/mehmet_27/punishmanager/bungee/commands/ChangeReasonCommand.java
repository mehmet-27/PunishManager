package com.mehmet_27.punishmanager.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.bungee.Utils.Utils;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.md_5.bungee.api.CommandSender;


@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.changereason")
public class ChangeReasonCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @CommandCompletion("id newReason")
    @Description("{@@changereason.description}")
    @CommandAlias("changereason")
    public void changeReason(CommandSender sender, @Conditions("mustInteger") @Name("Id") String punishmentId, @Name("newReason") String newReason) {

        Punishment punishment = storageManager.getPunishmentWithId(Integer.parseInt(punishmentId));
        if (punishment == null) {
            Utils.sendText(sender, "changereason.punishmentNotFound");
            return;
        }

        storageManager.updatePunishmentReason(punishment.getId(), newReason);
        Utils.sendText(sender, "changereason.done", message -> message.replace("%id%", "" + punishment.getId()).replace("%reason%", newReason));
    }
}
