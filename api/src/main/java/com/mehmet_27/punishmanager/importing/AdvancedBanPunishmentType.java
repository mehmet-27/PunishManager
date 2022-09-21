package com.mehmet_27.punishmanager.importing;

import com.mehmet_27.punishmanager.objects.Punishment;

public enum AdvancedBanPunishmentType {
    BAN(Punishment.PunishType.BAN),
    TEMP_BAN(Punishment.PunishType.TEMPBAN),
    IP_BAN(Punishment.PunishType.IPBAN),
    MUTE(Punishment.PunishType.MUTE),
    TEMP_MUTE(Punishment.PunishType.TEMPMUTE),
    KICK(Punishment.PunishType.KICK);

    private final Punishment.PunishType type;

    AdvancedBanPunishmentType(Punishment.PunishType type) {
        this.type = type;
    }

    public Punishment.PunishType getType() {
        return type;
    }
}
