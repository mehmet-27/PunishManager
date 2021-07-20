package com.mehmet_27.punishmanager.managers;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BungeeCommandIssuer;
import co.aikar.commands.BungeeCommandManager;
import co.aikar.commands.ConditionFailedException;
import com.mehmet_27.punishmanager.PunishManager;
import net.md_5.bungee.api.CommandSender;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.logging.Level;

public class CommandManager extends BungeeCommandManager {
    private final PunishManager plugin;

    public CommandManager(PunishManager plugin) {
        super(plugin);
        this.plugin = plugin;
        this.permissionManager = PunishManager.getPermissionManager();
    }

    private void registerDependencies() {
        registerDependency(DisconnectManager.class, PunishManager.getInstance().getDisconnectManager());
        registerDependency(PunishmentManager.class, PunishManager.getInstance().getPunishmentManager());
    }

    public void registerCommands() {
        // Mark a package that we want to reflect
        Reflections reflections = new Reflections("com.mehmet_27.punishmanager.commands");
        // Get all classes, which `extends BaseCommand`
        Set<Class<? extends BaseCommand>> commands = reflections.getSubTypesOf(BaseCommand.class);
        // Just to make sure, that all commands were registered
        plugin.getLogger().info(String.format("Registering %d base commands...", commands.size()));

        for (Class<? extends BaseCommand> c : commands) {
            // ACF already registers nested classes
            if (c.isMemberClass() || Modifier.isStatic(c.getModifiers())) {
                continue;
            }
            try {
                // Make an instance of commands
                BaseCommand baseCommand = c.getConstructor().newInstance();
                // Register them in ACF
                registerCommand(baseCommand);
            } catch (Exception ex) {
                plugin.getLogger().log(Level.SEVERE, "Error registering command", ex);
            }
        }
    }
    public void registerConditions() {
        getCommandConditions().addCondition(String.class, "other_player", (context, exec, value) -> {
            BungeeCommandIssuer issuer = context.getIssuer();
            if (issuer.getPlayer().getName().equals(value)){
                throw new ConditionFailedException("You cannot use this command on yourself!");
            }
        });
    }
}
