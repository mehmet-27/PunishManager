package dev.mehmet27.punishmanager.bukkit.inventory.inventories;

import com.cryptomorin.xseries.XMaterial;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.bukkit.PMBukkit;
import dev.mehmet27.punishmanager.bukkit.inventory.*;
import dev.mehmet27.punishmanager.managers.ConfigManager;
import dev.mehmet27.punishmanager.utils.Messages;
import dev.mehmet27.punishmanager.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class LangSelector extends UIFrame {

    private final PunishManager punishManager = PunishManager.getInstance();
    private final ConfigManager configManager = punishManager.getConfigManager();

    public LangSelector(UIFrame parent, Player viewer) {
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
            UIComponent component = new UIComponentImpl.Builder(XMaterial.PAPER)
                    .name(localeNames.get(i))
                    .slot(i)
                    .build();
            component.setListener(ClickType.LEFT, () -> {
                String name = ChatColor.stripColor(component.getItemMeta().getDisplayName());
                Locale newLocale = Utils.stringToLocale(name);
                punishManager.getStorageManager().updateLanguage(getViewer().getUniqueId(), newLocale);
                punishManager.getOfflinePlayers().get(getViewer().getUniqueId()).setLocale(newLocale);
                PMBukkit.getInstance().getCommandManager().setPlayerLocale(getViewer(),
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
