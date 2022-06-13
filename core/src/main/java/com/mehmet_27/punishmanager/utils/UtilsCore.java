package com.mehmet_27.punishmanager.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mehmet_27.punishmanager.MethodInterface;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class UtilsCore {
    public static final Pattern NumberAndUnit = Pattern.compile("(?<number>[0-9]+)(?<unit>mo|[ywdhms])");
    private static final ConfigManager configManager = PunishManager.getInstance().getConfigManager();
    private static final MethodInterface methods = PunishManager.getInstance().getMethods();

    public static String replacePunishmentPlaceholders(String message, Punishment punishment) {
        return message.replace("%reason%", punishment.getReason())
                .replace("%duration%", punishment.getDuration())
                .replace("%operator%", punishment.getOperator())
                .replace("%server%", punishment.getServer())
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

    public static String getValueFromUrlJson(String ip) {
        String value = "error";
        String url = configManager.getConfig().getString("apis.countryApi.url",
                "http://ip-api.com/json/%ip%?fields=country").replaceAll("%ip%", ip);
        String key = configManager.getConfig().getString("apis.countryApi.key", "country");
        try {
            String string = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
            JsonObject json = new Gson().fromJson(string, JsonObject.class);
            if (json != null) {
                JsonElement element = json.get(key);
                if (element != null) {
                    value = element.toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
}
