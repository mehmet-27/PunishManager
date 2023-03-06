package dev.mehmet27.punishmanager.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.ConfigManager;
import dev.mehmet27.punishmanager.velocity.inventory.InventoryController;
import dev.mehmet27.punishmanager.velocity.listeners.*;
import dev.mehmet27.punishmanager.velocity.managers.PMVelocityCommandManager;
import org.bstats.charts.SingleLineChart;
import org.bstats.velocity.Metrics;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(
        id = "punishmanager",
        name = "PunishManager",
        version = "1.4.10",
        description = "Advanced punish management plugin.",
        authors = {"Mehmet_27"}
)
public class PMVelocity {

    private static PMVelocity instance;

    private ConfigManager configManager;
    private PMVelocityCommandManager commandManager;

    private InventoryController inventoryController;

    public static PMVelocity getInstance() {
        return instance;
    }

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private final Metrics.Factory metricsFactory;

    @Inject
    public PMVelocity(ProxyServer server, @DataDirectory Path dataDirectory, Metrics.Factory metricsFactory) {
        this.server = server;
        this.logger = Logger.getLogger("punishmanager");
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;
        PunishManager.getInstance().onEnable(new VelocityMethods());
        configManager = PunishManager.getInstance().getConfigManager();
        commandManager = new PMVelocityCommandManager(server, this);

        server.getEventManager().register(this, new ConnectionListener(this));
        server.getEventManager().register(this, new ChatListener());
        server.getEventManager().register(this, new CommandListener(this));
        server.getEventManager().register(this, new PunishListener(this));
        server.getEventManager().register(this, new PunishRevokeListener());

        inventoryController = new InventoryController();

        Metrics metrics = metricsFactory.make(this, 15231);
        metrics.addCustomChart(new SingleLineChart("punishments", () -> PunishManager.getInstance().getStorageManager().getPunishmentsCount()));
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        PunishManager.getInstance().onDisable();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PMVelocityCommandManager getCommandManager() {
        return commandManager;
    }

    public InventoryController getInventoryController() {
        return inventoryController;
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }
}
