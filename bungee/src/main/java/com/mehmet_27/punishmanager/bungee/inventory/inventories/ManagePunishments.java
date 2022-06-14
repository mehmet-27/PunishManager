package com.mehmet_27.punishmanager.bungee.inventory.inventories;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.inventory.*;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Messages;
import com.mehmet_27.punishmanager.utils.Paginator;
import com.mehmet_27.punishmanager.utils.Utils;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.data.ItemType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.stream.Collectors;

public class ManagePunishments extends UIFrame {

    private final Paginator paginator;
    private final StorageManager storageManager = PunishManager.getInstance().getStorageManager();
    private final List<Punishment> punishments = storageManager.getAllPunishments();

    public ManagePunishments(UIFrame parent, ProxiedPlayer viewer) {
        super(parent, viewer);
        paginator = new Paginator(getSize() - 9, punishments);
    }

    @Override
    public String getTitle() {
        return Messages.GUI_MANAGEPUNISHMENTS_TITLE.getString(getViewer().getName())
                .replace("{0}", "" + punishments.size());
    }

    @Override
    public int getSize() {
        return 9 * 6;
    }

    @Override
    public void createComponents() {
        add(Components.getBackComponent(getParent(), 53, getViewer()));

        int slot = 0;
        for (int i = paginator.getMinIndex(); paginator.isValidIndex(i); i++) {
            Punishment punishment = punishments.get(i);
            addPunish(slot, punishment);
            slot++;
        }
        add(Components.getPreviousPageComponent(51, this::previousPage, paginator, getViewer()));
        add(Components.getNextPageComponent(52, this::nextPage, paginator, getViewer()));
    }

    private void addPunish(int slot, Punishment punishment) {
        List<String> lore = Messages.GUI_MANAGEPUNISHMENTS_PUNISHMENT_LORE.getStringList(getViewer().getName())
                .stream().map(string -> Utils.replacePunishmentPlaceholders(string, punishment))
                .collect(Collectors.toList());
        UIComponent component = new UIComponentImpl.Builder(ItemType.PAPER)
                .name(Messages.GUI_MANAGEPUNISHMENTS_PUNISHMENT_NAME.getString(getViewer().getName())
                        .replace("%id%", "" + punishment.getId()))
                .lore(lore)
                .slot(slot)
                .build();
        component.setListener(ClickType.LEFT_CLICK, () -> {
            if (punishment.isMuted()){
                InventoryController.runCommand(getViewer(), "unmute", true, punishment.getPlayerName());
            }
            if (punishment.isBanned()){
                InventoryController.runCommand(getViewer(), "unban", true, punishment.getPlayerName());
            }
            punishments.remove(punishment);
        });
        component.setConfirmationRequired(ClickType.LEFT_CLICK);
        add(component);
    }

    private void previousPage() {
        if (paginator.previousPage()) {
            updateFrame();
        }
    }

    private void nextPage() {
        if (paginator.nextPage()) {
            updateFrame();
        }
    }

    private void updateFrame() {
        InventoryDrawer.open(this);
    }
}
