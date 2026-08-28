package ru.mr_hares.dualmode;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.mr_hares.dualmode.commands.*;
import ru.mr_hares.dualmode.listener.*;
import ru.mr_hares.dualmode.utils.DataBase;
import ru.mr_hares.dualmode.utils.LocaleManager;
import ru.mr_hares.dualmode.utils.ModePlaceholder;
import ru.mr_hares.dualmode.utils.ModrinthUpdate;

public final class DualMode extends JavaPlugin {
    private static DualMode instance;
    private static LocaleManager messages;
    private static DataBase db;
    public static Cache<UUID, UUID> combat_list = CacheBuilder.newBuilder().expireAfterWrite(25, TimeUnit.SECONDS).build();
    
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        messages = new LocaleManager(this);

        console(colorize("\n  ██████╗ ██╗   ██╗ █████╗ ██╗     ███╗   ███╗ ██████╗ ██████╗ ███████╗\n" +
                        "  ██╔══██╗██║   ██║██╔══██╗██║     ████╗ ████║██╔═══██╗██╔══██╗██╔════╝\n" +
                        "  ██║  ██║██║   ██║███████║██║     ██╔████╔██║██║   ██║██║  ██║█████╗  \n" +
                        "  ██║  ██║██║   ██║██╔══██║██║     ██║╚██╔╝██║██║   ██║██║  ██║██╔══╝  \n" +
                        "  ██████╔╝╚██████╔╝██║  ██║███████╗██║ ╚═╝ ██║╚██████╔╝██████╔╝███████╗\n" +
                        "  ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚══════╝╚═╝     ╚═╝ ╚═════╝ ╚═════╝ ╚══════╝\n&f&r"));

        try {
            new ModrinthUpdate("dualmode", Bukkit.getServer().getName())
                    .checkVersion(version -> {
                        if (version.equals("error")) {
                            console("[!] (DualMode) Error checking the new version");
                        } else if (getDescription().getVersion().equals(version)) {
                            console("(DualMode) The latest supported version is used.");
                        } else {
                            console("[!] (DualMode) A new version " + version + " has been detected (You have " + getDescription().getVersion() + ")");
                            console("[!] (DualMode) https://modrinth.com/project/dualmode/versions");
                        }
                    });
        } catch (Exception e) {
            console("[!] (DualMode) Error checking the new version");
        }

        try {
            db = new DataBase(this);
            console("(DualMode) Load data from database");
        } catch (SQLException e) {
            console("[!] (DualMode) Database loading error");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        registerEvents(this,
                new InventoryClick(),
                new InventoryClose(),
                new PlayerJoin(),
                new PlayerDamage()
        );

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ModePlaceholder(this).register();
            console("(DualMode) Loaded integration: PlaceholderAPI");
        } else {
            console("[!] (DualMode) Not loaded integration: PlaceholderAPI");
        }

        new Mode();
        new ru.mr_hares.dualmode.commands.DualMode();
    }

    private void registerEvents(Plugin plugin, Listener... listeners) {
        PluginManager pm = Bukkit.getPluginManager();
        for (Listener listener : listeners) {
            pm.registerEvents(listener, plugin);
        }

        List<String> events = Arrays.stream(listeners).map(listener -> listener.getClass().getSimpleName()).toList();
        console("(DualMode) Loaded events: " + String.join(", ", events));
    }

    @Override
    public void onDisable() {
        console(colorize("\n  ██████╗ ██╗   ██╗ █████╗ ██╗     ███╗   ███╗ ██████╗ ██████╗ ███████╗\n" +
                        "  ██╔══██╗██║   ██║██╔══██╗██║     ████╗ ████║██╔═══██╗██╔══██╗██╔════╝\n" +
                        "  ██║  ██║██║   ██║███████║██║     ██╔████╔██║██║   ██║██║  ██║█████╗  \n" +
                        "  ██║  ██║██║   ██║██╔══██║██║     ██║╚██╔╝██║██║   ██║██║  ██║██╔══╝  \n" +
                        "  ██████╔╝╚██████╔╝██║  ██║███████╗██║ ╚═╝ ██║╚██████╔╝██████╔╝███████╗\n" +
                        "  ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚══════╝╚═╝     ╚═╝ ╚═════╝ ╚═════╝ ╚══════╝\n&f&r"));
    }

    public static DualMode getInstance() { return instance; }
    public static LocaleManager getMessages() { return messages; }
    public static DataBase getDB() { return db; }

    public static String colorize(String msg) {
        Matcher match = Pattern.compile("#[a-fA-F0-9]{6}").matcher(msg);
        while (match.find()) {
            String color = msg.substring(match.start(), match.end());
            msg = msg.replace(color, String.valueOf(ChatColor.of(color)));
            match = Pattern.compile("#[a-fA-F0-9]{6}").matcher(msg);
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static void console(String message) { Bukkit.getConsoleSender().sendMessage(message); }
}
