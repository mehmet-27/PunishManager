package com.mehmet_27.punishmanager.velocity.inventory;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.utils.Utils;
import com.mehmet_27.punishmanager.velocity.PMVelocity;
import com.mehmet_27.punishmanager.velocity.inventory.inventories.ConfirmationFrame;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.api.inventory.InventoryClose;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.inventory.InventoryType;

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
            Player velocityPlayer = PMVelocity.getInstance().getServer().getPlayer(player.uniqueId()).orElse(null);
            if (velocityPlayer == null) return;
            if (!hasPermission(velocityPlayer, permission)) {
                PunishManager.getInstance().getMethods().sendMessage(velocityPlayer.getUniqueId(), PunishManager.getInstance().getConfigManager().getMessage("gui.clickNoPerm"));
                return;
            }
        }

        if (component.isConfirmationRequired(clickType)) {
            listener = () -> InventoryDrawer.open(new ConfirmationFrame(frame, frame.getViewer(), component.getListener(clickType)));
        }

        Runnable finalListener = listener;
        PMVelocity.getInstance().getServer().getScheduler().buildTask(PMVelocity.getInstance(), () -> {
            ItemStack currentItem = (ItemStack) click.clickedItem();
            if (currentItem == null) return;

            click.clickedItem().lore(Collections.singletonList(Utils.color("&7Loading...")), true);
            finalListener.run();
        }).schedule();
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
        PMVelocity plugin = PMVelocity.getInstance();
        String finalCommand = command + " " + String.join(" ", args);

        plugin.getServer().getScheduler().buildTask(plugin, () -> {
            plugin.getServer().getCommandManager().executeAsync(player, finalCommand);
            if (!update) {
                Protocolize.playerProvider().player(player.getUniqueId()).closeInventory();
            } else {
                UIFrame currentFrame = frames.get(player.getUniqueId());
                if (currentFrame instanceof ConfirmationFrame) {
                    currentFrame = currentFrame.getParent();
                }
                InventoryDrawer.open(currentFrame);
            }
        }).schedule();
    }
}
