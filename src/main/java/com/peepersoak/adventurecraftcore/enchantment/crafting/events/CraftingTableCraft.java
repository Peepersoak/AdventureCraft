package com.peepersoak.adventurecraftcore.enchantment.crafting.events;

import com.peepersoak.adventurecraftcore.enchantment.crafting.CraftingResultBook;
import com.peepersoak.adventurecraftcore.enchantment.crafting.Paper;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CraftingTableCraft implements Listener {

	private final CraftingResultBook book = new CraftingResultBook();
	
	@EventHandler
	public void onCraft(PrepareItemCraftEvent e) {
		ItemStack[] ingredients = e.getInventory().getMatrix();
		
		int leather = 0;
		int paperCount = 0;
		int normal = 0;
		int custom = 0;
		int skill = 0;
		String lockEnchant = null;
		ItemStack lockPaper = null;
		
		for (ItemStack item : ingredients) {
			Paper paper = new Paper();
			if (item == null) continue;
			if (item.getType() == Material.LEATHER) leather++;
			if (item.getType() == Material.PAPER) {
				ItemMeta meta = item.getItemMeta();
				List<String> itemLore = meta.getLore();
				
				if (itemLore == null) {
					paperCount++;
				} else {
					// This means the item has lore
					paper.setItemMeta(item);
					paper.setPaperDefaultName();
					paper.setEnchantNameKeyandLevel();
					String paperName = paper.getDefaultName();

					if (paperName.equalsIgnoreCase("Enchant Scripture")) {
						if (!paper.isUpdated()) return;
						if (lockEnchant == null) {
							lockPaper = item;
							lockEnchant = paper.getEnchantNameKey();
							normal++;
						} else if (lockEnchant.equalsIgnoreCase(paper.getEnchantNameKey())) {
							lockPaper = item;
							normal++;
						}
					}
					
					if (paperName.equalsIgnoreCase("Forbidden Scripture")) {
						if (!paper.isUpdated()) return;
						if (lockEnchant == null) {
							lockPaper = item;
							lockEnchant = paper.getEnchantNameKey();
							custom++;
						} else if (lockEnchant.equalsIgnoreCase(paper.getEnchantNameKey())) {
							lockPaper = item;
							custom++;
						}
					}
					
					if (paperName.equalsIgnoreCase("Forgotten Scripture")) {
						if (!paper.isUpdated()) return;
						if (lockEnchant == null) {
							lockPaper = item;
							lockEnchant = paper.getEnchantNameKey();
							skill++;
						} else if (lockEnchant.equalsIgnoreCase(paper.getEnchantNameKey())) {
							lockPaper = item;
							skill++;
						}
					}
				}
			}
		}
		
		if (leather == 1 && paperCount == 3 && normal == 0 && custom == 0 & skill == 0) {
			book.createBook("Normal");
			e.getInventory().setResult(book.createBook("Normal"));
		}
		else if (leather == 1 && paperCount == 0 && normal == 3 && custom == 0 & skill == 0) {
			book.setItemMeta(lockPaper);
			e.getInventory().setResult(book.createBook("Enchant Scripture"));
		}
		else if (leather == 1 && paperCount == 0 && normal == 0 && custom == 3 & skill == 0) {
			book.setItemMeta(lockPaper);
			e.getInventory().setResult(book.createBook("Forbidden Scripture"));
		}
		else if (leather == 1 && paperCount == 0 && normal == 0 && custom == 0 & skill == 3) {
			book.setItemMeta(lockPaper);
			e.getInventory().setResult(book.createBook("Forgotten Scripture"));
		}
	}
}
