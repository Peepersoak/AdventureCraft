package com.peepersoak.adventurecraftcore.enchantment.crafting;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class Result {

	private ItemStack item;
	private String itemName;
	private ItemMeta meta;
	private HashMap<Enchantment, Integer> itemEnchant;
	private List<String> itemLore;
	
	public void setItem(ItemStack item) {
		this.item = new ItemStack(item);
		setItemName();
		setItemMeta();
	}
	
	public void setItemMeta() {
		meta = item.getItemMeta();
	}
	
	public void setItemName() {
		itemName = item.getItemMeta().getDisplayName();
	}
	
	public void setEnchantment(HashMap<Enchantment, Integer> enchantments) {
		itemEnchant = enchantments;
	}
	
	public void setItemLore(List<String> itemLore) {
		this.itemLore = itemLore;
	}
	
	public ItemStack createItem() {
		meta.setDisplayName(itemName);
		meta.setLore(itemLore);
		item.setItemMeta(meta);
		if (itemEnchant != null) {
			item.addUnsafeEnchantments(itemEnchant);
		}
		return item;
	}
}
