package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.MethodProvider;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.configuration.Configuration;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DiscordBotReadyEvent extends ListenerAdapter {

    private final PunishManager punishManager = PunishManager.getInstance();
    private final MethodProvider methods = punishManager.getMethods();
    private final Configuration config = punishManager.getConfigManager().getConfig();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        methods.getLogger().info("Bot connected to discord with name " + event.getJDA().getSelfUser().getName());
        DiscordManager discordManager = punishManager.getDiscordManager();
        discordManager.setAnnounceChannel(event.getJDA().getTextChannelById(config.getString("discord.punish-announce.channel-id")));
    }
}
