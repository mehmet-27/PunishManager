package com.mehmet_27.punishmanager.velocity.listeners;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.configuration.Configuration;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import com.mehmet_27.punishmanager.velocity.PMVelocity;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.util.List;

public class CommandListener {

    private final Configuration config;
    private final PunishManager punishManager = PunishManager.getInstance();
    private final StorageManager storageManager = punishManager.getStorageManager();

    public CommandListener(PMVelocity plugin) {
        config = plugin.getConfigManager().getConfig();
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerChat(CommandExecuteEvent event) {
        if (!(event.getCommandSource() instanceof Player)) return;
        Player player = (Player) event.getCommandSource();
        Punishment punishment = storageManager.getMute(player.getUniqueId());
        if (punishment == null) return;
        if (punishment.isExpired()) {
            storageManager.unPunishPlayer(punishment);
            return;
        }
        List<String> bannedCommands = config.getStringList("banned-commands-while-mute");
        String command = event.getCommand().split(" ")[0];
        if (bannedCommands.contains(command)) {
            event.setResult(CommandExecuteEvent.CommandResult.denied());
            player.sendMessage(Component.text(Utils.getLayout(punishment)));
        }
    }
}
