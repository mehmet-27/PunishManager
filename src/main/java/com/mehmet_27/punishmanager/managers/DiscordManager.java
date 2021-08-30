package com.mehmet_27.punishmanager.managers;

import com.google.gson.Gson;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.events.DiscordBotReady;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.md_5.bungee.config.Configuration;

import javax.security.auth.login.LoginException;
import java.util.Map;

public class DiscordManager {
    private final PunishManager plugin;
    private final Map<String, String> embeds;
    private final Configuration config;
    private final DatabaseManager dataBaseManager;
    private JDA api;
    private Guild guild;
    private TextChannel announceChannel;

    public DiscordManager(PunishManager plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getConfig();
        this.embeds = plugin.getConfigManager().getEmbeds();
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

    public void givePunishedRole(Punishment punishment) {
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
        guild.addRoleToMember(member, role).queue();
        Utils.debug("Added the " + role.getName() + " role to " + punishment.getPlayerName() + " in Discord.");
    }

    public void removePunishedRole(Punishment punishment) {
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
        guild.removeRoleFromMember(member, role).queue();
        Utils.debug("Removed the " + role.getName() + " role to " + punishment.getPlayerName() + " in Discord.");
    }

    public void sendEmbed(Punishment punishment) {
        Gson gson = new Gson();
        MessageEmbed embed = gson.fromJson(embeds.get(punishment.getPunishType().name())
                .replace("%player%", punishment.getPlayerName())
                .replace("%operator%", punishment.getOperator())
                .replace("%reason%", punishment.getReason())
                .replace("%duration%", punishment.getDuration()), MessageEmbed.class);
        announceChannel.sendMessageEmbeds(embed).queue();
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
