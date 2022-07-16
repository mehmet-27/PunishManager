package com.mehmet_27.punishmanager.bungee.inventory;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.PMBungee;
import com.mehmet_27.punishmanager.bungee.inventory.inventories.ConfirmationFrame;
import com.mehmet_27.punishmanager.utils.Utils;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.api.inventory.InventoryClose;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryController {

    private static final Map<UUID, UIFrame> frames = new HashMap<>();

    public void onClose(InventoryClose close) {
        UUID uuid = close.player().uniqueId();
        frames.remove(uuid);
    }

    public void onInteract(InventoryClick click) {
        ProtocolizePlayer player = click.player();

        UIFrame frame = frames.get(player.uniqueId());
        if (frame == null) {
            return;
        }

        click.cancelled(true);

        if (click.inventory() == null || click.inventory().type() == InventoryType.PLAYER) {
            return;
        }

        UIComponent component = frame.getComponent(click.slot());
        if (component == null) {
            return;
        }

        ClickType clickType = click.clickType();
        Runnable listener = component.getListener(clickType);
        if (listener == null) {
            return;
        }

        String permission = component.getPermission(clickType);
        if (permission != null) {
            ProxiedPlayer proxiedPlayer = PMBungee.getInstance().getProxy().getPlayer(player.uniqueId());
            if (!hasPermission(proxiedPlayer, permission)) {
                PunishManager.getInstance().getMethods().sendMessage(proxiedPlayer, PunishManager.getInstance().getConfigManager().getMessage("gui.clickNoPerm"));
                return;
            }
        }

        if (component.isConfirmationRequired(clickType)) {
            listener = () -> InventoryDrawer.open(new ConfirmationFrame(frame, frame.getViewer(), component.getListener(clickType)));
        }

        Runnable finalListener = listener;
        PMBungee.getInstance().getProxy().getScheduler().runAsync(PMBungee.getInstance(), () -> {
            ItemStack currentItem = click.clickedItem();
            if (currentItem == null) return;

            click.clickedItem().lore(Collections.singletonList(Utils.color("&7Loading...")), true);
            finalListener.run();
        });
    }

    private boolean hasPermission(ProxiedPlayer player, String permission) {
        return player.hasPermission(permission);
    }

    public static void register(UIFrame frame) {
        frames.put(frame.getViewer().getUniqueId(), frame);
    }

    public static boolean isRegistered(ProxiedPlayer player) {
        return frames.containsKey(player.getUniqueId());
    }

    public static void runCommand(ProxiedPlayer player, String command, boolean update, String... args) {
        PMBungee plugin = PMBungee.getInstance();
        String finalCommand = command + " " + String.join(" ", args);

        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            plugin.getProxy().getPluginManager().dispatchCommand(player, finalCommand);
            if (!update) {
                Protocolize.playerProvider().player(player.getUniqueId()).closeInventory();
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
