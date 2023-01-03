package dev.mehmet27.punishmanager.velocity.inventory.inventories;

import com.velocitypowered.api.proxy.Player;
import dev.mehmet27.punishmanager.utils.Messages;
import dev.mehmet27.punishmanager.velocity.inventory.InventoryDrawer;
import dev.mehmet27.punishmanager.velocity.inventory.UIComponent;
import dev.mehmet27.punishmanager.velocity.inventory.UIComponentImpl;
import dev.mehmet27.punishmanager.velocity.inventory.UIFrame;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.data.ItemType;

public class MainFrame extends UIFrame {

    public MainFrame(Player viewer) {
        super(null, viewer);
    }

    @Override
    public String getTitle() {
        return Messages.GUI_MAIN_TITLE.getString(getViewer().getUniqueId());
    }

    @Override
    public int getSize() {
        return 9 * 3;
    }

    @Override
    public void createComponents() {
        addLanguageSelector();
        addManagePunishments();
        addAdminPanel();
    }

    private void addLanguageSelector() {
        UIComponent languageSelector = new UIComponentImpl.Builder(ItemType.WHITE_BANNER)
                .name(Messages.GUI_MAIN_LANGUAGESELECTOR_NAME.getString(getViewer().getUniqueId()))
                .lore(Messages.GUI_MAIN_LANGUAGESELECTOR_LORE.getStringList(getViewer().getUniqueId()))
                .slot(11).build();
        languageSelector.setListener(ClickType.LEFT_CLICK, () -> InventoryDrawer.open(new LangSelector(this, getViewer())));
        add(languageSelector);
    }

    private void addManagePunishments() {
        if (!getViewer().hasPermission("punishmanager.gui.managepunishments")) return;
        UIComponent managePunishments = new UIComponentImpl.Builder(ItemType.DIAMOND_AXE)
                .name(Messages.GUI_MAIN_MANAGEPUNISHMENTS_NAME.getString(getViewer().getUniqueId()))
                .lore(Messages.GUI_MAIN_MANAGEPUNISHMENTS_LORE.getStringList(getViewer().getUniqueId()))
                .slot(13).build();
        managePunishments.setListener(ClickType.LEFT_CLICK, () -> InventoryDrawer.open(new ManagePunishments(this, getViewer())));
        add(managePunishments);
    }

    private void addAdminPanel() {
        if (!getViewer().hasPermission("punishmanager.gui.admin")) return;
        UIComponent adminPanel = new UIComponentImpl.Builder(ItemType.COMMAND_BLOCK)
                .name(Messages.GUI_MAIN_ADMINPANEL_NAME.getString(getViewer().getUniqueId()))
                .lore(Messages.GUI_MAIN_ADMINPANEL_LORE.getStringList(getViewer().getUniqueId()))
                .slot(15).build();
        adminPanel.setPermission(ClickType.LEFT_CLICK, "punishmanager.gui.admin");
        adminPanel.setListener(ClickType.LEFT_CLICK, () -> InventoryDrawer.open(new AdminPanel(this, getViewer())));
        add(adminPanel);
    }
}
