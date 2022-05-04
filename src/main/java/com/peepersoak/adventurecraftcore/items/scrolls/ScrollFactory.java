package com.peepersoak.adventurecraftcore.items.scrolls;

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

public class ScrollFactory {

    public ItemStack createScroll() {
        ScrollType[] types = ScrollType.values();
        ScrollType type = types[Utils.getRandom(types.length - 1, 0)];

        ItemStack paper = new ItemStack(Material.PAPER);
        paper.addUnsafeEnchantment(Enchantment.DURABILITY, 0);
        paper.setItemMeta(getItemMeta(paper, type));

        return paper;
    }

    private ItemMeta getItemMeta(ItemStack item, ScrollType type) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        Utils.getPDC(meta).set(StringPath.CUSTOM_SCROLL, PersistentDataType.STRING, type.name());
        meta.setDisplayName(Utils.color("&6" + type.name().replace("_", " ") + " SCROLL"));
        meta.setLore(getLore(type));
        return meta;
    }

    private List<String> getLore(ScrollType type) {
        List<String> scrollLore = new ArrayList<>();
        scrollLore.add(" ");

        switch (type) {
            case TELEPORT -> {
                scrollLore.add(Utils.color("&dAfter 5 second of chanting"));
                scrollLore.add(Utils.color("&dYou will be teleported to your last"));
                scrollLore.add(Utils.color("&dspawn location!"));
            }

            case ANGELS_BREATH -> {
                scrollLore.add(Utils.color("&dHeal all players in a 20 Blocks Radius"));
                scrollLore.add(Utils.color("&dArround the caster!"));
            }

            case  ACCOMPANY -> {
                scrollLore.add(Utils.color("&dAfter 5 second of chanting,"));
                scrollLore.add(Utils.color("&dTeleport all players in a 5 block radius"));
                scrollLore.add(Utils.color("&daround you to a players location"));
            }

            case MAGNETIC_FORCE -> {
                scrollLore.add(Utils.color("&dAfter 5 second of chanting,"));
                scrollLore.add(Utils.color("&dTeleport to a players location"));
            }

            case ALL_FOR_ONE -> {
                scrollLore.add(Utils.color("&dGet all positive effect"));
                scrollLore.add(Utils.color("&dfor 60 seconds"));
            }

            case ONE_FOR_ALL -> {
                scrollLore.add(Utils.color("&dAdd 1 random positive effect to"));
                scrollLore.add(Utils.color("&dall players in a 20 Block radius"));
                scrollLore.add(Utils.color("&dfor 60 seconds"));
            }

            case CLAIREVOYANCE -> scrollLore.add(Utils.color("&dView a players inventory"));
        }

        scrollLore.add(" ");
        scrollLore.add(Utils.color("&cRight-Clicked to use!"));
        return scrollLore;
    }
}
