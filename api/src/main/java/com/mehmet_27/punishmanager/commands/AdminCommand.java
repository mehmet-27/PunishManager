package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.configuration.Configuration;
import com.mehmet_27.punishmanager.configuration.ConfigurationProvider;
import com.mehmet_27.punishmanager.configuration.YamlConfiguration;
import com.mehmet_27.punishmanager.importing.AdvancedBanPunishmentType;
import com.mehmet_27.punishmanager.importing.SupportedPlugins;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.storage.DBCore;
import com.mehmet_27.punishmanager.storage.MySQLCore;
import com.mehmet_27.punishmanager.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.admin")
public class AdminCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @Dependency
    private PunishManager punishManager;

    @Dependency
    private ConfigManager configManager;

    @CommandCompletion("@players Reason")
    @Description("{@@admin.description}")
    @Subcommand("admin")
    public void admin(CommandIssuer issuer) {

    }

    @Description("{@@admin.description}")
    @Subcommand("admin")
    public class AdminSubCommands extends BaseCommand {

        @Subcommand("import")
        @CommandCompletion("AdvancedBan")
        @Description("{@@admin.import.description}")
        public void importC(CommandIssuer issuer, String pluginName) {
            UUID issuerUuid = issuer.isPlayer() ? issuer.getUniqueId() : null;
            Configuration importConfig = null;
            try {
                File importFile = new File(configManager.getDataFolder() + File.separator + "import.yml");
                if (!importFile.exists()) {
                    configManager.createFile(importFile);
                    Configuration newImport = new Configuration();
                    newImport.set("host", "localhost");
                    newImport.set("port", 3306);
                    newImport.set("database", "database");
                    newImport.set("username", "username");
                    newImport.set("password", "password");
                    ConfigurationProvider.getProvider(YamlConfiguration.class).save(newImport, importFile);
                    Utils.sendText(issuerUuid, "punishmanager.admin.import.fileCreated");
                    return;
                }
                importConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(importFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (importConfig == null) {
                Utils.sendText(issuerUuid, "punishmanager.admin.import.nullConfiguration");
                return;
            }
            SupportedPlugins plugin;
            try {
                plugin = SupportedPlugins.valueOf(pluginName.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                Utils.sendText(issuerUuid, "punishmanager.admin.import.unsupportedPlugin");
                return;
            }

            Utils.sendText(issuerUuid, "punishmanager.admin.import.start");

            if (plugin.equals(SupportedPlugins.ADVANCEDBAN)) {
                Utils.sendText(issuerUuid, "punishmanager.admin.import.connecting");
                DBCore core = new MySQLCore(importConfig.getString("host"),
                        importConfig.getString("database"),
                        importConfig.getInt("port"),
                        importConfig.getString("username"),
                        importConfig.getString("password"));
                String query = "SELECT * FROM Punishments";
                List<Punishment> punishments = new ArrayList<>();
                try (Connection connection = core.getDataSource().getConnection()) {
                    ResultSet result = connection.createStatement().executeQuery(query);
                    while (result.next()) {
                        String playerName = result.getString("name");
                        UUID uuid = UUID.fromString(result.getString("uuid"));
                        String reason = result.getString("reason");
                        String operator = result.getString("operator");
                        String server = "ALL";
                        long start = result.getLong("start");
                        long end = result.getLong("end");
                        AdvancedBanPunishmentType advancedBanPunishmentType;
                        Punishment.PunishType punishType;
                        try {
                            advancedBanPunishmentType = AdvancedBanPunishmentType.valueOf(result.getString("punishmentType"));
                        } catch (IllegalArgumentException e) {
                            punishManager.getLogger().warning(configManager.getMessage("punishmanager.admin.import.unsupportedPunishType")
                                    .replace("%type%", result.getString("punishmentType")));
                            continue;
                        }
                        punishType = advancedBanPunishmentType.getType();
                        Punishment punishment = new Punishment(playerName, uuid, null, punishType, reason, operator, null, server, start, end, 0);
                        punishments.add(punishment);
                    }
                    Utils.sendText(issuerUuid, "punishmanager.admin.import.found", message ->
                            message.replace("%total%", String.valueOf(punishments.size())));
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                int imported = 0;
                for (Punishment punishment : punishments) {
                    if (!punishment.isExpired()) {
                        storageManager.addPunishToPunishments(punishment);
                        imported++;
                    }
                    storageManager.addPunishToHistory(punishment);
                }
                int finalImported = imported;
                Utils.sendText(issuerUuid, "punishmanager.admin.import.imported", message ->
                        message.replace("%total%", String.valueOf(finalImported)));
                core.getDataSource().close();
                Utils.sendText(issuerUuid, "punishmanager.admin.import.end");
            }
        }

        @Subcommand("reload")
        @Description("{@@punishmanager.admin.reload.description}")
        public void reload(CommandIssuer sender) {
            punishManager.getConfigManager().setup();
            UUID operatorUuid = sender.isPlayer() ? sender.getUniqueId() : null;
            punishManager.getMethods().getCommandManager().updateDefaultLocale();
            Utils.sendText(operatorUuid, "punishmanager.admin.reload.done");
        }
    }
}