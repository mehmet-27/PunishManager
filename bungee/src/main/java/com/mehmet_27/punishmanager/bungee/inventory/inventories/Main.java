package com.mehmet_27.punishmanager.bungee.inventory.inventories;

import com.mehmet_27.punishmanager.bungee.PMBungee;
import com.mehmet_27.punishmanager.utils.Messages;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.connection.ProxiedPlayer;


public class Main extends UIFrame {

    public Main(UIFrame parent, InventoryType type, ProxiedPlayer viewer) {
        super(parent, type, viewer);
        PMBungee plugin = PMBungee.getInstance();
        title(Messages.GUI_MAIN_TITLE.getString(viewer.getName()));

        ItemStack languageSelector = new ItemStack(ItemType.WHITE_BANNER)
                .displayName(Messages.GUI_MAIN_LANGUAGESELECTOR_NAME.getString(viewer.getName()));
        item(11, languageSelector);

        ItemStack managePunishments = new ItemStack(ItemType.DIAMOND_AXE)
                .displayName(Messages.GUI_MAIN_MANAGEPUNISHMENTS_NAME.getString(viewer.getName()));
        if (viewer.hasPermission("punishmanager.gui.managepunishments")){
            item(13, managePunishments);
        }

        ItemStack adminPanel = new ItemStack(ItemType.COMMAND_BLOCK)
                .displayName(Messages.GUI_MAIN_ADMINPANEL_NAME.getString(viewer.getName()));
        if (viewer.hasPermission("punishmanager.gui.admin")){
            item(15, adminPanel);
        }
        onClick(click -> {
            click.cancelled(true);
            ItemStack clickedItem = click.clickedItem();
            if (click.clickedItem() == null) return;
            if (clickedItem.equals(languageSelector)) {
                click.player().openInventory(new LanguageSelector(this, InventoryType.GENERIC_9X6, viewer));
            }
            if (clickedItem.equals(adminPanel)) {
                click.player().openInventory(new AdminPanel(this, InventoryType.GENERIC_9X3, viewer));
            }
            if (clickedItem.equals(managePunishments)) {
                click.player().openInventory(new ManagePunishments(this, InventoryType.GENERIC_9X6, viewer, 0));
            }
        });
    }
}
