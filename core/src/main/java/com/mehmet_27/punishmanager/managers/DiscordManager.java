package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.ConfigurationAdapter;
import com.mehmet_27.punishmanager.MethodInterface;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.UtilsCore;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;

import javax.security.auth.login.LoginException;
import java.util.Locale;

public class DiscordManager {
    private final PunishManager punishManager = PunishManager.getInstance();
    private final MethodInterface methods = punishManager.getMethods();
    private final ConfigManager configManager = methods.getConfigManager();
    private final ConfigurationAdapter config = configManager.getConfig();
    private final boolean isEnabledInConfig;
    private JDA api;
    private TextChannel announceChannel;

    public DiscordManager() {
        this.isEnabledInConfig = config.getBoolean("discord.enable", false);
        if (isEnabledInConfig) {
            buildBot();
        }
    }

    private void buildBot() {
        try {
            api = JDABuilder.createDefault(config.getString("discord.token"))
                    .addEventListeners(new DiscordBotReadyEvent())
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .build();
        } catch (LoginException e) {
            methods.getLogger().severe("Discord bot failed to connect!");
            e.printStackTrace();
        }
    }

    public void disconnectBot() {
        if (!isEnabledInConfig) return;
        if (api != null) {
            api.shutdown();
            api = null;
        }
    }

    public void sendEmbed(Punishment punishment) {
        if (!isEnabledInConfig) return;
        String path = "discord.punish-announce.embeds." + punishment.getPunishType().name().toLowerCase(Locale.ENGLISH);
        if (!(config.getBoolean("discord.punish-announce.enable") && config.getBoolean(path))) {
            return;
        }
        String json = UtilsCore.replacePunishmentPlaceholders(configManager.getEmbed(punishment.getPunishType().name()), punishment);
        announceChannel.sendMessageEmbeds(((JDAImpl) api).getEntityBuilder().createMessageEmbed(DataObject.fromJson(json))).queue();
    }

    public void setAnnounceChannel(TextChannel channel) {
        this.announceChannel = channel;
    }

    public JDA getApi() {
        return api;
    }
}
