package ru.mr_hares.dualmode.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mr_hares.dualmode.DualMode;

import java.util.List;

import static ru.mr_hares.dualmode.DualMode.*;

public class ModePlaceholder extends PlaceholderExpansion {
    private DualMode plugin;

    public ModePlaceholder(DualMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "dualmode";
    }

    @Override
    public @NotNull String getAuthor() {
        return "mr_hares";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {

        if (params.equalsIgnoreCase("mode")) {
            String status = getDB().getMode(player.getUniqueId());
            if (status == null) {
                return "";
            }

            return DualMode.getInstance().getConfig().getString("placeholder.status." + status, "");
        } else if (params.equalsIgnoreCase("total")) {
            List<String[]> stats = getDB().getList();

            int total = stats.size();

            return String.valueOf(total);
        } else if (params.equalsIgnoreCase("pve")) {
            List<String[]> stats = getDB().getList();

            long pve = stats.stream().filter(f -> f[1].equals("pve")).count();

            return String.valueOf(pve);
        } else if (params.equalsIgnoreCase("pvp")) {
            List<String[]> stats = getDB().getList();

            long pvp = stats.stream().filter(f -> f[1].equals("pvp")).count();

            return String.valueOf(pvp);
        }

        return null;
    }
}
