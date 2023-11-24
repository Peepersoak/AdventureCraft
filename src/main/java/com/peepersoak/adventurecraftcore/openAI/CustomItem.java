package com.peepersoak.adventurecraftcore.openAI;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CustomItem {
    private final String itemRank;
    private final String itemType;
    private final String itemName;
    private final String itemLore;
    private final List<String> enchantmenst;
    private final List<String> attributes;
    private final String extraLore;
    private final String trimPattern;
    private final String trimMaterial;


    public CustomItem(
            String itemRank,
            String itemType,
            String itemName,
            String itemLore,
            String extraLore,
            List<String> enchantmenst,
            List<String> attributes,
            String trimPattern,
            String trimMaterial
    ) {
        this.itemRank = itemRank;
        this.itemType = itemType;
        this.itemName = itemName;
        this.itemLore = itemLore;
        this.extraLore = extraLore;
        this.enchantmenst = enchantmenst;
        this.attributes = attributes;
        this.trimPattern = trimPattern;
        this.trimMaterial = trimMaterial;

        createItemStack();
    }

    private ItemStack rewards = null;

    private void createItemStack() {
        if (itemType == null) return;
        ItemStack item = new ItemStack(Material.valueOf(Utils.cleanString(itemType)));
        HashMap<Enchantment, Integer> enchantments = new HashMap<>();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(ObjectiveStrings.PDC_CUSTOM_ITEM, PersistentDataType.STRING, "AC.Quest.CustomITEM");
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(Utils.color(itemName));
        List<String> lores = Utils.getLore(itemLore);
        if (extraLore != null && !extraLore.isEmpty()) {
            lores.add("");
            for (String additionalLore : Utils.getLore(extraLore)) {
                lores.add(Utils.color(additionalLore));
            }
        }
        if (!enchantmenst.isEmpty()) {
            lores.add("");
        }
        for (String enchantmentData : enchantmenst) {
            String[] split = enchantmentData.split(":");
            String enchantmentKey = split[0].trim();
            int level;
            try {
                level = Integer.parseInt(split[1].trim());
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(Utils.cleanString(enchantmentKey)));
                if (enchantment != null && enchantment.canEnchantItem(item)) {
                    enchantments.put(enchantment, level);
                    String enchantmentLore = "&4♣ &8" + Utils.capitalizeFirstLetter(enchantmentKey) + " &4" + level;
                    lores.add(Utils.color(enchantmentLore));
                }
            } catch (IllegalArgumentException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        lores.add("");
        lores.add(Utils.color("&3Item Rank: " + getItemRankColored(itemRank)));
        if (Utils.cleanString(itemRank).equalsIgnoreCase("Godlike") || Utils.cleanString(itemRank).equalsIgnoreCase("Ascended")) {
            meta.setUnbreakable(true);
            lores.add("&4✤ Unbreakable");
        }
        for (String attributeData : attributes) {
            String[] split = attributeData.split(":");
            String attribute = split[0];
            double modifier;

            try {
                modifier = Double.parseDouble(split[1]);
                Attribute finalAttribute = Attribute.valueOf(attribute);

                EquipmentSlot slot = EquipmentSlot.OFF_HAND;
                Material material = item.getType();
                if (Utils.isWeapon(material) || Utils.isTool(material)) {
                    slot = EquipmentSlot.HAND;
                } else if (Utils.isHelmet(material)) {
                    slot = EquipmentSlot.HEAD;
                } else if (Utils.isChestplate(material)) {
                    slot = EquipmentSlot.CHEST;
                } else if (Utils.isLeggings(material)) {
                    slot = EquipmentSlot.LEGS;
                } else if (Utils.isBoots(material)) {
                    slot = EquipmentSlot.FEET;
                }
                AttributeModifier attributeModifier = new AttributeModifier(UUID.randomUUID(), attribute, modifier, AttributeModifier.Operation.ADD_NUMBER, slot);
                meta.addAttributeModifier(finalAttribute, attributeModifier);
            } catch (IllegalArgumentException | NullPointerException e) {
                e.printStackTrace();
            }
        }

        meta.setLore(lores);
        if (meta instanceof ArmorMeta armorMeta) {
            armorMeta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
            if (trimPattern != null && trimMaterial != null) {
                TrimPattern pattern = AdventureCraftCore.getInstance().getQuestListChecker().getTrimPattern(trimPattern);
                TrimMaterial material = AdventureCraftCore.getInstance().getQuestListChecker().getTrimMaterial(trimMaterial);
                if (pattern != null && material != null) {
                    armorMeta.setTrim(new ArmorTrim(material, pattern));
                }
            }
        }

        item.setItemMeta(meta);
        for (Enchantment enchantment : enchantments.keySet()) {
            if (enchantment.getMaxLevel() == 1) {
                item.addUnsafeEnchantment(enchantment, 1);
            } else {
                item.addUnsafeEnchantment(enchantment, enchantments.get(enchantment));
            }
        }
        rewards = item;
    }

    public ItemStack getRewards() {
        return rewards;
    }
    public String getItemRank() {
        return itemRank;
    }
    public String getItemName() {
        return itemName;
    }
    public String getItemType() {
        return itemType;
    }
    public String getItemLore() {
        return itemLore;
    }
    public List<String> getEnchantmenst() {
        return enchantmenst;
    }
    public List<String> getAttributes() {
        return attributes;
    }
    public String getExtraLore() {
        return extraLore;
    }
    private String getItemRankColored(String rank) {
        if (rank.equalsIgnoreCase(ObjectiveStrings.UNCOMMON)) {
            return "&a" + rank;
        } else if (rank.equalsIgnoreCase(ObjectiveStrings.RARE)) {
            return "&9" + rank;
        } else if (rank.equalsIgnoreCase(ObjectiveStrings.EPIC)) {
            return "&5" + rank;
        } else if (rank.equalsIgnoreCase(ObjectiveStrings.LEGENDARY)) {
            return "&6" + rank;
        } else if (rank.equalsIgnoreCase(ObjectiveStrings.MYTHICAL)) {
            return "&d" + rank;
        } else if (rank.equalsIgnoreCase(ObjectiveStrings.FABLED)) {
            return "&b" + rank;
        } else if (rank.equalsIgnoreCase(ObjectiveStrings.GODLIKE)) {
            return "&4" + rank;
        } else if (rank.equalsIgnoreCase(ObjectiveStrings.ASCENDED)) {
            return "&e" + rank;
        } else {
            return "&7" + rank;
        }
    }
    public String getTrimPattern() {
        return trimPattern;
    }
    public String getTrimMaterial() {
        return trimMaterial;
    }
}
