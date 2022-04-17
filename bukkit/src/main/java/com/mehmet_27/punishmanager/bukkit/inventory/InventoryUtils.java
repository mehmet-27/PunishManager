package com.mehmet_27.punishmanager.bukkit.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InventoryUtils {
    public static void openInventory(Inventory inventory, Player player){
        player.openInventory(inventory);
    }
}
