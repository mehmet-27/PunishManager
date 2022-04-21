package com.mehmet_27.punishmanager.bungee.inventory.inventories;

import com.mehmet_27.punishmanager.bungee.PMBungee;
import com.mehmet_27.punishmanager.bungee.inventory.Components;
import com.mehmet_27.punishmanager.bungee.inventory.UIFrame;
import com.mehmet_27.punishmanager.bungee.inventory.UIComponent;
import com.mehmet_27.punishmanager.bungee.inventory.UIComponentImpl;
import com.mehmet_27.punishmanager.utils.Messages;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.data.ItemType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AdminPanel extends UIFrame {

    public AdminPanel(UIFrame parent, ProxiedPlayer viewer) {
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
        UIComponent reloadButton = new UIComponentImpl.Builder(ItemType.LIME_DYE)
                .name(Messages.GUI_ADMINPANEL_RELOAD_NAME.getString(getViewer().getName()))
                .slot(11).build();
        add(reloadButton);
        reloadButton.setPermission(ClickType.LEFT_CLICK,"punishmanager.command.punishmanager.reload");
        reloadButton.setListener(ClickType.LEFT_CLICK, ()-> PMBungee.getInstance().getProxy().getPluginManager().dispatchCommand(getViewer(), "punishmanager reload"));
    }
}
