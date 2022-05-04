package com.peepersoak.adventurecraftcore.items.arrows;

import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ArrowFactory {

    public ItemStack createArrow() {
        ArrowType[] types = ArrowType.values();
        ArrowType type = types[Utils.getRandom(types.length - 1, 0)];

        ItemStack arrow = new ItemStack(Material.ARROW);
        arrow.addUnsafeEnchantment(Enchantment.DURABILITY, 0);

        ItemMeta meta = arrow.getItemMeta();
        if (meta == null) return null;
        arrow.setItemMeta(getMetaData(meta, type));
        return arrow;
    }

    public ItemStack createArrow(ArrowType type) {
        ItemStack arrow = new ItemStack(Material.ARROW);
        arrow.addUnsafeEnchantment(Enchantment.DURABILITY, 0);

        ItemMeta meta = arrow.getItemMeta();
        if (meta == null) return null;
        arrow.setItemMeta(getMetaData(meta, type));
        return arrow;
    }

    private ItemMeta getMetaData(ItemMeta meta, ArrowType type) {
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        Utils.getPDC(meta).set(StringPath.CUSTOM_ARROW, PersistentDataType.STRING, type.name());
        meta.setDisplayName(Utils.color("&6" + type.name() + " ARROW"));
        meta.setLore(getArrowLore(type));
        return meta;
    }

    private List<String> getArrowLore(ArrowType type) {
        List<String> lore = new ArrayList<>();
        lore.add(" ");

        switch (type) {
            case LIGHTNING -> {
                lore.add(Utils.color("&7Hit your target with a"));
                lore.add(Utils.color("&7Lightning strike!"));
            }
            case EXPLOSIVE -> {
                lore.add(Utils.color("&7Make your target go"));
                lore.add(Utils.color("&7boom boom"));
            }
            case GRAVITY -> {
                lore.add(Utils.color("&7Compress all possible"));
                lore.add(Utils.color("&7Mobs in target location"));
            }
            case HEADSHOT -> {
                lore.add(Utils.color("&7Deal more damage to the"));
                lore.add(Utils.color("&7target"));
            }
            case BURROW -> {
                lore.add(Utils.color("&7Put your target underneath"));
                lore.add(Utils.color("&7the ground"));
            }
            case POISON -> {
                lore.add(Utils.color("&7Poison the target, dealing"));
                lore.add(Utils.color("&7damage per second"));
            }
            case PORTAL -> {
                lore.add(Utils.color("&7Teleport to the target"));
                lore.add(Utils.color("&7Location"));
            }
            case SHOTGUN -> {
                lore.add(Utils.color("&7Shoot multiple arrow"));
                lore.add(Utils.color("&7at the same time"));
            }
            case LAVA -> {
                lore.add(Utils.color("&7Create a lava on target"));
                lore.add(Utils.color("&7location"));
            }
            case WATER -> {
                lore.add(Utils.color("&7Create a water source on target"));
                lore.add(Utils.color("&7location"));
            }
            case FEATHER -> {
                lore.add(Utils.color("&7Make your target float for 5"));
                lore.add(Utils.color("&7Seconds"));
            }
            case WEB -> {
                lore.add(Utils.color("&7Spawn a cobweb on target"));
                lore.add(Utils.color("&7location"));
            }
            case FLYING -> {
                lore.add(Utils.color("&7Ride the arrow after"));
                lore.add(Utils.color("&7shooting it"));
            }
        }
        return lore;
    }
}
