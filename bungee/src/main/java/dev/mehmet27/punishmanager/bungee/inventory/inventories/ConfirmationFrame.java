package dev.mehmet27.punishmanager.bungee.inventory.inventories;

import dev.mehmet27.punishmanager.bungee.inventory.InventoryDrawer;
import dev.mehmet27.punishmanager.bungee.inventory.UIComponent;
import dev.mehmet27.punishmanager.bungee.inventory.UIComponentImpl;
import dev.mehmet27.punishmanager.bungee.inventory.UIFrame;
import dev.mehmet27.punishmanager.utils.Messages;
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
