package dev.mehmet27.punishmanager.bukkit.inventory.inventories;

import com.cryptomorin.xseries.XMaterial;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.bukkit.inventory.*;
import dev.mehmet27.punishmanager.managers.StorageManager;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.utils.Messages;
import dev.mehmet27.punishmanager.utils.Paginator;
import dev.mehmet27.punishmanager.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.stream.Collectors;

public class ManagePunishments extends UIFrame {

    private final Paginator paginator;
    private final StorageManager storageManager = PunishManager.getInstance().getStorageManager();
    private final List<Punishment> punishments = storageManager.getAllPunishments();

    public ManagePunishments(UIFrame parent, Player viewer) {
        super(parent, viewer);
        paginator = new Paginator(getSize() - 9, punishments);
    }

    @Override
    public String getTitle() {
        return Messages.GUI_MANAGEPUNISHMENTS_TITLE.getString(getViewer().getUniqueId())
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
        List<String> lore = Messages.GUI_MANAGEPUNISHMENTS_PUNISHMENT_LORE.getStringList(getViewer().getUniqueId())
                .stream().map(string -> Utils.replacePunishmentPlaceholders(string, punishment))
                .collect(Collectors.toList());
        UIComponent component = new UIComponentImpl.Builder(XMaterial.PAPER)
                .name(Messages.GUI_MANAGEPUNISHMENTS_PUNISHMENT_NAME.getString(getViewer().getUniqueId())
                        .replace("%id%", "" + punishment.getId()))
                .lore(lore)
                .slot(slot)
                .build();
        component.setListener(ClickType.LEFT, () -> {
            if (punishment.isMuted()) {
                InventoryController.runCommand(getViewer(), "unmute", true, punishment.getPlayerName());
            }
            if (punishment.isBanned()) {
                InventoryController.runCommand(getViewer(), "unban", true, punishment.getPlayerName());
            }
            punishments.remove(punishment);
        });
        component.setConfirmationRequired(ClickType.LEFT);
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
