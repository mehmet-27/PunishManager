package com.mehmet_27.punishmanager.inventories;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.utils.ProtocolizeUtils;
import com.mehmet_27.punishmanager.utils.Utils;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static com.mehmet_27.punishmanager.utils.Utils.color;

public class AdminPanel extends UIFrame {

    PunishManager plugin;
    ConfigManager configManager;

    public AdminPanel(UIFrame parent, InventoryType type, ProxiedPlayer viewer) {
        super(parent, type, viewer);
        plugin = PunishManager.getInstance();
        configManager = PunishManager.getInstance().getConfigManager();
        title(color(configManager.getMessage("gui.admin.title", viewer.getName())));
        ItemStack reloadButton = new ItemStack(ItemType.LIME_DYE)
                .displayName(color(configManager.getMessage("gui.admin.reload.name", viewer.getName())));
        item(11, reloadButton);
        ItemStack back = new ItemStack(ItemType.ARROW);
        back.displayName(color(configManager.getMessage("gui.backbutton.name", viewer.getName())));
        item(26, back);
        onClick(click -> {
            click.cancelled(true);
            if (click.clickedItem() == null) return;
            if (click.clickedItem().displayName(true).toString().contains(color(configManager.getMessage("gui.admin.reload.name", viewer.getName())))) {
                PunishManager.getInstance().getProxy().getPluginManager().dispatchCommand(viewer, "punishmanager reload");
            }
            if (click.clickedItem().displayName(true).toString().contains(color(configManager.getMessage("gui.backbutton.name", viewer.getName())))){
                ProtocolizeUtils.openInventory(getParent(), Protocolize.playerProvider().player(viewer.getUniqueId()));
            }
        });
    }
}
