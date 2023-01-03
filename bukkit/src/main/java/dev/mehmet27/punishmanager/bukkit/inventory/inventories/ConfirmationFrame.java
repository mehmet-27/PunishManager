package dev.mehmet27.punishmanager.bukkit.inventory.inventories;

import com.cryptomorin.xseries.XMaterial;
import dev.mehmet27.punishmanager.bukkit.inventory.InventoryDrawer;
import dev.mehmet27.punishmanager.bukkit.inventory.UIComponent;
import dev.mehmet27.punishmanager.bukkit.inventory.UIComponentImpl;
import dev.mehmet27.punishmanager.bukkit.inventory.UIFrame;
import dev.mehmet27.punishmanager.utils.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

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
        return 9*3;
    }

    @Override
    public void createComponents() {
        UIComponent confirmComponent = new UIComponentImpl.Builder(XMaterial.LIME_WOOL.parseItem())
                .name(Messages.GUI_CONFIRMATION_CONFIRM_NAME.getString(getViewer().getUniqueId())).slot(12).build();
        confirmComponent.setListener(ClickType.LEFT, listener);
        add(confirmComponent);

        UIComponent returnComponent = new UIComponentImpl.Builder(XMaterial.RED_WOOL.parseItem())
                .name(Messages.GUI_CONFIRMATION_RETURN_NAME.getString(getViewer().getUniqueId())).slot(14).build();
        returnComponent.setListener(ClickType.LEFT, () -> InventoryDrawer.open(getParent()));
        add(returnComponent);
    }
}
