package com.mehmet_27.punishmanager.bungee.inventory.inventories;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.PMBungee;
import com.mehmet_27.punishmanager.bungee.inventory.*;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.utils.Messages;
import com.mehmet_27.punishmanager.utils.Utils;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.data.ItemType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class LangSelector extends UIFrame {

    private final PunishManager punishManager = PunishManager.getInstance();
    private final ConfigManager configManager = punishManager.getConfigManager();

    public LangSelector(UIFrame parent, ProxiedPlayer viewer) {
        super(parent, viewer);
    }

    @Override
    public String getTitle() {
        Locale viewerLocale = punishManager.getOfflinePlayers().get(getViewer().getUniqueId()).getLocale();
        return Messages.GUI_LANGUAGESELECTOR_TITLE.getString(getViewer().getUniqueId())
                .replace("{0}", viewerLocale.toString());
    }

    @Override
    public int getSize() {
        return 9 * 6;
    }

    @Override
    public void createComponents() {
        add(Components.getBackComponent(getParent(), 53, getViewer()));

        List<String> localeNames = configManager.getAvailableLocales().stream().map(Locale::toString).sorted().collect(Collectors.toList());
        for (int i = 0; i < localeNames.size(); i++) {
            UIComponent component = new UIComponentImpl.Builder(ItemType.PAPER)
                    .name(localeNames.get(i))
                    .slot(i)
                    .build();
            component.setListener(ClickType.LEFT_CLICK, () -> {
                String name = ChatColor.stripColor(component.getItem().displayName(true));
                Locale newLocale = Utils.stringToLocale(name);
                punishManager.getStorageManager().updateLanguage(getViewer().getUniqueId(), newLocale);
                punishManager.getOfflinePlayers().get(getViewer().getUniqueId()).setLocale(newLocale);
                PMBungee.getInstance().getCommandManager().setIssuerLocale(getViewer(),
                        punishManager.getOfflinePlayers().get(getViewer().getUniqueId()).getLocale());
                Utils.sendText(getViewer().getUniqueId(), "main.setlanguage", message -> message.replace("{0}", newLocale.toString()));
                updateFrame();
            });
            add(component);
        }
    }

    private void updateFrame() {
        InventoryDrawer.open(this);
    }
}
