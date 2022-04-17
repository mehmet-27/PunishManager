package com.mehmet_27.punishmanager.utils;

import com.mehmet_27.punishmanager.MethodInterface;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.Punishment;

import java.util.regex.Pattern;

public class UtilsCore {
    public static final Pattern NumberAndUnit = Pattern.compile("(?<number>[0-9]+)(?<unit>mo|[ywdhms])");
    private static final MethodInterface methods = PunishManager.getInstance().getMethods();

    public static String replacePunishmentPlaceholders(String message, Punishment punishment){
        return message.replace("%reason%", punishment.getReason())
                .replace("%duration%", punishment.getDuration())
                .replace("%operator%", punishment.getOperator())
                .replace("%player%", punishment.getPlayerName())
                .replace("%type%", punishment.getPunishType().name())
                .replace("%ip%", "" + punishment.getIp())
                .replace("%uuid%", punishment.getUuid().toString());
    }

    public static long convertToMillis(int number, String unit) {
        switch (unit) {
            case "s":
                return (long) number * 1000;
            case "m":
                return (long) number * 1000 * 60;
            case "h":
                return (long) number * 1000 * 60 * 60;
            case "d":
                return (long) number * 1000 * 60 * 60 * 24;
            case "w":
                return (long) number * 1000 * 60 * 60 * 24 * 7;
            case "mo":
                return (long) number * 1000 * 60 * 60 * 24 * 28;
            case "y":
                return (long) number * 1000 * 60 * 60 * 24 * 28 * 12;
            default:
                return -1;
        }
    }
}
