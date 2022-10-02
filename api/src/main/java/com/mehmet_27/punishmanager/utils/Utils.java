package com.mehmet_27.punishmanager.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Utils {
    public static final Pattern NumberAndUnit = Pattern.compile("(?<number>[0-9]+)(?<unit>mo|[ywdhms])");
    private static final ConfigManager configManager = PunishManager.getInstance().getConfigManager();
    public static final char COLOR_CHAR = '\u00A7';
    public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";
    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-ORX]");

    public static String replacePunishmentPlaceholders(String message, Punishment punishment) {
        return message.replace("%reason%", punishment.getReason())
                .replace("%duration%", punishment.getDuration(punishment.getOperatorUUID()))
                .replace("%operator%", punishment.getOperator())
                .replace("%server%", punishment.getServer())
                .replace("%player%", punishment.getPlayerName())
                .replace("%type%", punishment.getPunishType().name())
                .replace("%ip%", "" + punishment.getIp())
                .replace("%uuid%", punishment.getUuid().toString())
                .replace("%id%", String.valueOf(punishment.getId()))
                .replace("%start%", String.valueOf(punishment.getStart() / 1000))
                .replace("%end%", String.valueOf(punishment.getEnd() / 1000))
                .replace("%startMillis%", String.valueOf(punishment.getStart()))
                .replace("%endMillis%", String.valueOf(punishment.getEnd()));
    }

    public static void sendText(@Nullable UUID sender, String path) {
        OfflinePlayer player = PunishManager.getInstance().getOfflinePlayers().get(sender);
        sendText(sender, path, message -> message.replace("%player%", player != null ? player.getName() : "CONSOLE"));
    }

    public static void sendText(@Nullable UUID sender, String playerName, String path) {
        sendText(sender, path, message -> message.replace("%player%", playerName));
    }

    public static void sendText(@Nullable UUID sender, String path, Function<String, String> placeholders) {
        String message = PunishManager.getInstance().getConfigManager().getMessage(path, sender);

        message = placeholders.apply(message);

        PunishManager.getInstance().getMethods().sendMessage(sender, message);
    }

    public static String TextComponentBuilder(List<String> messages, Punishment punishment) {
        String layout = "";
        for (String message : messages) {
            message = message.replace("%prefix%", PunishManager.getInstance().getConfigManager().getMessage("main.prefix", punishment.getUuid()));
            // Replace general punishment placeholders
            message = Utils.replacePunishmentPlaceholders(message, punishment);
            layout = layout.concat(message + "\n");
        }
        return layout;
    }

    public static String getLayout(Punishment punishment) {
        String path = punishment.getPunishType().toString().toLowerCase(Locale.ENGLISH) + ".layout";
        return TextComponentBuilder(PunishManager.getInstance().getConfigManager().getStringList(path, punishment.getUuid()), punishment);
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
        if (ip.equals("127.0.0.1")) return value;
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

    public static Locale stringToLocale(String loc) {
        String[] localeStr = loc.split("_");
        return new Locale(localeStr[0], localeStr[1]);
    }

    public static String color(String message) {
        return translateAlternateColorCodes('&', message);
    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && ALL_CODES.indexOf(b[i + 1]) > -1) {
                b[i] = COLOR_CHAR;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static String stripColor(final String input) {
        if (input == null) {
            return null;
        }

        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
