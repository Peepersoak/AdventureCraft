package com.peepersoak.adventurecraftcore.enchantment.crafting;

import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;

public class Paper {

	private String defaultName;
	private String paperName;
	private String enchantNameKey;
	private int enchantLevel;
	private ItemMeta meta;
	private List<String> paperLore;
	private Map<Enchantment, Integer> enchantment;
	
	public void setItemMeta(ItemStack paper) {
		meta = paper.getItemMeta();
	}
	
	public void setPaperDefaultName() {
		String name = meta.getDisplayName();
		defaultName = ChatColor.stripColor(name);
	}
	
	public void setEnchantNameKeyandLevel() {
		List<String> paperLore = meta.getLore();
		String enchant = paperLore.get(1);
		String enchantNoColor = ChatColor.stripColor(enchant);
		String[] enchantSplit = enchantNoColor.split(":");
		String enchantName = enchantSplit[0];
		enchantNameKey = enchantName.replace(" ", "_").toLowerCase();
		if (enchantSplit.length > 1) {
			enchantLevel = Integer.parseInt(enchantSplit[1].trim());
		}
	}
	
	public void setPaperName() {
		paperName = meta.getDisplayName();
	}
	
	public void setPaperLore() {
		paperLore = meta.getLore();
	}
	
	public void setEnchantMent() {
		enchantment.put(Enchantment.getByKey(NamespacedKey.minecraft(enchantNameKey)), enchantLevel);
	}
	
	public String getDefaultName() {
		return defaultName;
	}
	
	public String getPaperName() {
		return paperName;
	}
	
	public List<String> getPaperLore(){
		return paperLore;
	}
	
	public String getEnchantNameKey() {
		return enchantNameKey;
	}
	public Map<Enchantment, Integer> getEnchantment(){
		return enchantment;
	}

	public boolean isUpdated() {
		return Utils.getPDC(meta).has(StringPath.ENCHANT_META, PersistentDataType.INTEGER);
	}
}
