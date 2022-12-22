package com.peepersoak.adventurecraftcore.enchantment.crafting;

import com.peepersoak.adventurecraftcore.enchantment.Enchantments;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MainItem {
	
	Enchantments enchantment = new Enchantments();

	private ItemStack item;
	private List<String> itemLore;
	private HashMap<Enchantment, Integer> itemEnchantment;
	private String itemType;
	
	public List<String> getItemLore(){
		return itemLore;
	}
	public HashMap<Enchantment, Integer> getHashMap(){
		return itemEnchantment;
	}
	public String getItemType() {
		return itemType;
	}
	
	public void setItem(ItemStack item) {
		this.item = item;
		setItemLore();
		setItemType();
		setItemEnchant();
	}
	
	public void setItemLore() {
		itemLore = new ArrayList<>();
		itemLore = Objects.requireNonNull(item.getItemMeta()).getLore();
	}
	
	public void setItemType() {
		String type = item.getType().toString().toLowerCase();
		if (type.contains("sword")) {
			itemType = "sword";
		}
		else if (type.contains("helmet")) {
			itemType = "helmet";
		}
		else if (type.contains("chestplate")) {
			itemType = "chestplate";
		}
		else if (type.contains("leggings")) {
			itemType = "leggings";
		}
		else if (type.contains("boots")) {
			itemType = "boots";
		} 
		else if (type.contains("bow")) {
			itemType = "bow";
		}
		else if (type.contains("shovel")) {
			itemType = "shovel";
		}
		else if (type.contains("axe")) {
			itemType = "axe";
		}
		else if (type.contains("pickaxe")) {
			itemType = "pickaxe";
		}
		else if (type.contains("hoe")) {
			itemType = "hoe";
		}
	}
	
	public void setItemEnchant() {
		itemEnchantment = new HashMap<>();
		Map<Enchantment, Integer> enchant = item.getEnchantments();
		if (enchant.isEmpty()) return;
		Set<Enchantment> key = enchant.keySet();
		for (Enchantment ench : key) {
			itemEnchantment.put(ench, enchant.get(ench));
		}
	}
	public boolean canEnchant(String enchant) {
		enchantment.setAllEnchantment();
		if (itemType.equalsIgnoreCase("sword")) {
			return enchantment.getSwordEnchant().contains(enchant);
		}
		else if (itemType.equalsIgnoreCase("bow")) {
			return enchantment.getBowEnchant().contains(enchant);
		}
		else if (itemType.equalsIgnoreCase("helmet")) {
			return enchantment.getHelmetEnchant().contains(enchant);
		}
		else if (itemType.equalsIgnoreCase("chestplate")) {
			return enchantment.getChestplateEnchant().contains(enchant);
		}
		else if (itemType.equalsIgnoreCase("leggings")) {
			return enchantment.getLeggingsEnchant().contains(enchant);
		}
		else if (itemType.equalsIgnoreCase("boots")) {
			return enchantment.getBootsEnchant().contains(enchant);
		}
		else if (itemType.equalsIgnoreCase("shovel")) {
			return enchantment.getShovelEnchant().contains(enchant);
		}
		else if (itemType.equalsIgnoreCase("axe")) {
			return enchantment.getAxeEnchant().contains(enchant);
		}
		else if (itemType.equalsIgnoreCase("pickaxe")) {
			return enchantment.getPickaxeEnchant().contains(enchant);
		}
		else if (itemType.equalsIgnoreCase("hoe")) {
			return enchantment.getHoeEnchant().contains(enchant);
		}
		return false;
	}
}
