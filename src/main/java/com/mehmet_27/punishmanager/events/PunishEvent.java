package com.mehmet_27.punishmanager.events;

import com.mehmet_27.punishmanager.PlayerPunishEvent;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.DiscordManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Locale;

import static net.md_5.bungee.event.EventPriority.HIGHEST;

public class PunishEvent implements Listener {
    private final PunishManager plugin = PunishManager.getInstance();
    private final ConfigManager configManager = plugin.getConfigManager();
    private final DiscordManager discordManager = plugin.getDiscordManager();

    @EventHandler(priority = HIGHEST)
    public void onPunish(PlayerPunishEvent event) {
        Punishment punishment = event.getPunishment();
        CommandSender operator = "CONSOLE".equals(punishment.getOperator()) ? plugin.getProxy().getConsole() : plugin.getProxy().getPlayer(punishment.getOperator());

        if (configManager.getExemptPlayers().contains(punishment.getPlayerName())) {
            operator.sendMessage(new TextComponent(configManager.getMessage("main.exempt-player", operator.getName())));
            return;
        }

        //Adding punish to database
        plugin.getDataBaseManager().AddPunish(punishment);

        //Sending successfully punished message to operator
        String path = punishment.getPunishType().name().toLowerCase(Locale.ENGLISH) + ".punished";
        operator.sendMessage(new TextComponent(configManager.getMessage(path, operator.getName()).
                replace("%player%", punishment.getPlayerName())));

        //Sends the punish message
        Utils.sendLayout(punishment);

        //Sending the punish announcement
        String announcement = event.getAnnounceMessage();
        if (announcement == null || announcement.isEmpty()) return;
        announcement = announcement.
                replace("%reason%", punishment.getReason()).
                replace("%operator%", punishment.getOperator()).
                replace("%player%", punishment.getPlayerName()).
                replace("%duration%", punishment.getDuration());
        plugin.getProxy().broadcast(new TextComponent(announcement));

        //Give "punished role" on Discord
        if (punishment.getPunishType().isMute()) {
            plugin.getDiscordManager().givePunishedRole(punishment);
        }
        discordManager.sendEmbed(punishment);
    }
}
