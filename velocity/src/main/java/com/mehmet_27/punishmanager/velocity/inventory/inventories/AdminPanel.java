package com.mehmet_27.punishmanager.velocity.inventory.inventories;

import com.mehmet_27.punishmanager.utils.Messages;
import com.mehmet_27.punishmanager.velocity.inventory.*;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.data.ItemType;

public class AdminPanel extends UIFrame {

    public AdminPanel(UIFrame parent, Player viewer) {
        super(parent, viewer);
    }

    @Override
    public String getTitle() {
        return Messages.GUI_ADMINPANEL_TITLE.getString(getViewer().getUniqueId());
    }

    @Override
    public int getSize() {
        return 9 * 3;
    }

    @Override
    public void createComponents() {
        add(Components.getBackComponent(getParent(), 26, getViewer()));
        addReloadButton();
    }

    private void addReloadButton() {
        UIComponent reloadButton = new UIComponentImpl.Builder(ItemType.LIME_DYE)
                .name(Messages.GUI_ADMINPANEL_RELOAD_NAME.getString(getViewer().getUniqueId()))
                .slot(11).build();
        add(reloadButton);
        reloadButton.setPermission(ClickType.LEFT_CLICK, "punishmanager.command.punishmanager.reload");
        reloadButton.setListener(ClickType.LEFT_CLICK, () -> InventoryController.runCommand(getViewer(), "punishmanager", true, "reload"));
    }
}
