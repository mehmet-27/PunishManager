package com.mehmet_27.punishmanager.inventories;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.utils.Utils;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static com.mehmet_27.punishmanager.utils.Utils.color;

public class AdminPanel extends Inventory {
    public AdminPanel(InventoryType type, ProxiedPlayer sender) {
        super(type);
        ConfigManager configManager = PunishManager.getInstance().getConfigManager();
        title(color(configManager.getMessage("gui.admin-title", sender.getName())));
        ItemStack reloadButton = new ItemStack(ItemType.LIME_DYE)
                .displayName(color(configManager.getMessage("gui.admin-reload-name", sender.getName())));
        item(11, reloadButton);
        ItemStack back = new ItemStack(ItemType.ARROW);
        back.displayName(color(configManager.getMessage("gui.backbutton-name", sender.getName())));
        item(26, back);
        onClick(click -> {
            click.cancelled(true);
            if (click.clickedItem() == null) return;
            if (click.clickedItem().displayName(true).toString().contains(color(configManager.getMessage("gui.admin-reload-name", sender.getName())))) {
                PunishManager.getInstance().getProxy().getPluginManager().dispatchCommand(sender, "punishmanager reload");
            }
            if (click.clickedItem().displayName(true).toString().contains(color(configManager.getMessage("gui.backbutton-name", sender.getName())))){
                Utils.openInventory(new Main(InventoryType.GENERIC_9X3, sender), Protocolize.playerProvider().player(sender.getUniqueId()));
            }
        });
    }
}
