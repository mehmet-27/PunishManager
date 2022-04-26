package com.mehmet_27.punishmanager.bukkit.inventory.inventories;

import com.cryptomorin.xseries.XMaterial;
import com.mehmet_27.punishmanager.bukkit.PMBukkit;
import com.mehmet_27.punishmanager.bukkit.inventory.*;
import com.mehmet_27.punishmanager.utils.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class AdminPanel extends UIFrame {

    public AdminPanel(UIFrame parent, Player viewer) {
        super(parent, viewer);
    }

    @Override
    public String getTitle() {
        return Messages.GUI_ADMINPANEL_TITLE.getString(getViewer().getName());
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
        UIComponent reloadButton = new UIComponentImpl.Builder(XMaterial.LIME_DYE)
                .name(Messages.GUI_ADMINPANEL_RELOAD_NAME.getString(getViewer().getName()))
                .slot(11).build();
        add(reloadButton);
        reloadButton.setPermission(ClickType.LEFT,"punishmanager.command.punishmanager.reload");
        reloadButton.setListener(ClickType.LEFT, ()-> {
            InventoryController.runCommand(getViewer(), "punishmanager", true, "reload");
        });
    }
}
