package dev.mehmet27.punishmanager.velocity.inventory.inventories;

import com.velocitypowered.api.proxy.Player;
import dev.mehmet27.punishmanager.utils.Messages;
import dev.mehmet27.punishmanager.velocity.inventory.*;
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
        addDefaultLangButton();
    }

    private void addReloadButton() {
        UIComponent reloadButton = new UIComponentImpl.Builder(ItemType.LIME_DYE)
                .name(Messages.GUI_ADMINPANEL_RELOAD_NAME.getString(getViewer().getUniqueId()))
                .slot(11).build();
        add(reloadButton);
        reloadButton.setPermission(ClickType.LEFT_CLICK, "punishmanager.command.punishmanager.reload");
        reloadButton.setListener(ClickType.LEFT_CLICK, () -> InventoryController.runCommand(getViewer(), "punishmanager", true, "admin reload"));
    }

    private void addDefaultLangButton() {
        UIComponent defaultLangButton = new UIComponentImpl.Builder(ItemType.PAPER)
                .name(Messages.GUI_ADMINPANEL_DEFAULTLANGUAGESELECTOR_NAME.getString(getViewer().getUniqueId()))
                .slot(15).build();
        add(defaultLangButton);
        defaultLangButton.setPermission(ClickType.LEFT_CLICK, "punishmanager.gui.admin.defaultlanguage");
        defaultLangButton.setListener(ClickType.LEFT_CLICK, () -> InventoryDrawer.open(new DefaultLanguageSelector(this, getViewer())));
    }
}
