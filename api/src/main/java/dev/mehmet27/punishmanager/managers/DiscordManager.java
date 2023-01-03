package dev.mehmet27.punishmanager.managers;

import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.configuration.Configuration;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.objects.PunishmentRevoke;
import dev.mehmet27.punishmanager.utils.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;

import java.util.Locale;

public class DiscordManager {
    private final PunishManager punishManager = PunishManager.getInstance();
    private final ConfigManager configManager = punishManager.getConfigManager();
    private final Configuration config = configManager.getConfig();
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
        api = JDABuilder.createDefault(config.getString("discord.token"))
                .addEventListeners(new DiscordBotReadyEvent())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setChunkingFilter(ChunkingFilter.ALL)
                .build();
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
        if (!(config.getBoolean("discord.punish-announce.enable", true) && config.getBoolean(path, true))) {
            return;
        }
        String json = Utils.replacePunishmentPlaceholders(configManager.getEmbed(punishment.getPunishType().name()), punishment);
        announceChannel.sendMessageEmbeds(((JDAImpl) api).getEntityBuilder().createMessageEmbed(DataObject.fromJson(json))).queue();
    }

    public void sendRevokeEmbed(PunishmentRevoke punishmentRevoke) {
        if (!isEnabledInConfig) return;
        String path = "discord.punish-announce.embeds." + punishmentRevoke.getRevokeType().name().toLowerCase(Locale.ENGLISH);
        if (!(config.getBoolean("discord.punish-announce.enable", true) && config.getBoolean(path, true))) {
            return;
        }
        String json = Utils.replacePunishmentRevokePlaceholders(configManager.getEmbed(punishmentRevoke.getRevokeType().name()), punishmentRevoke);
        announceChannel.sendMessageEmbeds(((JDAImpl) api).getEntityBuilder().createMessageEmbed(DataObject.fromJson(json))).queue();
    }

    public void setAnnounceChannel(TextChannel channel) {
        this.announceChannel = channel;
    }

    public JDA getApi() {
        return api;
    }
}
