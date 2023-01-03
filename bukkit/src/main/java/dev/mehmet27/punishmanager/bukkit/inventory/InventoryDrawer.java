package dev.mehmet27.punishmanager.bukkit.inventory;

import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.bukkit.PMBukkit;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryDrawer {

    private static final PMBukkit plugin = PMBukkit.getInstance();
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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Inventory inventory = prepareInventory(frame);

            if (!frame.equals(OPENING.get(uuid))) {
                return;
            }
            Bukkit.getScheduler().runTask(plugin, () -> {
                frame.getViewer().openInventory(inventory);
                InventoryController.register(frame);
                OPENING.remove(uuid);
            });
        });
    }

    private static Inventory prepareInventory(UIFrame frame) {
        Inventory inventory = Bukkit.createInventory(frame.getViewer(), frame.getSize(), frame.getTitle());
        long start = System.currentTimeMillis();
        setComponents(inventory, frame);

        PunishManager.getInstance().debug(String.format("It took %s millisecond(s) to load the frame %s for %s",
                System.currentTimeMillis() - start, frame.getTitle(), frame.getViewer().getName()));

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
            inventory.setItem(c.getSlot(), c.getItem());
        }
    }
}
