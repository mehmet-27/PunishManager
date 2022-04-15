package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.ConfigurationAdapter;
import com.mehmet_27.punishmanager.MethodInterface;
import com.mehmet_27.punishmanager.PunishManager;
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

import javax.security.auth.login.LoginException;
import java.util.Locale;

import static com.mehmet_27.punishmanager.utils.UtilsCore.debug;

public class DiscordManager {
    private final MethodInterface methods = PunishManager.getInstance().getMethods();
    private final ConfigManager configManager = methods.getConfigManager();
    private final ConfigurationAdapter config = configManager.getConfig();
    private final StorageManager storageManager;
    private final boolean isEnabledInConfig;
    private JDA api;
    private Guild guild;
    private TextChannel announceChannel;

    public DiscordManager() {
        this.storageManager = PunishManager.getInstance().getStorageManager();
        this.isEnabledInConfig = config.getBoolean("discord.enable", false);
        if (isEnabledInConfig) {
            buildBot();
        }
    }

    private void buildBot() {
        if (!storageManager.isDiscordSRVTableExits()) {
            methods.getLogger().warning("The Punished Role feature will not work because DiscordSRV cannot connect to the database.");
        }
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

    public void updateRole(Punishment punishment, DiscordAction action) {
        if (!(config.getBoolean("discord.enable") && config.getBoolean("discord.punish-role.enable"))) return;
        if (!storageManager.isDiscordSRVTableExits()) return;
        if (api == null) {
            debug(String.format("Could not update role for %s because bot is null.", punishment.getPlayerName()));
            return;
        }
        String id = storageManager.getUserDiscordId(punishment.getUuid());
        if (id == null) {
            debug(String.format("Role action failed because player %s's Discord ID could not be found.", punishment.getPlayerName()));
            return;
        }
        Member member = guild.getMemberById(id);
        if (member == null) return;

        Role role = guild.getRoleById(config.getString("discord.punish-role.punishedRoleId"));
        if (role == null) {
            methods.getLogger().severe("Discord role not found!");
            return;
        }

        if (action == DiscordAction.ADD) {
            guild.addRoleToMember(member, role).queue();
        } else if (action == DiscordAction.REMOVE) {
            guild.removeRoleFromMember(member, role).queue();
        }

        debug(String.format("[%s] %s role - %s", action.name(), role.getName(), punishment.getPlayerName()));
    }

    public void sendEmbed(Punishment punishment) {
        String path = "discord.punish-announce.embeds." + punishment.getPunishType().name().toLowerCase(Locale.ENGLISH);
        if (!(config.getBoolean("discord.punish-announce.enable") && config.getBoolean(path))) {
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

    public enum DiscordAction {
        ADD, REMOVE;
    }
}
