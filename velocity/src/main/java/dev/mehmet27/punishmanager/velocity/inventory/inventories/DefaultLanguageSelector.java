package dev.mehmet27.punishmanager.velocity.inventory.inventories;

import com.velocitypowered.api.proxy.Player;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.velocity.inventory.*;
import dev.mehmet27.punishmanager.managers.ConfigManager;
import dev.mehmet27.punishmanager.utils.Messages;
import dev.mehmet27.punishmanager.utils.Utils;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.data.ItemType;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DefaultLanguageSelector extends UIFrame {
    private final PunishManager punishManager = PunishManager.getInstance();
    private final ConfigManager configManager = punishManager.getConfigManager();

    public DefaultLanguageSelector(UIFrame parent, Player viewer) {
        super(parent, viewer);
    }

    @Override
    public String getTitle() {
        return Messages.GUI_DEFAULTLANGUAGESELECTOR_TITLE.getString(getViewer().getUniqueId())
                .replace("{0}", configManager.getDefaultLocale().toString());
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
                    .slot(i + 9)
                    .build();
            component.setListener(ClickType.LEFT_CLICK, () -> {
                String name = Utils.stripColor(component.getItem().displayName(true));
                Locale newLocale = Utils.stringToLocale(name);

                Path path = PunishManager.getInstance().getMethods().getDataFolder().resolve("config.yml");
                Charset charset = StandardCharsets.UTF_8;
                try {
                    String content = new String(Files.readAllBytes(path), charset);
                    content = content.replaceAll("default-server-language: ([\"'])[a-zA-Z]{2}_[a-zA-Z]{2}([\"'])", String.format("default-server-language: '%s'", newLocale));
                    Files.write(path, content.getBytes(charset));
                    punishManager.getConfigManager().setDefaultLocale(newLocale);
                    punishManager.getMethods().getCommandManager().updateDefaultLocale();
                    Utils.sendText(getViewer().getUniqueId(), "main.setdefaultlanguage", message -> message.replace("{0}", newLocale.toString()));
                    updateFrame();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            add(component);
        }
    }

    private void updateFrame() {
        InventoryDrawer.open(this);
    }
}
