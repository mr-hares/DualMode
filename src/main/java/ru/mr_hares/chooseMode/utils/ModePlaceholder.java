package ru.mr_hares.chooseMode.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mr_hares.chooseMode.ChooseMode;

import static ru.mr_hares.chooseMode.ChooseMode.*;

public class ModePlaceholder extends PlaceholderExpansion {
    private ChooseMode plugin;

    public ModePlaceholder(ChooseMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "choosemode";
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

            return ChooseMode.getInstance().getConfig().getString("placeholder.status." + status, "");
        }

        return null;
    }
}
