package com.peepersoak.adventurecraftcore.enchantment.crafting.events;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.enchantment.crafting.Book;
import com.peepersoak.adventurecraftcore.enchantment.crafting.MainItem;
import com.peepersoak.adventurecraftcore.enchantment.crafting.Result;
import com.peepersoak.adventurecraftcore.utils.Flags;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AnvilCraft implements Listener {

	@EventHandler
	public void onAnvilCraft(PrepareAnvilEvent e) {
		ItemStack[] ingredients = e.getInventory().getContents();
		for (ItemStack item : ingredients) {
			if (item == null) return;
		}
		ItemStack item1 = ingredients[0];
		ItemStack item2 = ingredients[1];
		if (item1.getType() == Material.ENCHANTED_BOOK&& item1.getItemMeta().getLore() != null) return;
		if (item1.getType() == Material.PAPER && item1.getItemMeta().getLore() != null ||
				item2.getType() == Material.PAPER && item2.getItemMeta().getLore() != null) return;
			
		if (item2.getType() == Material.ENCHANTED_BOOK) {
			MainItem item = new MainItem();
			Book book = new Book();
			Result result = new Result();
			item.setItem(item1);
			book.setItem(item2);
			result.setItem(item1);
			String bookName = book.getBookName();
			boolean allowEnchant = false;
			if (bookName.equalsIgnoreCase("Enchant Scripture") || bookName.equalsIgnoreCase("Forbidden Scripture")
					|| bookName.equalsIgnoreCase("Forgotten Scripture")) {
				String bookLore = book.getEnchantName();
				List<String> newLore = new ArrayList<>();
				if (book.getItemLore() == null && book.getItemEnchant() == null) return;
				if (!book.getIsUpgradable()) {
					if (item.getItemLore() != null) {
						for (String lores : item.getItemLore()) {
							String lore = ChatColor.stripColor(lores);
							if (lore.equalsIgnoreCase(bookLore)) return;
						}
					}
				}
				if (!book.getIsNormal()) {
					if (!item.canEnchant(book.getEnchantName())) return;
					int loopCount = 0;
					if (item.getItemLore() != null) {
						if (item.getHashMap() != null) result.setEnchantment(item.getHashMap());
						for (String lore : item.getItemLore()) {
							loopCount++;
							String[] enchants = ChatColor.stripColor(lore).split(":");
							String enchantName = enchants[0];
							if (enchantName.equalsIgnoreCase(bookLore)) {
								if (enchants.length > 1) {
									int loreLevel = Integer.parseInt(enchants[1].trim()) + 1;
									newLore.add(ChatColor.DARK_GREEN + "" + bookLore + ": " + loreLevel);
								} else {
									newLore.add(ChatColor.DARK_GREEN + "" + bookLore + ": " + 2);
								}
								allowEnchant = true;
							} else {
								if (!newLore.contains(lore)) {
									newLore.add(lore);
									if (loopCount == item.getItemLore().size()) {
										if (!allowEnchant) {
											newLore.add(ChatColor.DARK_GREEN + "" + bookLore);
											allowEnchant = true;
										}
									}
								}
							}
						}
					} else {
						newLore.add(" ");
						newLore.add(ChatColor.GOLD + "Forbidden Enchant:");
						newLore.add(ChatColor.DARK_GREEN + bookLore);
						allowEnchant = true;
					}
				} else {
					if (item.getHashMap() != null) {
						Set<Enchantment> enchants = item.getHashMap().keySet();
						Enchantment bookEnchant = book.getEnchantment();
						HashMap<Enchantment, Integer> newEnchantment = new HashMap<>();
						for (Enchantment ench : enchants) {
							if (ench.equals(bookEnchant)) {
								int enchantmentBookLevel = book.getEnchantmentLevel();
								int enchantmentMaxLevel = book.getEnchantment().getMaxLevel();
								int enchantmentItemLevel = item.getHashMap().get(ench);
								if (enchantmentBookLevel > enchantmentMaxLevel) {
									if ((enchantmentItemLevel + 1) == enchantmentBookLevel) {
										newEnchantment.put(bookEnchant, enchantmentBookLevel);
										allowEnchant = true;
									}
								}
							} else {
								newEnchantment.put(ench, item.getHashMap().get(ench));
							}
						}
						if (allowEnchant) {
							if (item.getItemLore() != null) result.setItemLore(item.getItemLore());
							result.setEnchantment(newEnchantment);
						}
					}
				}
				if (!newLore.isEmpty()) {
					result.setItemLore(newLore);
				}
				if (allowEnchant) {
					if (Utils.checkWGState(e.getView().getPlayer(), Flags.ALLOW_CUSTOM_CRAFT_ENCHANT)) {
						e.setResult(result.createItem());
						AdventureCraftCore.getInstance().getServer().getScheduler().runTask(AdventureCraftCore.getInstance(), () -> e.getInventory().setRepairCost(30));
					}
				}
			}
		}
	}
}
