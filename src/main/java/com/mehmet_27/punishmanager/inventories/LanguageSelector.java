package com.mehmet_27.punishmanager.inventories;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.utils.Utils;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.mehmet_27.punishmanager.utils.Utils.color;

//TODO add multi-page support
public class LanguageSelector extends Inventory {

    public LanguageSelector(InventoryType type, ProxiedPlayer sender) {
        super(type);
        ConfigManager configManager = PunishManager.getInstance().getConfigManager();
        Locale senderLocale = PunishManager.getInstance().getOfflinePlayers().get(sender.getName()).getLocale();
        title(color(configManager.getMessage("gui.languageselector-title", sender.getName())
                .replace("{0}", senderLocale.toString())));
        List<String> localeNames = configManager.getAvailableLocales().stream().map(Locale::toString).sorted().collect(Collectors.toList());
        // Sort locales alphabetically
        for (int i = 0; i < localeNames.size(); i++) {
            ItemStack itemStack = new ItemStack(ItemType.PAPER);
            itemStack.displayName(localeNames.get(i));
            item(i, itemStack);
        }
        ItemStack back = new ItemStack(ItemType.ARROW);
        back.displayName(color(configManager.getMessage("gui.backbutton-name", sender.getName())));
        item(53, back);
        onClick(click -> {
            click.cancelled(true);
            if (click.clickedItem() == null) return;
            if (click.clickedItem().displayName(true).toString().contains(color(configManager.getMessage("gui.backbutton-name", sender.getName())))){
                Utils.openInventory(new Main(InventoryType.GENERIC_9X3, sender), Protocolize.playerProvider().player(sender.getUniqueId()));
                return;
            }
            String name = click.clickedItem().displayName(true).toString();
            Locale locale = Utils.stringToLocale(name.substring(name.lastIndexOf("_") - 2, name.lastIndexOf("_") + 3));
            PunishManager.getInstance().getStorageManager().updateLanguage(sender, locale);
            PunishManager.getInstance().getOfflinePlayers().get(sender.getName()).setLocale(locale);
            Utils.openInventory(new LanguageSelector(InventoryType.GENERIC_9X6, sender), Protocolize.playerProvider().player(sender.getUniqueId()));
        });
    }
}
