package com.mehmet_27.punishmanager.velocity.inventory.inventories;

import com.mehmet_27.punishmanager.utils.Messages;
import com.mehmet_27.punishmanager.velocity.inventory.InventoryDrawer;
import com.mehmet_27.punishmanager.velocity.inventory.UIComponent;
import com.mehmet_27.punishmanager.velocity.inventory.UIComponentImpl;
import com.mehmet_27.punishmanager.velocity.inventory.UIFrame;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.data.ItemType;

public class ConfirmationFrame extends UIFrame {

    private final Runnable listener;

    public ConfirmationFrame(UIFrame parent, Player viewer, Runnable listener) {
        super(parent, viewer);
        this.listener = listener;
    }

    @Override
    public String getTitle() {
        return Messages.GUI_CONFIRMATION_TITLE.getString(getViewer().getUniqueId());
    }

    @Override
    public int getSize() {
        return 9 * 3;
    }

    @Override
    public void createComponents() {
        UIComponent confirmComponent = new UIComponentImpl.Builder(ItemType.LIME_WOOL)
                .name(Messages.GUI_CONFIRMATION_CONFIRM_NAME.getString(getViewer().getUniqueId())).slot(12).build();
        confirmComponent.setListener(ClickType.LEFT_CLICK, listener);
        add(confirmComponent);

        UIComponent returnComponent = new UIComponentImpl.Builder(ItemType.RED_WOOL)
                .name(Messages.GUI_CONFIRMATION_RETURN_NAME.getString(getViewer().getUniqueId())).slot(14).build();
        returnComponent.setListener(ClickType.LEFT_CLICK, () -> InventoryDrawer.open(getParent()));
        add(returnComponent);
    }
}
