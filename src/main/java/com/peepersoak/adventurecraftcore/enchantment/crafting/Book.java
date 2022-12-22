package com.peepersoak.adventurecraftcore.enchantment.crafting;

import com.peepersoak.adventurecraftcore.enchantment.Enchantments;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Book {

	Enchantments enchant = new Enchantments();
	
	private ItemStack item;
	private String bookName;
	private String customEnchantName;
	private int bookLevel;
	private List<String> itemLore;
	private HashMap<Enchantment, Integer> itemEnchants;
	private Enchantment enchantment;
	private int enchantmentLevel;
	private boolean isNormal;
	private boolean isUpgradable;
	
	public String getBookName() {
		return bookName;
	}
	public String getEnchantName() {
		return customEnchantName;
	}
	public int getBookLevel() {
		return bookLevel;
	}
	public List<String> getItemLore() {
		return itemLore;
	}
	public HashMap<Enchantment, Integer> getItemEnchant() {
		return itemEnchants;
	}
	public boolean getIsNormal() {
		return isNormal;
	}
	public boolean getIsUpgradable() {
		return isUpgradable;
	}
	public Enchantment getEnchantment() {
		return enchantment;
	}
	public Integer getEnchantmentLevel() {
		return enchantmentLevel;
	}
	
	public void setItem(ItemStack item) {
		this.item = item;
		setBookName();
		setItemLore();
		setIsNormal();
		setItemEnchants();
		setCustomEnchantNameAndLevel();
		isUpgradable();
	}
	
	public void isUpgradable() {
		enchant.setAllEnchantment();
		if (enchant.getNotUpgradable().contains(customEnchantName)) {
			isUpgradable = false;
		} else {
			isUpgradable = true;
		}
	}
	
	public void setBookName() {
		bookName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
	}
	
	public void setItemLore() {
		itemLore = new ArrayList<>();
		itemLore = item.getItemMeta().getLore();
	}
	
	public void setCustomEnchantNameAndLevel() {
		if (itemLore == null) return;
		String[] lores = itemLore.get(1).split(":");
		customEnchantName = ChatColor.stripColor(lores[0]);
		if (lores.length < 1) {
			bookLevel = Integer.parseInt(ChatColor.stripColor(lores[1].trim()));
		}
	}
	
	public void setIsNormal() {
		if (itemLore == null) {
			isNormal = true;
		} else {
			isNormal = false;
		}
	}
	
	public void setItemEnchants() {
		itemEnchants = new HashMap<>();
		Map<Enchantment, Integer> enchant = item.getEnchantments();
		if (enchant.isEmpty()) return;
		Set<Enchantment> key = enchant.keySet();
		for (Enchantment ench : key) {
			enchantment = ench;
			enchantmentLevel = enchant.get(ench);
			itemEnchants.put(ench, enchant.get(ench));
		}
	}
}
