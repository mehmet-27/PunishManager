package com.mehmet_27.punishmanager.bukkit.inventory;

import com.mehmet_27.punishmanager.bukkit.PMBukkit;
import com.mehmet_27.punishmanager.bukkit.inventory.inventories.ConfirmationFrame;
import com.mehmet_27.punishmanager.bukkit.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class InventoryController implements Listener {

    private static final Map<UUID, UIFrame> frames = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onClose(InventoryCloseEvent event) {
        HumanEntity entity = event.getPlayer();
        if (!(entity instanceof Player)) {
            return;
        }
        frames.remove(entity.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(InventoryClickEvent event) {
        HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) {
            return;
        }

        UIFrame frame = frames.get(entity.getUniqueId());
        if (frame == null) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() == null || event.getClickedInventory().getType() == InventoryType.PLAYER) {
            return;
        }

        UIComponent component = frame.getComponent(event.getSlot());
        if (component == null) {
            return;
        }

        ClickType click = event.getClick();
        Runnable listener = component.getListener(click);
        if (listener == null) {
            return;
        }

        String permission = component.getPermission(click);
        if (permission != null) {
            if (!hasPermission((Player) entity, permission)) {
                return;
            }
        }

        if (component.isConfirmationRequired(click)) {
            listener = () -> InventoryDrawer.open(new ConfirmationFrame(frame, frame.getViewer(), component.getListener(click)));
        }

        Runnable finalListener = listener;
        Bukkit.getScheduler().runTask(PMBukkit.getInstance(), () -> {
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null) return;

            ItemMeta itemMeta = currentItem.getItemMeta();
            Objects.requireNonNull(itemMeta).setLore(Collections.singletonList(Utils.color("&7Loading...")));
            currentItem.setItemMeta(itemMeta);

            finalListener.run();
        });
    }

    private boolean hasPermission(Player player, String permission) {
        return player.hasPermission(permission);
    }

    public static void register(UIFrame frame) {
        frames.put(frame.getViewer().getUniqueId(), frame);
    }

    public static boolean isRegistered(Player player) {
        return frames.containsKey(player.getUniqueId());
    }

    public static void runCommand(Player player, String command, boolean update, String... args) {
        PMBukkit plugin = PMBukkit.getInstance();
        String finalCommand = command + " " + String.join(" ", args);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            player.performCommand(finalCommand);
            if (!update) {
                player.closeInventory();
            } else {
                UIFrame currentFrame = frames.get(player.getUniqueId());
                if (currentFrame instanceof ConfirmationFrame) {
                    currentFrame = currentFrame.getParent();
                }
                InventoryDrawer.open(currentFrame);
            }
        });
    }
}
