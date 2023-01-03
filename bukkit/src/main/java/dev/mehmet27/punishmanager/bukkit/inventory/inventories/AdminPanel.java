package dev.mehmet27.punishmanager.bukkit.inventory.inventories;

import com.cryptomorin.xseries.XMaterial;
import dev.mehmet27.punishmanager.bukkit.inventory.*;
import dev.mehmet27.punishmanager.utils.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

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
        UIComponent reloadButton = new UIComponentImpl.Builder(XMaterial.LIME_DYE)
                .name(Messages.GUI_ADMINPANEL_RELOAD_NAME.getString(getViewer().getUniqueId()))
                .slot(11).build();
        add(reloadButton);
        reloadButton.setPermission(ClickType.LEFT, "punishmanager.command.punishmanager.reload");
        reloadButton.setListener(ClickType.LEFT, () -> InventoryController.runCommand(getViewer(), "punishmanager", true, "admin reload"));
    }

    private void addDefaultLangButton() {
        UIComponent defaultLangButton = new UIComponentImpl.Builder(XMaterial.PAPER)
                .name(Messages.GUI_ADMINPANEL_DEFAULTLANGUAGESELECTOR_NAME.getString(getViewer().getUniqueId()))
                .slot(15).build();
        add(defaultLangButton);
        defaultLangButton.setPermission(ClickType.LEFT, "punishmanager.gui.admin.defaultlanguage");
        defaultLangButton.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new DefaultLanguageSelector(this, getViewer())));
    }
}
