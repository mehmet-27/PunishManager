package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.ConfigurationAdapter;
import com.mehmet_27.punishmanager.MethodInterface;
import com.mehmet_27.punishmanager.PunishManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DiscordBotReadyEvent extends ListenerAdapter {

    private final PunishManager punishManager = PunishManager.getInstance();
    private final MethodInterface methods = punishManager.getMethods();
    private final ConfigurationAdapter config = methods.getConfig();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        methods.getLogger().info("Bot connected to discord with name " + event.getJDA().getSelfUser().getName());
        DiscordManager discordManager = punishManager.getDiscordManager();
        discordManager.setAnnounceChannel(event.getJDA().getTextChannelById(config.getString("discord.punish-announce.channel-id")));
    }
}
