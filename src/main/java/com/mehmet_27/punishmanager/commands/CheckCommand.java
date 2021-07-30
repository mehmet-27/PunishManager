package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static com.mehmet_27.punishmanager.Punishment.PunishType.KICK;

@CommandAlias("check")
@CommandPermission("punishmanager.command.check")
public class CheckCommand extends BaseCommand {

    @Dependency
    private final PunishManager plugin = PunishManager.getInstance();
    private final PunishmentManager punishmentManager = plugin.getPunishmentManager();

    @Default
    @CommandCompletion("@players")
    public void check(CommandSender sender, @Name("Player") String playerName) {
        sender.sendMessage(new TextComponent(Utils.color("&eChecking &a" + playerName)));
        if (!punishmentManager.isLoggedServer(playerName)) {
            sender.sendMessage(new TextComponent(Utils.color("&cPlayer not found!")));
            return;
        }

        String ip = punishmentManager.getOfflineIp(playerName);
        Punishment ban = punishmentManager.getBan(playerName);
        Punishment mute = punishmentManager.getMute(playerName);
        ProxiedPlayer player = plugin.getProxy().getPlayer(playerName);

        String uuid = ((player != null && player.isConnected()) ? player.getUniqueId().toString() : playerName);
        String banStatus = ((ban != null && ban.playerIsBanned() && ban.isStillPunished()) ? ban.getDuration() : "&anot banned");
        String muteStatus = ((mute != null && mute.playerIsMuted() && mute.isStillPunished()) ? mute.getDuration() : "&anot muted");
        //Kalan süreler çok uzun olduğunda bir yerden sonrasını kırp
        sender.sendMessage(new TextComponent(Utils.color("&eUUID: &a" + uuid)));
        sender.sendMessage(new TextComponent(Utils.color("&eIP: &a" + (ip != null ? ip : "&cnot found"))));
        sender.sendMessage(new TextComponent(Utils.color("&eBan status: &c" + banStatus)));
        sender.sendMessage(new TextComponent(Utils.color("&eMute status: &c" + muteStatus)));
    }
}
