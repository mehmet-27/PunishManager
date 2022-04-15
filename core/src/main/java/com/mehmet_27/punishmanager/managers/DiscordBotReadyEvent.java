package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.ConfigurationAdapter;
import com.mehmet_27.punishmanager.MethodInterface;
import com.mehmet_27.punishmanager.PunishManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public class DiscordBotReadyEvent implements EventListener {

    private final MethodInterface methods = PunishManager.getInstance().getMethods();
    private final ConfigurationAdapter config = methods.getConfig();
    DiscordManager discordManager = PunishManager.getInstance().getDiscordManager();

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof ReadyEvent) {
            methods.getLogger().info("Bot connected to discord with name " + event.getJDA().getSelfUser().getName());
            JDA api = event.getJDA();
            discordManager.setApi(api);
            discordManager.setGuild(api.getGuildById(config.getString("discord.serverId")));
            discordManager.setAnnounceChannel(api.getTextChannelById(config.getString("discord.punish-announce.channel-id")));
        }
    }
}
