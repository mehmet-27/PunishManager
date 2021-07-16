package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PunishmentManager {
    private Connection connection = com.mehmet_27.punishmanager.PunishManager.getInstance().getConnection();

    public void BanPlayer(ProxiedPlayer player, String reason, String operator){
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT IGNORE INTO `punishmanager_punishments` (`name`, `uuid`, `reason`, `operator`, `type`) VALUES (?,?,?,?,?)");
            ps.setString(1, player.getName());
            ps.setString(2, player.getUniqueId().toString());
            ps.setString(3, reason);
            ps.setString(4, operator);
            ps.setString(5, "ban");
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void UnBanPlayer(String wantedPlayer){
        try {
            ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(wantedPlayer);
            UUID uuid;
            PreparedStatement ps;
            if (player != null && player.isConnected()){
                uuid = player.getUniqueId();
                ps = connection.prepareStatement("DELETE FROM `punishmanager_punishments` WHERE uuid = ?");
                ps.setString(1, uuid.toString());
            }else{
                ps = connection.prepareStatement("DELETE FROM `punishmanager_punishments` WHERE name = ?");
                ps.setString(1, wantedPlayer);
            }
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public boolean PlayerIsBanned(String wantedPlayer) {
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(wantedPlayer);
        UUID uuid;
        try {
            PreparedStatement ps;
            if (player != null && player.isConnected()){
                uuid = player.getUniqueId();
                ps = connection.prepareStatement("SELECT * FROM `punishmanager_punishments` WHERE `uuid` = ?");
                ps.setString(1, uuid.toString());
            }else{
                ps = connection.prepareStatement("SELECT * FROM `punishmanager_punishments` WHERE `name` = ?");
                ps.setString(1, wantedPlayer);
            }
            ResultSet result = ps.executeQuery();
            if (result.next()){
                if (result.getString("type").equals("ban")){
                    return true;
                }
                return false;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public String getReason(ProxiedPlayer player){
        UUID uuid = player.getUniqueId();
        String reason = null;
        if (!PlayerIsBanned(player.getName())){
            return null;
        }
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `punishmanager_punishments` WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet result = ps.executeQuery();
            if (result.next()){
                if (result.getString("type").equals("ban")){
                    reason = result.getString("reason");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return reason;
    }
}
