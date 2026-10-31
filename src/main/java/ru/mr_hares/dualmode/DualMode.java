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
import ru.mr_hares.dualmode.database.DataBase;
import ru.mr_hares.dualmode.utils.MessagesLoader;
import ru.mr_hares.dualmode.utils.ModePlaceholder;

public final class DualMode extends JavaPlugin {
    private static DualMode instance;
    private static MessagesLoader messages;
    private static DataBase db;
    public static Cache<UUID, UUID> combat_list = CacheBuilder.newBuilder().expireAfterWrite(25, TimeUnit.SECONDS).build();
    
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        messages = new MessagesLoader(this);

        try {
            db = new DataBase(this);
            getLogger().info("Load data from database");
        } catch (SQLException e) {
            getLogger().severe("Database loading error");
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
            getLogger().info("Loaded integration: PlaceholderAPI");
        } else {
            getLogger().warning("Not loaded integration: PlaceholderAPI");
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
        getLogger().info("Loaded events: " + String.join(", ", events));
    }

    @Override
    public void onDisable() {

    }

    public static DualMode getInstance() { return instance; }
    public static MessagesLoader getMessages() { return messages; }
    public static DataBase getDB() { return db; }

    public static void console(String message) { Bukkit.getConsoleSender().sendMessage(message); }
}
