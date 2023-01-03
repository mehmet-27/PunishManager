package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.StorageManager;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.utils.Utils;

import java.util.UUID;


@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.changereason")
public class ChangeReasonCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @Dependency
    private PunishManager punishManager;

    @CommandCompletion("id newReason")
    @Description("{@@changereason.description}")
    @CommandAlias("%changereason")
    public void changeReason(CommandIssuer sender, @Conditions("mustInteger") @Name("Id") String punishmentId, @Name("newReason") String newReason) {

        Punishment punishment = storageManager.getPunishmentWithId(Integer.parseInt(punishmentId));
        UUID uuid = sender.isPlayer() ? sender.getUniqueId() : null;
        if (punishment == null) {
            Utils.sendText(uuid, "changereason.punishmentNotFound");
            return;
        }

        storageManager.updatePunishmentReason(punishment.getId(), newReason);
        Utils.sendText(uuid, "changereason.done", message -> message.replace("%id%", "" + punishment.getId()).replace("%reason%", newReason));
    }
}
