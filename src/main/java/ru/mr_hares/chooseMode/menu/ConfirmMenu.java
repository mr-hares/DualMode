package ru.mr_hares.chooseMode.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import ru.mr_hares.chooseMode.ChooseMode;

import java.util.List;

import static ru.mr_hares.chooseMode.ChooseMode.*;

public class ConfirmMenu implements InventoryHolder {
    private Inventory inventory;
    private static boolean forceClosed = false;
    private static NamespacedKey MODE_TYPE = new NamespacedKey(getInstance(), "mode_type");

    public ConfirmMenu(String mode) {
        inventory = Bukkit.createInventory(this, 27);
        Material material = mode.equals("pve") ? Material.SHIELD : Material.DIAMOND_SWORD;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(color(getMessages().getString("menu.choose." + mode + ".name")));

        List<String> PVElore =
                getMessages().getYAML().getStringList("menu.choose." + mode + ".lore").stream().map(ChooseMode::color
        ).toList();
        meta.setLore(PVElore);

        item.setItemMeta(meta);

        inventory.setItem(13, item);
        inventory.setItem(4, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        inventory.setItem(22, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                int slot = i*9 + j;
                ItemStack accept = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
                ItemMeta a_meta = accept.getItemMeta();

                a_meta.setDisplayName(color("&aПОДТВЕРДИТЬ"));

                PersistentDataContainer a_container = a_meta.getPersistentDataContainer();
                a_container.set(MODE_TYPE, PersistentDataType.STRING, mode);

                accept.setItemMeta(a_meta);
                inventory.setItem(slot, accept);
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 5; j < 9; j++) {
                int slot = i*9 + j;
                ItemStack decline = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                ItemMeta d_meta = decline.getItemMeta();

                d_meta.setDisplayName(color("&cОТКЛОНИТЬ"));

                PersistentDataContainer d_container = d_meta.getPersistentDataContainer();
                d_container.set(MODE_TYPE, PersistentDataType.STRING, "back");

                decline.setItemMeta(d_meta);
                inventory.setItem(slot, decline);
            }
        }
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
