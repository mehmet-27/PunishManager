package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.ConfigManager;
import dev.mehmet27.punishmanager.managers.StorageManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.utils.Utils;

import java.util.List;
import java.util.UUID;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.warns")
public class WarnsCommand extends BaseCommand {

	@Dependency
	private PunishManager punishManager;

	@Dependency
	private ConfigManager configManager;

	@Dependency
	private StorageManager storageManager;

	@CommandCompletion("@players")
	@Description("{@@warns.description}")
	@CommandAlias("%warns")
	public void warns(CommandIssuer sender, @Name("Player") OfflinePlayer player) {
		String playerName = player.getName();
		UUID uuid = player.getUniqueId();
		UUID operatorUuid = sender.isPlayer() ? sender.getUniqueId() : null;
		String online = (punishManager.getMethods().isOnline(uuid)) ? configManager.getMessage("main.online") : configManager.getMessage("main.offline");
		Utils.sendText(operatorUuid, "warns.checking", message -> message
				.replace("%player%", playerName)
				.replace("%online%", online));
		if (!storageManager.isLoggedServer(uuid)) {
			Utils.sendText(operatorUuid, playerName, "warns.playerNotFound");
			return;
		}
		List<Punishment> punishmentList = storageManager.getPunishments(uuid, "WARN");
		if (punishmentList.isEmpty()) {
			Utils.sendText(operatorUuid, "warns.notPunished", message -> message.replace("%player%", playerName));
			return;
		}
		for (Punishment punishment : punishmentList) {
			Utils.sendText(operatorUuid, "warns.layout", message -> message.replace("%id%", String.valueOf(punishment.getId())).replace("%reason%", punishment.getReason()));
		}
	}
}
