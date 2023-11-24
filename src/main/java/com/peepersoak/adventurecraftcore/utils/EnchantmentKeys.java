package com.peepersoak.adventurecraftcore.utils;

import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnchantmentKeys {

    private final HashMap<String, String> keys = new HashMap<>();
    public EnchantmentKeys() {
        for (Enchantment enchantment : Enchantment.values()) {
            String key = enchantment.getKey().getKey();
            String ench = enchantment.toString();

            Pattern pattern = Pattern.compile("\\[(.*?)]", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(ench);

            String bukkitName = "";
            while (matcher.find()) {
                String[] str = matcher.group(1).split(",");
                if (str.length != 2) continue;
                bukkitName = str[1].trim().toUpperCase();
            }
            if (bukkitName.equalsIgnoreCase("")) continue;
            keys.put(bukkitName, key);
        }
    }

    public String convertToMinecraftKey(String str) {
        String check = str.toUpperCase();
        if (keys.containsKey(check)) {
            return keys.get(check).toLowerCase();
        }
        return str.toLowerCase();
    }
}
