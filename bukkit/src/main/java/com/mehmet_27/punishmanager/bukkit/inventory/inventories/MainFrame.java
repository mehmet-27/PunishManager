package com.mehmet_27.punishmanager.bukkit.inventory.inventories;

import com.cryptomorin.xseries.XMaterial;
import com.mehmet_27.punishmanager.bukkit.inventory.InventoryDrawer;
import com.mehmet_27.punishmanager.bukkit.inventory.UIComponent;
import com.mehmet_27.punishmanager.bukkit.inventory.UIComponentImpl;
import com.mehmet_27.punishmanager.bukkit.inventory.UIFrame;
import com.mehmet_27.punishmanager.utils.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class MainFrame extends UIFrame {

    public MainFrame(Player viewer) {
        super(null, viewer);
    }

    @Override
    public String getTitle() {
        return Messages.GUI_MAIN_TITLE.getString(getViewer().getName());
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
        UIComponent languageSelector = new UIComponentImpl.Builder(XMaterial.WHITE_BANNER)
                .name(Messages.GUI_MAIN_LANGUAGESELECTOR_NAME.getString(getViewer().getName()))
                .slot(11).build();
        languageSelector.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new LangSelector(this, getViewer())));
        add(languageSelector);
    }

    private void addManagePunishments() {
        if (!getViewer().hasPermission("punishmanager.gui.managepunishments")) return;
        UIComponent managePunishments = new UIComponentImpl.Builder(XMaterial.DIAMOND_AXE)
                .name(Messages.GUI_MAIN_MANAGEPUNISHMENTS_NAME.getString(getViewer().getName()))
                .slot(13).build();
        managePunishments.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new ManagePunishments(this, getViewer())));
        add(managePunishments);
    }

    private void addAdminPanel() {
        if (!getViewer().hasPermission("punishmanager.gui.admin")) return;
        UIComponent adminPanel = new UIComponentImpl.Builder(XMaterial.COMMAND_BLOCK)
                .name(Messages.GUI_MAIN_ADMINPANEL_NAME.getString(getViewer().getName()))
                .slot(15).build();
        adminPanel.setPermission(ClickType.LEFT, "punishmanager.gui.admin");
        adminPanel.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new AdminPanel(this, getViewer())));
        add(adminPanel);
    }
}
