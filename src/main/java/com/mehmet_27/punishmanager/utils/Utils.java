package com.mehmet_27.punishmanager.utils;

import net.md_5.bungee.api.ChatColor;

public class Utils {
    public static String color(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
