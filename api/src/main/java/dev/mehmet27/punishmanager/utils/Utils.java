package dev.mehmet27.punishmanager.utils;

import co.aikar.commands.InvalidCommandArgument;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.ConfigManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.objects.PunishmentRevoke;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final ConfigManager configManager = PunishManager.getInstance().getConfigManager();
    public static final char COLOR_CHAR = '§';
    public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";
    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-ORX]");

    private static final String IPV4 = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
    public static final Pattern IPV4_PATTERN = Pattern.compile(IPV4);

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

    public static String replacePunishmentRevokePlaceholders(String message, PunishmentRevoke punishmentRevoke) {
        return message.replace("%reason%", punishmentRevoke.getReason())
                .replace("%operator%", punishmentRevoke.getOperator())
                .replace("%player%", punishmentRevoke.getPlayerName())
                .replace("%type%", punishmentRevoke.getRevokeType().name())
                .replace("%uuid%", punishmentRevoke.getUuid().toString())
                .replace("%id%", String.valueOf(punishmentRevoke.getId()))
                .replace("%time%", String.valueOf(punishmentRevoke.getTime() / 1000));
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

    public static String getLayout(@NotNull Punishment punishment) {
        String path = punishment.getPunishType().toString().toLowerCase(Locale.ENGLISH) + ".layout";
        return TextComponentBuilder(PunishManager.getInstance().getConfigManager().getStringList(path, punishment.getUuid()), punishment);
    }

    public static long convertToMillis(String time) {
        Pattern YEARS = Pattern.compile("([0-9]+y)", Pattern.CASE_INSENSITIVE);
        Pattern MONTHS = Pattern.compile("([0-9]+mo)", Pattern.CASE_INSENSITIVE);
        Pattern WEEKS = Pattern.compile("([0-9]+w)", Pattern.CASE_INSENSITIVE);
        Pattern DAYS = Pattern.compile("([0-9]+d)", Pattern.CASE_INSENSITIVE);
        Pattern HOURS = Pattern.compile("([0-9]+h)", Pattern.CASE_INSENSITIVE);
        Pattern MINUTES = Pattern.compile("([0-9]+m)", Pattern.CASE_INSENSITIVE);
        Pattern SECONDS = Pattern.compile("([0-9]+s)", Pattern.CASE_INSENSITIVE);

        Matcher yearsMatcher = YEARS.matcher(time);
        Matcher monthsMatcher = MONTHS.matcher(time);
        Matcher weeksMatcher = WEEKS.matcher(time);
        Matcher daysMatcher = DAYS.matcher(time);
        Matcher hoursMatcher = HOURS.matcher(time);
        Matcher minutesMatcher = MINUTES.matcher(time);
        Matcher secondsMatcher = SECONDS.matcher(time);

        boolean foundYears = yearsMatcher.find();
        boolean foundMonths = monthsMatcher.find();
        boolean foundWeeks = weeksMatcher.find();
        boolean foundDays = daysMatcher.find();
        boolean foundHours = hoursMatcher.find();
        boolean foundMinutes = minutesMatcher.find();
        boolean foundSeconds = secondsMatcher.find();

        if (!foundYears && !foundMonths && !foundWeeks && !foundDays && !foundHours && !foundMinutes && !foundSeconds) {
            throw new InvalidCommandArgument();
        }

        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        if (foundYears) {
            years = Integer.parseInt(yearsMatcher.group(1).substring(0, yearsMatcher.group(1).length() - 1));
        }
        if (foundMonths) {
            months = Integer.parseInt(monthsMatcher.group(1).substring(0, monthsMatcher.group(1).length() - 2));
        }
        if (foundWeeks) {
            weeks = Integer.parseInt(weeksMatcher.group(1).substring(0, weeksMatcher.group(1).length() - 1));
        }
        if (foundDays) {
            days = Integer.parseInt(daysMatcher.group(1).substring(0, daysMatcher.group(1).length() - 1));
        }
        if (foundHours) {
            hours = Integer.parseInt(hoursMatcher.group(1).substring(0, hoursMatcher.group(1).length() - 1));
        }
        // fixme: it conflicts with month regex
        if (foundMinutes) {
            minutes = Integer.parseInt(minutesMatcher.group(1).substring(0, minutesMatcher.group(1).length() - 1));
        }
        if (foundSeconds) {
            seconds = Integer.parseInt(secondsMatcher.group(1).substring(0, secondsMatcher.group(1).length() - 1));
        }
        return (years * 31536000000L) + (months * 2592000000L) + (weeks * 604800000L) + (days * 86400000L) + (hours * 3600000L) + (minutes * 60000L) + (seconds * 1000L);
    }

    public static String getValueFromUrlJson(String ip) {
        String value = "error";
        if (ip.equals("127.0.0.1")) return value;
        String url = configManager.getConfig().getString("apis.countryApi.url",
                "https://ip-api.com/json/%ip%?fields=country").replaceAll("%ip%", ip);
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
