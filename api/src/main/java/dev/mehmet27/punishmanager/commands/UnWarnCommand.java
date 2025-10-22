package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.StorageManager;
import dev.mehmet27.punishmanager.objects.PunishmentRevoke;

import java.util.UUID;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.unwarn")
public class UnWarnCommand extends BaseCommand {
	@Dependency
	private StorageManager storageManager;

	//@CommandCompletion("@players")
	@Description("{@@unwarn.description}")
	@CommandAlias("%unwarn")
	public void unWarn(CommandIssuer sender, @Name("Id") @Conditions("mustInteger") int id, @Optional @Name("Reason") @Default("none") String reason) {
		UUID operatorUuid = sender.isPlayer() ? sender.getUniqueId() : null;
		String operator = sender.isPlayer() ? storageManager.getOfflinePlayer(sender.getUniqueId()).getName() : "CONSOLE";
		PunishmentRevoke punishmentRevoke = new PunishmentRevoke(null, null, PunishmentRevoke.RevokeType.UNWARN, reason, operator, operatorUuid, System.currentTimeMillis(), id);
		PunishManager.getInstance().getMethods().callPunishRevokeEvent(punishmentRevoke);
	}
}
