package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PunishmentManager {
    private Connection connection = com.mehmet_27.punishmanager.PunishManager.getInstance().getConnection();

    public void BanPlayer(Punishment punishment) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT IGNORE INTO `punishmanager_punishments` (`name`, `uuid`, `reason`, `operator`, `type`) VALUES (?,?,?,?,?)");
            ps.setString(1, punishment.getPlayerName());
            ps.setString(2, punishment.getUuid());
            ps.setString(3, punishment.getReason());
            ps.setString(4, punishment.getOperator());
            ps.setString(5, punishment.getPunishType().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UnBanPlayer(String wantedPlayer) {
        try {
            ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(wantedPlayer);
            UUID uuid;
            PreparedStatement ps;
            if (player != null && player.isConnected()) {
                uuid = player.getUniqueId();
                ps = connection.prepareStatement("DELETE FROM `punishmanager_punishments` WHERE uuid = ?");
                ps.setString(1, uuid.toString());
            } else {
                ps = connection.prepareStatement("DELETE FROM `punishmanager_punishments` WHERE name = ?");
                ps.setString(1, wantedPlayer);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean PlayerIsBanned(String wantedPlayer) {
        try {
            PreparedStatement ps;
            ps = connection.prepareStatement("SELECT * FROM `punishmanager_punishments` WHERE `name` = ?");
            ps.setString(1, wantedPlayer);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                if (result.getString("type").equals("BAN") ||
                        result.getString("type").equals("IPBAN") ||
                        result.getString("type").equals("TEMPBAN")) {
                    return true;
                }
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getPunishType(String wantedPlayer) {
        if (PlayerIsBanned(wantedPlayer)) {
            try {
                PreparedStatement ps;
                ps = connection.prepareStatement("SELECT * FROM `punishmanager_punishments` WHERE `name` = ?");
                ps.setString(1, wantedPlayer);
                ResultSet result = ps.executeQuery();
                return result.getString("type");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getReason(ProxiedPlayer player) {
        UUID uuid = player.getUniqueId();
        String reason = null;
        if (!PlayerIsBanned(player.getName())) {
            return null;
        }
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `punishmanager_punishments` WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                reason = result.getString("reason");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reason;
    }

    public String getOperator(ProxiedPlayer player) {
        UUID uuid = player.getUniqueId();
        String operator = null;
        if (!PlayerIsBanned(player.getName())) {
            return null;
        }
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `punishmanager_punishments` WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                operator = result.getString("operator");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return operator;
    }

    public String getType(ProxiedPlayer player) {
        UUID uuid = player.getUniqueId();
        String type = null;
        if (!PlayerIsBanned(player.getName())) {
            return null;
        }
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `punishmanager_punishments` WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                type = result.getString("type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return type;
    }

    public Punishment getPunishment(String wantedPlayer) {
        if (!PlayerIsBanned(wantedPlayer)) {
            return null;
        }
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `punishmanager_punishments` WHERE name = ?");
            ps.setString(1, wantedPlayer);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                String playerName = result.getString("name");
                String uuid = result.getString("uuid");
                String reason = result.getString("reason");
                String operator = result.getString("operator");
                Punishment.PunishType punishType = Punishment.PunishType.valueOf(result.getString("type"));
                return new Punishment(playerName, uuid, punishType, operator, reason);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
