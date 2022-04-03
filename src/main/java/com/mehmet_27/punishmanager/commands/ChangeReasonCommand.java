package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.md_5.bungee.api.CommandSender;

import static com.mehmet_27.punishmanager.utils.Utils.sendTextComponent;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.changereason")
public class ChangeReasonCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;
    @Dependency
    private PunishManager punishManager;

    @CommandCompletion("id newReason")
    @Description("{@@changereason.description}")
    @CommandAlias("changereason")
    public void changeReason(CommandSender sender, @Conditions("mustInteger") @Name("Id") String punishmentId, @Name("newReason") String newReason) {

        Punishment punishment = storageManager.getPunishmentWithId(Integer.parseInt(punishmentId));
        if (punishment == null) {
            sendTextComponent(sender, "ban.punishmentNotFound");
            return;
        }

        storageManager.updatePunishmentReason(punishment.getId(), newReason);
        sendTextComponent(sender, "changereason.done", message -> message.replace("%id%", "" + punishment.getId()).replace("%reason%", newReason));
    }
}
