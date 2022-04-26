package com.mehmet_27.punishmanager.bungee.inventory.inventories;

import com.mehmet_27.punishmanager.bungee.inventory.InventoryDrawer;
import com.mehmet_27.punishmanager.bungee.inventory.UIComponent;
import com.mehmet_27.punishmanager.bungee.inventory.UIComponentImpl;
import com.mehmet_27.punishmanager.bungee.inventory.UIFrame;
import com.mehmet_27.punishmanager.utils.Messages;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.data.ItemType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ConfirmationFrame extends UIFrame {

    private final Runnable listener;

    public ConfirmationFrame(UIFrame parent, ProxiedPlayer viewer, Runnable listener) {
        super(parent, viewer);
        this.listener = listener;
    }

    @Override
    public String getTitle() {
        return Messages.GUI_CONFIRMATION_TITLE.getString(getViewer().getName());
    }

    @Override
    public int getSize() {
        return 9*3;
    }

    @Override
    public void createComponents() {
        UIComponent confirmComponent = new UIComponentImpl.Builder(ItemType.LIME_WOOL)
                .name(Messages.GUI_CONFIRMATION_CONFIRM_NAME.getString(getViewer().getName())).slot(12).build();
        confirmComponent.setListener(ClickType.LEFT_CLICK, listener);
        add(confirmComponent);

        UIComponent returnComponent = new UIComponentImpl.Builder(ItemType.RED_WOOL)
                .name(Messages.GUI_CONFIRMATION_RETURN_NAME.getString(getViewer().getName())).slot(14).build();
        returnComponent.setListener(ClickType.LEFT_CLICK, () -> InventoryDrawer.open(getParent()));
        add(returnComponent);
    }
}
