package com.mehmet_27.punishmanager.inventories;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static com.mehmet_27.punishmanager.utils.Utils.color;

public class Main extends Inventory {

    public Main(InventoryType type, ProxiedPlayer sender) {
        super(type);
        ConfigManager configManager = PunishManager.getInstance().getConfigManager();
        title(color(configManager.getMessage("gui.main-title", sender.getName())));
        ItemStack selectLanguage = new ItemStack(ItemType.WHITE_BANNER)
                .displayName(color(configManager.getMessage("gui.main-languageselector-name", sender.getName())));
        item(11, selectLanguage);
        ItemStack viewPunishments = new ItemStack(ItemType.DIAMOND_AXE)
                .displayName(color(configManager.getMessage("gui.main-managepunishments-name", sender.getName())));
        if (sender.hasPermission("punishmanager.gui.managepunishments")){
            item(13, viewPunishments);
        }
        ItemStack adminPanel = new ItemStack(ItemType.COMMAND_BLOCK)
                .displayName(color(configManager.getMessage("gui.main-adminpanel-name", sender.getName())));
        if (sender.hasPermission("punishmanager.gui.admin")){
            item(15, adminPanel);
        }
        onClick(click -> {
            click.cancelled(true);
            if (click.clickedItem() == null) return;
            if (click.clickedItem().displayName(true).toString().contains(color(configManager.getMessage("gui.main-languageselector-name", sender.getName())))) {
                click.player().openInventory(new LanguageSelector(InventoryType.GENERIC_9X6, sender));
            }
            if (click.clickedItem().displayName(true).toString().contains(color(configManager.getMessage("gui.main-adminpanel-name", sender.getName())))) {
                click.player().openInventory(new AdminPanel(InventoryType.GENERIC_9X3, sender));
            }
        });
    }
}
