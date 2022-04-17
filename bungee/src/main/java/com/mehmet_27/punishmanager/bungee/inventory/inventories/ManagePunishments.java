package com.mehmet_27.punishmanager.bungee.inventory.inventories;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.inventory.InventoryUtils;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Messages;
import com.mehmet_27.punishmanager.utils.UtilsCore;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class ManagePunishments extends UIFrame {

    public ManagePunishments(@Nullable UIFrame parent, InventoryType type, @NotNull ProxiedPlayer viewer, int startIndex) {
        super(parent, type, viewer);
        StorageManager storageManager = PunishManager.getInstance().getStorageManager();
        List<Punishment> punishments = storageManager.getAllPunishments();

        title(Messages.GUI_MANAGEPUNISHMENTS_TITLE.getString(viewer.getName())
                .replace("{0}", "" + punishments.size()));
        int index = 0;
        for (int i = startIndex; i < punishments.size(); i++) {
            if (index == 45) break;
            Punishment punishment = punishments.get(i);
            ItemStack is = new ItemStack(ItemType.PAPER);
            is.displayName(Messages.GUI_MANAGEPUNISHMENTS_PUNISHMENT_NAME.getString(viewer.getName())
                    .replace("%id%", "" + punishment.getId()));

            List<String> lore = Messages.GUI_MANAGEPUNISHMENTS_PUNISHMENT_LORE.getStringList(viewer.getName())
                    .stream().map(string -> UtilsCore.replacePunishmentPlaceholders(string, punishment)).collect(Collectors.toList());
            is.lore(lore, true);
            item(index, is);
            index++;
            startIndex++;
        }
        int finalStartIndex = startIndex;

        ItemStack backButton = new Item().back(viewer.getName());
        item(53, backButton);

        // next button
        ItemStack nextButton = new ItemStack(ItemType.ARROW);
        nextButton.displayName(Messages.GUI_MANAGEPUNISHMENTS_NEXT_NAME.getString(viewer.getName()));
        if (startIndex >= 0 && punishments.size() > startIndex) {
            item(52, nextButton);
        }

        // previous button
        ItemStack previousButton = new ItemStack(ItemType.ARROW);
        previousButton.displayName(Messages.GUI_MANAGEPUNISHMENTS_PREVIOUS_NAME.getString(viewer.getName()));
        if (startIndex > 45) {
            item(51, previousButton);
        }


        onClick(click -> {
            click.cancelled(true);
            ItemStack clickedItem = click.clickedItem();
            if (click.clickedItem() == null) return;
            //back
            if (clickedItem.equals(backButton)){
                ProtocolizePlayer protocolizePlayer = Protocolize.playerProvider().player(viewer.getUniqueId());
                InventoryUtils.openInventory(getParent(), protocolizePlayer);
                return;
            }
            //next
            if (clickedItem.equals(nextButton)) {
                InventoryUtils.openInventory(new ManagePunishments(this, InventoryType.GENERIC_9X6, viewer, finalStartIndex), Protocolize.playerProvider().player(viewer.getUniqueId()));
                return;
            }
            //previous
            if (clickedItem.equals(previousButton)) {
                InventoryUtils.openInventory(getParent(), Protocolize.playerProvider().player(viewer.getUniqueId()));
            }
        });
    }
}
