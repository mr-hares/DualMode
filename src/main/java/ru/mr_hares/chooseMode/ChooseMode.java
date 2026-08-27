package ru.mr_hares.chooseMode;

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

import ru.mr_hares.chooseMode.commands.*;
import ru.mr_hares.chooseMode.listener.*;
import ru.mr_hares.chooseMode.utils.DataBase;
import ru.mr_hares.chooseMode.utils.LocaleManager;
import ru.mr_hares.chooseMode.utils.ModePlaceholder;
import ru.mr_hares.chooseMode.utils.ModrinthUpdate;

public final class ChooseMode extends JavaPlugin {
    private static ChooseMode instance;
    private static LocaleManager messages;
    private static DataBase db;
    public static Cache<UUID, UUID> combat_list = CacheBuilder.newBuilder().expireAfterWrite(25, TimeUnit.SECONDS).build();
    
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        messages = new LocaleManager(this);

        console(color("\n   ██████╗██╗  ██╗ ██████╗  ██████╗ ███████╗███████╗\n" +
                      "  ██╔════╝██║  ██║██╔═══██╗██╔═══██╗██╔════╝██╔════╝\n" +
                      "  ██║     ███████║██║   ██║██║   ██║███████╗█████╗  \n" +
                      "  ██║     ██╔══██║██║   ██║██║   ██║╚════██║██╔══╝  \n" +
                      "  ╚██████╗██║  ██║╚██████╔╝╚██████╔╝███████║███████╗\n" +
                      "   ╚═════╝╚═╝  ╚═╝ ╚═════╝  ╚═════╝ ╚══════╝╚══════╝\n" +
                      "                                                  \n" +
                      "  ███╗   ███╗ ██████╗ ██████╗ ███████╗              \n" +
                      "  ████╗ ████║██╔═══██╗██╔══██╗██╔════╝              \n" +
                      "  ██╔████╔██║██║   ██║██║  ██║█████╗                \n" +
                      "  ██║╚██╔╝██║██║   ██║██║  ██║██╔══╝                \n" +
                      "  ██║ ╚═╝ ██║╚██████╔╝██████╔╝███████╗              \n" +
                      "  ╚═╝     ╚═╝ ╚═════╝ ╚═════╝ ╚══════╝              \n&f&r"));

        try {
            new ModrinthUpdate("choose-mode", Bukkit.getServer().getName())
                    .checkVersion(version -> {
                        if (getDescription().getVersion().equals(version)) {
                            console("(ChooseMode) The latest supported version is used.");
                        } else {
                            console("[!] (ChooseMode) A new version " + version + " has been detected (You have " + getDescription().getVersion() + ")");
                            console("[!] (ChooseMode) https://modrinth.com/project/choose-mode/versions");
                        }
                    });
        } catch (Exception e) {
            console("[!] (ChooseMode) Error checking the new version");
        }

        try {
            db = new DataBase(this);
            console("(ChooseMode) Load data from database");
        } catch (SQLException e) {
            console("[!] (ChooseMode) Database loading error");
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
            console("(ChooseMode) Loaded integration: PlaceholderAPI");
        } else {
            console("[!] (ChooseMode) Not loaded integration: PlaceholderAPI");
        }

        new Mode();
        new ru.mr_hares.chooseMode.commands.ChooseMode();
    }

    private void registerEvents(Plugin plugin, Listener... listeners) {
        PluginManager pm = Bukkit.getPluginManager();
        for (Listener listener : listeners) {
            pm.registerEvents(listener, plugin);
        }

        List<String> events = Arrays.stream(listeners).map(listener -> listener.getClass().getSimpleName()).toList();
        console("(ChooseMode) Loaded events: " + String.join(", ", events));
    }

    @Override
    public void onDisable() {
        console(color("\n   ██████╗██╗  ██╗ ██████╗  ██████╗ ███████╗███████╗\n" +
                      "  ██╔════╝██║  ██║██╔═══██╗██╔═══██╗██╔════╝██╔════╝\n" +
                      "  ██║     ███████║██║   ██║██║   ██║███████╗█████╗  \n" +
                      "  ██║     ██╔══██║██║   ██║██║   ██║╚════██║██╔══╝  \n" +
                      "  ╚██████╗██║  ██║╚██████╔╝╚██████╔╝███████║███████╗\n" +
                      "   ╚═════╝╚═╝  ╚═╝ ╚═════╝  ╚═════╝ ╚══════╝╚══════╝\n" +
                      "                                                  \n" +
                      "  ███╗   ███╗ ██████╗ ██████╗ ███████╗              \n" +
                      "  ████╗ ████║██╔═══██╗██╔══██╗██╔════╝              \n" +
                      "  ██╔████╔██║██║   ██║██║  ██║█████╗                \n" +
                      "  ██║╚██╔╝██║██║   ██║██║  ██║██╔══╝                \n" +
                      "  ██║ ╚═╝ ██║╚██████╔╝██████╔╝███████╗              \n" +
                      "  ╚═╝     ╚═╝ ╚═════╝ ╚═════╝ ╚══════╝              \n&f&r"));
    }

    public static ChooseMode getInstance() { return instance; }
    public static LocaleManager getMessages() { return messages; }
    public static DataBase getDB() { return db; }

    public static String color(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        message = message.replace("{prefix}", getMessages().getString("prefix"));

        Matcher matcher = Pattern.compile("&#([A-Fa-f0-9]{6})").matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.of("#" + hex).toString());
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    public static void console(String message) { Bukkit.getConsoleSender().sendMessage(message); }
}
