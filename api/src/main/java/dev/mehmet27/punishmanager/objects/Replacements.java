package dev.mehmet27.punishmanager.objects;

public enum Replacements {
    BAN("ban", "ban.name"),
    TEMPBAN("tempban", "tempban.name"),
    IPBAN("ipban", "ipban.name"),
    MUTE("mute", "mute.name"),
    WARN("warn", "warn.name"),
    TEMPMUTE("tempmute", "tempmute.name"),
    KICK("kick", "kick.name"),
    UNBAN("unban", "unban.name"),
    UNMUTE("unmute", "unmute.name"),
    UNWARN("unwarn", "unwarn.name"),
    UNPUNISH("unpunish", "unpunish.name"),
    CLEARWARNS("clearwarns", "clearwarns.name"),
    PUNISH("punish", "punish.name"),
    CHECK("check", "check.name"),
    WARNS("warns", "warns.name"),
    CHANGEREASON("changereason", "changereason.name"),
    ADMIN("admin", "punishmanager.admin.name"),
    RELOAD("reload", "punishmanager.admin.reload.name"),
    IMPORT("import", "punishmanager.admin.import.name"),
    HELP("help", "punishmanager.help.name"),
    ABOUT("about", "punishmanager.about.name");

    private final String def, path;

    Replacements(String def, String path) {
        this.def = def;
        this.path = path;
    }

    public String getDef() {
        return def;
    }

    public String getPath() {
        return path;
    }
}
