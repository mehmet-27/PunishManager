package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.listeners.DiscordBotReady;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.md_5.bungee.config.Configuration;

import javax.security.auth.login.LoginException;
import java.util.Locale;

import static com.mehmet_27.punishmanager.managers.DiscordAction.ADD;
import static com.mehmet_27.punishmanager.utils.Utils.debug;

public class DiscordManager {
    private final PunishManager plugin;
    private final ConfigManager configManager;
    private final Configuration config;
    private final DatabaseManager dataBaseManager;
    private JDA api;
    private Guild guild;
    private TextChannel announceChannel;

    public DiscordManager(PunishManager plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.config = configManager.getConfig();
        this.dataBaseManager = plugin.getDataBaseManager();
    }

    public void buildBot() {
        //Return if feature is disabled.
        if (!config.getBoolean("discord.enable")) return;
        if (dataBaseManager.getSource() == null) {
            plugin.getLogger().severe("Discord feature will not work because DiscordSRV could not connect to database.");
        }
        try {
            api = JDABuilder.createDefault(config.getString("discord.token"))
                    .addEventListeners(new DiscordBotReady())
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .build();
        } catch (LoginException e) {
            plugin.getLogger().severe("Discord bot failed to connect!");
            e.printStackTrace();
        }
    }

    public void disconnectBot() {
        if (api != null) {
            api.shutdown();
            api = null;
        }
    }

    public void updateRole(Punishment punishment, DiscordAction action) {
        if (!(config.getBoolean("discord.enable") && config.getBoolean("discord.punish-role.enable"))) return;

        Role role = guild.getRoleById(config.getString("discord.punish-role.punishedRoleId"));
        if (role == null) {
            plugin.getLogger().severe("Discord role not found!");
            return;
        }

        Member member = guild.getMemberById(dataBaseManager.getUserDiscordId(punishment.getUuid()));
        if (member == null) {
            plugin.getLogger().severe("Discord member not found!");
            return;
        }

        if (action == ADD) {
            guild.addRoleToMember(member, role).queue();
        } else {
            guild.removeRoleFromMember(member, role).queue();
        }

        debug(String.format("[%s] %s role - %s", action.name(), role.getName(), punishment.getPlayerName()));
    }

    public void sendEmbed(Punishment punishment) {
        String path = "discord.punish-announce.embeds." + punishment.getPunishType().name().toLowerCase(Locale.ENGLISH);
        if (!(configManager.getConfig().getBoolean("discord.punish-announce.enable") &&configManager.getConfig().getBoolean(path)) ){
            return;
        }
            String json = configManager.getEmbed(punishment.getPunishType().name())
                .replace("%player%", punishment.getPlayerName())
                .replace("%operator%", punishment.getOperator())
                .replace("%reason%", punishment.getReason())
                .replace("%duration%", punishment.getDuration());
        announceChannel.sendMessageEmbeds(((JDAImpl) api).getEntityBuilder().createMessageEmbed(DataObject.fromJson(json))).queue();
    }

    public void setApi(JDA api) {
        this.api = api;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public void setAnnounceChannel(TextChannel channel) {
        this.announceChannel = channel;
    }

}
