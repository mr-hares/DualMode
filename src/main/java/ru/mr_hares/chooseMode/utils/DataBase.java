package ru.mr_hares.chooseMode.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class DataBase {
    private Connection connection;
    private static Plugin plugin;
    private static final DateTimeFormatter DEFAULT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DataBase(Plugin plugin) throws SQLException {
        this.plugin = plugin;

        connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() +
                "/database.db");

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(String.format("CREATE TABLE IF NOT EXISTS %s (" +
                    "uuid TEXT NOT NULL," +
                    "mode TEXT NOT NULL," +
                    "next_edit TEXT NOT NULL)", "user_info"));
        }
    }

    public void addPlayer(UUID uuid, String mode) {
        String sql = "INSERT INTO user_info (uuid, mode, next_edit) VALUES (?,?,?)";

        LocalDateTime currentDate = LocalDateTime.now();
        currentDate = currentDate.plusDays(plugin.getConfig().getInt("choose-time"));
        String dateString = DEFAULT_FORMATTER.format(currentDate);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, mode);
            stmt.setString(3, dateString);

            stmt.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(e.toString());
        }
    }

    public void setMode(UUID uuid, String mode) {
        String sql = "UPDATE user_info SET mode = ? WHERE uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(2, uuid.toString());
            stmt.setString(1, mode);

            stmt.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(e.toString());
        }

        String sql1 = "UPDATE user_info SET next_edit = ? WHERE uuid = ?";

        LocalDateTime currentDate = LocalDateTime.now();
        currentDate = currentDate.plusDays(plugin.getConfig().getInt("choose-time"));
        String dateString = DEFAULT_FORMATTER.format(currentDate);

        try (PreparedStatement stmt = connection.prepareStatement(sql1)) {
            stmt.setString(2, uuid.toString());
            stmt.setString(1, dateString);

            stmt.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(e.toString());
        }
    }

    public String getMode(UUID uuid) {
        String sql = "SELECT * FROM user_info WHERE uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("mode");
            }
        } catch (SQLException ignored) {}

        return null;
    }

    public String getTime(UUID uuid) {
        String sql = "SELECT * FROM user_info WHERE uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return getRemainingTime(rs.getString("next_edit"));
            }
        } catch (SQLException ignored) {}

        return null;
    }

    public List<String[]> getList() {
        List<String[]> list = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user_info")) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new String[]{rs.getString("uuid"), rs.getString("mode")});
            }
        } catch (SQLException ignored) {}

        return list;
    }

    public static String getRemainingTime(String dateString) {
        LocalDateTime targetDate = LocalDateTime.parse(dateString, DEFAULT_FORMATTER);
        LocalDateTime now = LocalDateTime.now();

        if (targetDate.isBefore(now)) {
            return null;
        }

        long days = ChronoUnit.DAYS.between(now, targetDate);
        long hours = ChronoUnit.HOURS.between(now, targetDate) % 24;
        long minutes = ChronoUnit.MINUTES.between(now, targetDate) % 60;
        long seconds = ChronoUnit.SECONDS.between(now, targetDate) % 60;

        if (days > 0) return days + plugin.getConfig().getString("time.days");
        if (hours > 0) return days + plugin.getConfig().getString("time.hours");
        if (minutes > 0) return days + plugin.getConfig().getString("time.minutes");

        return seconds + plugin.getConfig().getString("time.minutes");
    }
}