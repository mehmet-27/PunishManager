package com.mehmet_27.punishmanager.bungee.inventory.inventories;

import com.mehmet_27.punishmanager.bungee.PMBungee;
import com.mehmet_27.punishmanager.bungee.inventory.InventoryUtils;
import com.mehmet_27.punishmanager.utils.Messages;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AdminPanel extends UIFrame {

    public AdminPanel(UIFrame parent, InventoryType type, ProxiedPlayer viewer) {
        super(parent, type, viewer);
        PMBungee plugin = PMBungee.getInstance();
        title(Messages.GUI_ADMINPANEL_TITLE.getString(viewer.getName()));

        ItemStack reloadButton = new ItemStack(ItemType.LIME_DYE)
                .displayName(Messages.GUI_ADMINPANEL_RELOAD_NAME.getString(viewer.getName()));
        item(11, reloadButton);

        ItemStack backButton = new Item().back(viewer.getName());
        item(26, backButton);

        onClick(click -> {
            click.cancelled(true);
            ItemStack clickedItem = click.clickedItem();
            if (clickedItem == null) return;
            if (clickedItem.equals(reloadButton)) {
                plugin.getProxy().getPluginManager().dispatchCommand(viewer, "punishmanager reload");
            }
            if (clickedItem.equals(backButton)){
                ProtocolizePlayer protocolizePlayer = Protocolize.playerProvider().player(viewer.getUniqueId());
                InventoryUtils.openInventory(getParent(), protocolizePlayer);
            }
        });
    }
}
