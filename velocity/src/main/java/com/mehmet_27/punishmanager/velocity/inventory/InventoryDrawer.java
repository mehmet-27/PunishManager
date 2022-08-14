package com.mehmet_27.punishmanager.velocity.inventory;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.velocity.PMVelocity;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.data.inventory.InventoryType;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryDrawer {

    private static final PMVelocity plugin = PMVelocity.getInstance();
    private static final ConcurrentHashMap<UUID, UIFrame> OPENING = new ConcurrentHashMap<>();

    public static void open(UIFrame frame) {
        if (frame == null) {
            return;
        }
        UUID uuid = frame.getViewer().getUniqueId();
        if (frame.equals(OPENING.get(uuid))) {
            return;
        }

        OPENING.put(uuid, frame);
        plugin.getServer().getScheduler().buildTask(plugin, () -> {
            Inventory inventory = prepareInventory(frame);

            if (!frame.equals(OPENING.get(uuid))) {
                return;
            }
            plugin.getServer().getScheduler().buildTask(plugin, () -> {
                if (frame.getViewer().getUniqueId() == null) return;
                Protocolize.playerProvider().player(frame.getViewer().getUniqueId()).openInventory(inventory);
                InventoryController.register(frame);
                OPENING.remove(uuid);
            }).schedule();
        }).schedule();
    }

    private static Inventory prepareInventory(UIFrame frame) {
        Inventory inventory = new Inventory(InventoryType.chestInventoryWithSize(frame.getSize()));
        inventory.title(frame.getTitle());
        long start = System.currentTimeMillis();
        setComponents(inventory, frame);

        PunishManager.getInstance().debug(String.format("It took %s millisecond(s) to load the frame %s for %s",
                System.currentTimeMillis() - start, frame.getTitle(), frame.getViewer().getUsername()));

        inventory.onClick(click -> plugin.getInventoryController().onInteract(click));
        inventory.onClose(inventoryClose -> plugin.getInventoryController().onClose(inventoryClose));

        return inventory;
    }

    private static void setComponents(Inventory inventory, UIFrame frame) {
        frame.clear();
        try {
            frame.createComponents();
        } catch (NoSuchFieldError ex) {
            return;
        }

        Set<UIComponent> components = frame.getComponents();
        if (components.isEmpty()) {
            plugin.getLogger().warning(String.format("Frame %s has no components", frame.getTitle()));
            return;
        }
        for (UIComponent c : frame.getComponents()) {
            if (c.getSlot() >= frame.getSize()) {
                continue;
            }
            inventory.item(c.getSlot(), c.getItem());
        }
    }
}
