package com.mehmet_27.punishmanager.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.bukkit.inventory.InventoryDrawer;
import com.mehmet_27.punishmanager.bukkit.inventory.inventories.PunishFrame;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.punish")
public class PunishCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @CommandCompletion("@players")
    @Description("{@@punish.description}")
    @CommandAlias("punish")
    public void punish(Player sender, @Conditions("other_player") @Name("Player") OfflinePlayer player) {
        InventoryDrawer.open(new PunishFrame(sender, player));
    }
}
