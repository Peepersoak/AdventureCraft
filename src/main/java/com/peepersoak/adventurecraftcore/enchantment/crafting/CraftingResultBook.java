package com.peepersoak.adventurecraftcore.enchantment.crafting;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CraftingResultBook {
	
	private String defaultName;
	private String bookName;
	private ItemMeta meta;
	private List<String> bookLore;
	private Map<Enchantment, Integer> enchantment;

	public void setItemMeta(ItemStack paper) {
		meta = paper.getItemMeta();
	}
	
	public void setBookName () {
		bookName = meta.getDisplayName();	
	}
	
	public void setDefaultName() {
		defaultName = ChatColor.stripColor(meta.getDisplayName());
	}
	
	public void setBookEnchant() {
		ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
		List<String> paperLore = meta.getLore();
		String enchant = paperLore.get(1);
		String enchantNoColor = ChatColor.stripColor(enchant);
		String[] enchantSplit = enchantNoColor.split(":");
		String enchantName = enchantSplit[0];
		String enchantKey = enchantName.replace(" ", "_").toLowerCase();
		int enchantLevel = Integer.parseInt(enchantSplit[1].trim());
		book.addUnsafeEnchantment(Objects.requireNonNull(Enchantment.getByKey(NamespacedKey.minecraft(enchantKey))), enchantLevel);
		enchantment = book.getEnchantments();
	}
	
	public void setBookLore() {
		bookLore = meta.getLore();
	}
	
	
	public String getDefaultName() {
		return defaultName;
	}
	
	public String getBookName() {
		return bookName;
	}
	
	public List<String> getBookLore(){
		return bookLore;
	}
	
	public Map<Enchantment, Integer> getEnchantment(){
		return enchantment;
	}
	
	
	public ItemStack createBook(String type) {
		ItemStack book = null;
		if (type.equalsIgnoreCase("Normal")) {
			book = new ItemStack(Material.BOOK);
		} else {
			book = new ItemStack(Material.ENCHANTED_BOOK);
			ItemMeta itemMeta = book.getItemMeta();
			setBookName();
			setBookLore();
			itemMeta.setDisplayName(bookName);
			if (type.equalsIgnoreCase("Enchant Scripture")) {
				setBookEnchant();
				book.setItemMeta(itemMeta);
				book.addUnsafeEnchantments(enchantment);
				return book;
			}
			else if (type.equalsIgnoreCase("Forbidden Scripture") ||
					type.equalsIgnoreCase("Forgotten Scripture")) {
				itemMeta.setLore(bookLore);
				book.setItemMeta(meta);
				return book;
			}
		}
		return book;
	}
}
