package com.mehmet_27.punishmanager.inventories;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.ProtocolizeUtils;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import static com.mehmet_27.punishmanager.utils.Utils.color;

public class ManagePunishments extends UIFrame {

    PunishManager plugin;
    ConfigManager configManager;
    StorageManager storageManager;
    List<Punishment> punishments;


    public ManagePunishments(@Nullable UIFrame parent, InventoryType type, @NotNull ProxiedPlayer viewer, int startIndex) {
        super(parent, type, viewer);
        plugin = PunishManager.getInstance();
        configManager = plugin.getConfigManager();
        storageManager = plugin.getStorageManager();
        punishments = storageManager.getAllPunishments();

        title(color(configManager.getMessage("gui.managepunishments.title", viewer.getName()).replace("{0}", "" + punishments.size())));
        int index = 0;
        for (int i = startIndex; i < punishments.size(); i++) {
            if (index == 45) break;
            Punishment punishment = punishments.get(i);
            ItemStack is = new ItemStack(ItemType.PAPER);
            is.displayName(color(configManager.getMessage("gui.managepunishments.punishment.name", viewer.getName()).replace("%id%", "" + punishment.getId())));
            List<String> lore = configManager.getStringList("gui.managepunishments.punishment.lore", viewer.getName()).stream().map(string -> string.replace("%reason%", punishment.getReason())
                    .replace("%operator%", punishment.getOperator())
                    .replace("%player%", punishment.getPlayerName())
                    .replace("%type%", punishment.getPunishType().name())
                    .replace("%ip%", "" + punishment.getIp())
                    .replace("%uuid%", punishment.getUuid().toString())
            ).collect(Collectors.toList());
            is.lore(lore, true);
            item(index, is);
            index++;
            startIndex++;
        }
        int finalStartIndex = startIndex;

        ItemStack back = new ItemStack(ItemType.ARROW);
        back.displayName(color(configManager.getMessage("gui.backbutton.name", viewer.getName())));
        item(53, back);

        // next button
        if (startIndex >= 0 && punishments.size() > startIndex) {
            ItemStack next = new ItemStack(ItemType.ARROW);
            next.displayName(color(configManager.getMessage("gui.managepunishments.next.name", viewer.getName())));
            item(52, next);
        }
        // previous button
        if (startIndex > 45) {
            ItemStack previous = new ItemStack(ItemType.ARROW);
            previous.displayName(color(configManager.getMessage("gui.managepunishments.previous.name", viewer.getName())));
            item(51, previous);
        }


        onClick(click -> {
            click.cancelled(true);
            if (click.clickedItem() == null) return;
            //back
            if (click.clickedItem().displayName(true).toString().contains(color(configManager.getMessage("gui.backbutton.name", viewer.getName())))) {
                ProtocolizeUtils.openMainInventory(viewer);
                return;
            }
            //next
            if (click.clickedItem().displayName(true).toString().contains(color(configManager.getMessage("gui.managepunishments.next.name", viewer.getName())))) {
                ProtocolizeUtils.openInventory(new ManagePunishments(this, InventoryType.GENERIC_9X6, viewer, finalStartIndex), Protocolize.playerProvider().player(viewer.getUniqueId()));
                return;
            }
            //previous
            if (click.clickedItem().displayName(true).toString().contains(color(configManager.getMessage("gui.managepunishments.previous.name", viewer.getName())))) {
                ProtocolizeUtils.openInventory(getParent(), Protocolize.playerProvider().player(viewer.getUniqueId()));
            }
        });
    }
}
