package com.mehmet_27.punishmanager.inventories;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static com.mehmet_27.punishmanager.utils.Utils.color;

public class Main extends UIFrame {

    PunishManager plugin;
    ConfigManager configManager;

    public Main(UIFrame parent, InventoryType type, ProxiedPlayer viewer) {
        super(parent, type, viewer);
        plugin = PunishManager.getInstance();
        configManager = plugin.getConfigManager();
        title(color(configManager.getMessage("gui.main.title", viewer.getName())));
        ItemStack selectLanguage = new ItemStack(ItemType.WHITE_BANNER)
                .displayName(color(configManager.getMessage("gui.main.languageselector.name", viewer.getName())));
        item(11, selectLanguage);
        ItemStack viewPunishments = new ItemStack(ItemType.DIAMOND_AXE)
                .displayName(color(configManager.getMessage("gui.main.managepunishments.name", viewer.getName())));
        if (viewer.hasPermission("punishmanager.gui.managepunishments")){
            item(13, viewPunishments);
        }
        ItemStack adminPanel = new ItemStack(ItemType.COMMAND_BLOCK)
                .displayName(color(configManager.getMessage("gui.main.adminpanel.name", viewer.getName())));
        if (viewer.hasPermission("punishmanager.gui.admin")){
            item(15, adminPanel);
        }
        onClick(click -> {
            click.cancelled(true);
            if (click.clickedItem() == null) return;
            if (click.clickedItem().displayName(true).toString().contains(color(configManager.getMessage("gui.main.languageselector.name", viewer.getName())))) {
                click.player().openInventory(new LanguageSelector(this, InventoryType.GENERIC_9X6, viewer));
            }
            if (click.clickedItem().displayName(true).toString().contains(color(configManager.getMessage("gui.main.adminpanel.name", viewer.getName())))) {
                click.player().openInventory(new AdminPanel(this, InventoryType.GENERIC_9X3, viewer));
            }
            if (click.clickedItem().displayName(true).toString().contains(color(configManager.getMessage("gui.main.managepunishments.name", viewer.getName())))) {
                click.player().openInventory(new ManagePunishments(this, InventoryType.GENERIC_9X6, viewer, 0));
            }
        });
    }
}
