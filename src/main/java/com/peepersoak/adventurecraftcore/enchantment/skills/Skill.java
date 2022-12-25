package com.peepersoak.adventurecraftcore.enchantment.skills;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Skill {

	private ItemStack item;
	private boolean hasLore;
	private List<String> itemLore;
	private List<String> loreName;
	
	private HashMap<String, Integer> loreMap = new HashMap<>();
	
	public void setItem(ItemStack item) {
		this.item = item;
		setItemLore();
		setLoreMap();
	}
	
	public void setItemLore() {
		if (Objects.requireNonNull(item.getItemMeta()).getLore() != null) {
			itemLore = item.getItemMeta().getLore();
			hasLore = true;
		}
	}
	
	public void setLoreMap() {
		if (hasLore) {
			loreName = new ArrayList<>();
			loreMap = new HashMap<>();
			for (String loreName : itemLore) {
				String[] lore = ChatColor.stripColor(loreName).split(":");
				int level = 1;
				if (lore.length > 1) {
					try {
						level = Integer.parseInt(lore[1].trim());
					} catch (NumberFormatException e) {
						level = 0;
						//
					}
				}
				this.loreName.add(lore[0]);
				loreMap.put(lore[0], level);
			}
		}
	}
	
	public List<String> getLoreName() {
		return loreName;
	}
	
	public Map<String, Integer> getLoreMap() {
		return loreMap;
	}
}
