package com.mehmet_27.punishmanager.objects;

public enum Replacements {
    BAN("ban", "ban.name"),
    TEMPBAN("tempban", "tempban.name"),
    IPBAN("ipban", "ipban.name"),
    MUTE("mute", "mute.name"),
    TEMPMUTE("tempmute", "tempmute.name"),
    KICK("kick", "kick.name"),
    UNBAN("unban", "unban.name"),
    UNMUTE("unmute", "unmute.name"),
    UNPUNISH("unpunish", "unpunish.name"),
    PUNISH("punish", "punish.name"),
    CHECK("check", "check.name"),
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
