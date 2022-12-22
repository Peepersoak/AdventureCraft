package com.peepersoak.adventurecraftcore.items.wards;

import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class WardFactory {

    public ItemStack createWard() {
        WardType[] types = WardType.values();
        WardType type = types[Utils.getRandom(types.length - 1, 0)];

        ItemStack ward = new ItemStack(Material.TOTEM_OF_UNDYING);
        ward.addUnsafeEnchantment(Enchantment.DURABILITY, 0);
        ward.setItemMeta(getItemMeta(ward, type));

        return ward;
    }

    public ItemStack createWard(WardType type) {
        ItemStack ward = new ItemStack(Material.TOTEM_OF_UNDYING);
        ward.addUnsafeEnchantment(Enchantment.DURABILITY, 0);
        ward.setItemMeta(getItemMeta(ward, type));

        return ward;
    }

    private ItemMeta getItemMeta(ItemStack item, WardType type) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        Utils.getPDC(meta).set(StringPath.CUSTOM_WARD, PersistentDataType.STRING, type.name());
        meta.setDisplayName(Utils.color("&6" + type.name().replace("_", " ") + " WARDS"));
        meta.setLore(getLore());
        return meta;
    }

    private List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GOLD + "Summon the necromancer");
        lore.add(ChatColor.GOLD + "on your location for 60 seconds!");
        lore.add("");
        lore.add(ChatColor.AQUA + "Right-Clicked to use");
        lore.add("");
        lore.add(ChatColor.RED + "One time use only!!");
        lore.add(ChatColor.RED + "Will also work as a regular Totem");
        return lore;
    }
}
