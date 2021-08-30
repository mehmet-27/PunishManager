package com.mehmet_27.punishmanager.events;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.DiscordManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.NotNull;

public class DiscordBotReady implements EventListener {
    Configuration config = PunishManager.getInstance().getConfigManager().getConfig();
    DiscordManager discordManager = PunishManager.getInstance().getDiscordManager();

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof ReadyEvent) {
            PunishManager.getInstance().getLogger().info("Bot connected to discord with name " + event.getJDA().getSelfUser().getName());
            JDA api = event.getJDA();
            discordManager.setApi(api);
            discordManager.setGuild(api.getGuildById(config.getString("discord.serverId")));
            discordManager.setAnnounceChannel(api.getTextChannelById(config.getString("discord.punish-announce.channel-id")));
        }
    }
}
