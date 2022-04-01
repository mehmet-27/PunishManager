package com.mehmet_27.punishmanager.inventories;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.utils.ProtocolizeUtils;
import com.mehmet_27.punishmanager.utils.Utils;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.mehmet_27.punishmanager.utils.Utils.color;

//TODO add multi-page support
public class LanguageSelector extends UIFrame{

    PunishManager plugin;
    ConfigManager configManager;
    Locale viewerLocale;

    public LanguageSelector(UIFrame parent, InventoryType type, @NotNull ProxiedPlayer viewer) {
        super(parent, type, viewer);
        plugin = PunishManager.getInstance();
        configManager = plugin.getConfigManager();
        viewerLocale = plugin.getOfflinePlayers().get(viewer.getName()).getLocale();
        title(color(configManager.getMessage("gui.languageselector.title", viewer.getName())
                .replace("{0}", viewerLocale.toString())));
        List<String> localeNames = configManager.getAvailableLocales().stream().map(Locale::toString).sorted().collect(Collectors.toList());
        // Sort locales alphabetically
        for (int i = 0; i < localeNames.size(); i++) {
            ItemStack itemStack = new ItemStack(ItemType.PAPER);
            itemStack.displayName(localeNames.get(i));
            item(i, itemStack);
        }
        ItemStack back = new ItemStack(ItemType.ARROW);
        back.displayName(color(configManager.getMessage("gui.backbutton.name", viewer.getName())));
        item(53, back);
        onClick(click -> {
            click.cancelled(true);
            if (click.clickedItem() == null) return;
            if (click.clickedItem().displayName(true).toString().contains(color(configManager.getMessage("gui.backbutton.name", viewer.getName())))){
                ProtocolizeUtils.openInventory(getParent(), Protocolize.playerProvider().player(viewer.getUniqueId()));
                return;
            }
            String name = click.clickedItem().displayName(true).toString();
            Locale locale = Utils.stringToLocale(name.substring(name.lastIndexOf("_") - 2, name.lastIndexOf("_") + 3));
            PunishManager.getInstance().getStorageManager().updateLanguage(viewer, locale);
            PunishManager.getInstance().getOfflinePlayers().get(viewer.getName()).setLocale(locale);
            ProtocolizeUtils.openMainInventory(viewer);
        });
    }
}
