package com.mehmet_27.punishmanager.utils;

import com.mehmet_27.punishmanager.inventories.Main;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ProtocolizeUtils {
    public static void openInventory(Inventory inventory, ProtocolizePlayer player){
        player.openInventory(inventory);
    }

    public static void openMainInventory(ProxiedPlayer player){
        Protocolize.playerProvider().player(player.getUniqueId())
                .openInventory(new Main(InventoryType.GENERIC_9X3, player));
    }
}
