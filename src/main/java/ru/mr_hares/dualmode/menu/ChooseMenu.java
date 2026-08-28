package ru.mr_hares.dualmode.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import ru.mr_hares.dualmode.DualMode;

import java.util.List;

import static ru.mr_hares.dualmode.DualMode.*;

public class ChooseMenu implements InventoryHolder {
    private static NamespacedKey MODE_TYPE = new NamespacedKey(getInstance(), "mode_type");
    private static boolean forceClosed = false;
    private Inventory inventory;

    public ChooseMenu() {
        inventory = Bukkit.createInventory(this, 27, colorize(getInstance().getConfig().getString("menu.settings.title", "&5" +
                "Выбор режима")));

        ItemStack PVE = getOptionItem("pve");
        ItemStack PVP = getOptionItem("pvp");

        inventory.setItem(getInstance().getConfig().getInt("menu.icons.pve.slot", 11), PVE);
        inventory.setItem(getInstance().getConfig().getInt("menu.icons.pvp.slot", 15), PVP);

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            }
        }
    }

    private ItemStack getOptionItem(String mode) {
        ItemStack item = new ItemStack(Material.valueOf(getInstance().getConfig().getString("menu.icons." + mode + ".material", mode.equals("pve") ? "SHIELD" : "DIAMOND_SWORD")));
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(colorize(getMessages().getString("menu.choose." + mode + ".name")));
        List<String> lore =
                getMessages().getYAML().getStringList("menu.choose." + mode + ".lore").stream().map(DualMode::colorize).toList();
        meta.setLore(lore);

        if (getInstance().getConfig().getBoolean("menu.icons." + mode + ".enchant-glow", true)) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        PersistentDataContainer PVEcontainer = meta.getPersistentDataContainer();
        PVEcontainer.set(MODE_TYPE, PersistentDataType.STRING, mode);

        item.setItemMeta(meta);

        return item;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public static String getMode(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.get(MODE_TYPE, PersistentDataType.STRING);
    }

    public void setForceClosed(boolean force) {
        forceClosed = force;
    }

    public static boolean isForceClosed() {
        return forceClosed;
    }
}
