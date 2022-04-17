package com.mehmet_27.punishmanager.bungee.inventory;

import com.mehmet_27.punishmanager.bungee.inventory.inventories.Main;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class InventoryUtils {
    public static void openInventory(Inventory inventory, ProtocolizePlayer player){
        player.openInventory(inventory);
    }

    public static void openMainInventory(ProxiedPlayer player){
        Protocolize.playerProvider().player(player.getUniqueId())
                .openInventory(new Main(null, InventoryType.GENERIC_9X3, player));
    }
}
