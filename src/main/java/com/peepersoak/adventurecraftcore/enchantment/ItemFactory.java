package com.peepersoak.adventurecraftcore.enchantment;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.enchantment.crafting.CraftingResultBook;
import com.peepersoak.adventurecraftcore.utils.Flags;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ItemFactory {

	public ItemFactory() {
		config = AdventureCraftCore.getInstance().getConfig();
	}
	private LivingEntity mob;
	private int mobLevel;
	private int materialLevel;
	private String materialEnchant;
	private String materialName;
	private String materialRarity;
	private String materialType;
	private List<String> materialLore;
	FileConfiguration config;
	Random rand = new Random();
	Enchantments enchantment = new Enchantments();
	
	public ItemStack createPaper() {
		ItemStack paper = new ItemStack(Material.PAPER);
		ItemMeta meta = paper.getItemMeta();
		if (meta == null) return null;
		meta.setDisplayName(materialName);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(materialLore);
		Utils.getPDC(meta).set(StringPath.ENCHANT_META, PersistentDataType.INTEGER, 1);
		paper.setItemMeta(meta);
		paper.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

		if (mobLevel >= materialLevel) {
			return paper;
		} else {
			return null;
		}
	}
	
	public ItemStack createBook(int level) {
		ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
		ItemMeta meta = book.getItemMeta();
		if (level <= 5) {
			mobLevel = 5;
		} else {
			mobLevel = level;
		}
		setMaterialType();
		setMaterialEnchant();
		setMaterialLevel();
		setMaterialName();
		setMaterialRarity();
		setLore();
		if (meta == null) return null;
		meta.setDisplayName(materialName);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(materialLore);
		book.setItemMeta(meta);
		return book;
	}

	public ItemStack createBook(int level, String type, String enchantment) {
		this.materialType = type;
		this.mobLevel = level;
		this.materialEnchant = enchantment.replace("_", " ");

		String bookType = "Normal";
		if (type.equalsIgnoreCase("normal")) {
			bookType = "Enchant Scripture";
		} else if (type.equalsIgnoreCase("custom")) {
			bookType = "Forbidden Scripture";
			mobLevel = 45;
		} else if (type.equalsIgnoreCase("skill")) {
			bookType = "Forgotten Scripture";
			mobLevel = 60;
		}

		setMaterialLevel();
		setMaterialName();
		setMaterialRarity();
		setLore();

		ItemStack paper = new ItemStack(Material.PAPER);
		ItemMeta meta = paper.getItemMeta();
		if (meta == null) return null;

		meta.setDisplayName(materialName);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(materialLore);
		Utils.getPDC(meta).set(StringPath.ENCHANT_META, PersistentDataType.INTEGER, 1);
		paper.setItemMeta(meta);
		paper.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

		CraftingResultBook book = new CraftingResultBook();
		book.setItemMeta(paper);

		return book.createBook(bookType);
	}

	public void setPaper(int level, String type) {
		this.mobLevel = level;
		this.materialType = type;

		setMaterialEnchant();
		setMaterialLevel();
		setMaterialName();
		setMaterialRarity();
		setLore();
	}
	
	public void setPaper(LivingEntity mob) {
		this.mob = mob;
		setMobLevel();
		setMaterialType();
		setMaterialEnchant();
		setMaterialLevel();
		setMaterialName();
		setMaterialRarity();
		setLore();
	}

	public void setMobLevel() {
		PersistentDataContainer data = mob.getPersistentDataContainer();
		if (data.has(StringPath.MOB_LEVEL_KEY, PersistentDataType.INTEGER)) {
			Integer i = data.get(StringPath.MOB_LEVEL_KEY, PersistentDataType.INTEGER);
			mobLevel = i == null ? 1 : i;
		}
	}
	
	public void setMaterialType() {
		int customChance = config.getInt("Drop_Chance." + "Custom");
		int skillChance = config.getInt("Drop_Chance." + "Skill");

		int customChanceWG = Utils.getWorldGuardValue(mob, Flags.CUSTOM_ENCHANT_CHANCE);
		int skillChanceWG = Utils.getWorldGuardValue(mob, Flags.SKILL_ENCHANT_CHANCE);

		if (customChanceWG != -1) customChance = customChanceWG;
		if (skillChanceWG != -1) skillChance = skillChanceWG;

		System.out.println(mobLevel);

		if (mobLevel <= 15) {
			materialType = "Normal";
		}
		
		if (mobLevel > 15 && mobLevel < 30) {
			int random = Utils.getRandom(100);
			if (random < customChance) {
				materialType = "Custom";
			} else {
				materialType = "Normal";
			}
		}
		
		if (mobLevel >= 30) {
			int random = Utils.getRandom(100);
			if (random < skillChance) {
				materialType =  "Skill";
			} else if (Utils.getRandom(100) < customChance) {
				materialType = "Custom";
			} else {
				materialType = "Normal";
			}
		}
	}
	
	public void setMaterialEnchant() {
		enchantment.setAllEnchantment();
		if (materialType.equalsIgnoreCase("Normal")) getRandomEnchant(enchantment.getNormalEnchant());
		if (materialType.equalsIgnoreCase("Custom")) getRandomEnchant(enchantment.getCustomEnchant());
		if (materialType.equalsIgnoreCase("Skill")) getRandomEnchant(enchantment.getSkills());
	}
	
	public void getRandomEnchant(List<String> list) {
		int getEnchant = rand.nextInt(list.size());
		materialEnchant = list.get(getEnchant);
	}
	
	public void setMaterialLevel() {
		String enchantName = materialEnchant.toLowerCase().replace(" ", "_");
		if (materialType.equalsIgnoreCase("Normal")) {
			int enchantMaxLevel = Objects.requireNonNull(Enchantment.getByKey(NamespacedKey.minecraft(enchantName))).getMaxLevel();
			materialLevel = Math.max(enchantMaxLevel + 1, mobLevel);
		} else {
			materialLevel = mobLevel;
		}
	}
	
	public void setLore() {
		boolean isCustom = false;
		materialLore = new ArrayList<>();
		materialLore.add(" ");
		if (materialType.equalsIgnoreCase("Normal")) {
			materialLore.add(ChatColor.AQUA + materialEnchant + ": " + materialLevel);
		}
		else if (materialType.equalsIgnoreCase("Custom") || materialType.equalsIgnoreCase("Skill")) {
			materialLore.add(ChatColor.AQUA + materialEnchant);
			isCustom = true;
		}
		
		if (isCustom) {
			materialLore.add(" ");
			ChatColor g = ChatColor.DARK_GRAY;
			switch (materialEnchant) {
				case "CRITICAL" -> {
					materialLore.add(g + "Chance to deal an additional");
					materialLore.add(g + "5 more damage per level");
					materialLore.add("");
					materialLore.add(g + "Applicable to:");
					materialLore.add(g + "SWORD AND AXE");
				}
				case "HEADSHOT" -> {
					materialLore.add(g + "Chance to deal an additional");
					materialLore.add(g + "5 more damage per level");
					materialLore.add("");
					materialLore.add(g + "Applicable to:");
					materialLore.add(g + "BOW");
				}
				case "SPEED" -> {
					materialLore.add(g + "Increase your speed by 400%");
					materialLore.add("");
					materialLore.add(g + "Applicable to:");
					materialLore.add(g + "BOOTS");}
				case "HEALTH BOOST" -> {
					materialLore.add(g + "Increase your health by");
					materialLore.add(g + "20 points per level");
					materialLore.add("");
					materialLore.add(g + "Applicable to:");
					materialLore.add(g + "CHEST PLATE");
				}
				case "LAST RESORT" -> {
					materialLore.add(g + "When taking a lethal damage");
					materialLore.add(g + "there's a chance to avoid death");
					materialLore.add("");
					materialLore.add(g + "Applicable to:");
					materialLore.add(g + "HELMET");
				}
				case "LIFE STEAL" -> {
					materialLore.add(g + "There's a chance to recover");
					materialLore.add(g + "a portion of your life base");
					materialLore.add(g + "on your damage");
					materialLore.add("");
					materialLore.add(g + "Applicable to:");
					materialLore.add(g + "SWORD AND AXE");
				}
				case "REJUVENATION" -> {
					materialLore.add(g + "Regain you health after");
					materialLore.add(g + "Sleeping peacefully");
					materialLore.add("");
					materialLore.add(g + "Applicable to:");
					materialLore.add(g + "CHEST PLATE");
				}
				case "SOUL BOUND" -> {
					materialLore.add(g + "Allow an item to be bound");
					materialLore.add(g + "to your soul that even death");
					materialLore.add(g + "won't be able to take it away!");
					materialLore.add("");
					materialLore.add(g + "Applicable to:");
					materialLore.add(g + "ALL EQUIPMENT");
				}
				case "LIGHTNING STRIKE" -> {
					materialLore.add(g + "A chance to hit a target with");
					materialLore.add(g + "a lightning strike dealing an");
					materialLore.add(g + "additional 5 more damage per level");
					materialLore.add("");
					materialLore.add(g + "Applicable to:");
					materialLore.add(g + "BOW");
				}
				case "EXPLOSION" -> {
					materialLore.add(g + "A chance to make the target");
					materialLore.add(g + "explode after hitting them");
					materialLore.add("");
					materialLore.add(g + "Applicable to:");
					materialLore.add(g + "BOW");
				}
				case "ARISE" -> {
					materialLore.add(g + "A chance to summon your follower,");
					materialLore.add(g + "1 follower per level");
					materialLore.add("");
					materialLore.add(g + "Applicable to:");
					materialLore.add(g + "HELMET");
				}
				case "GRAVITY" -> {
					materialLore.add(g + "Summon a gravitational pull on");
					materialLore.add(g + "your target, every hostile mobs");
					materialLore.add(g + "in a 15 block radius will be");
					materialLore.add(g + "pulled towards your target.");
					materialLore.add("");
					materialLore.add(g + "Applicable to:");
					materialLore.add(g + "BOW");
				}
				case "RAGE" -> {
					materialLore.add(g + "When taking a lethal damage");
					materialLore.add(g + "there will be a chance that");
					materialLore.add(g + "you will enter rage mode,");
					materialLore.add(g + "Increasing your health, damage");
					materialLore.add(g + "and speed for 5 seconds, depending");
					materialLore.add(g + "on the number of enemy around you.");
					materialLore.add("");
					materialLore.add(g + "Applicable to:");
					materialLore.add(g + "CHEST PLATE");
				}
				case "FULL COUNTER" -> {
					materialLore.add(g + "A chance to reflect the damage");
					materialLore.add(g + "to your attacker with twice");
					materialLore.add(g + "the ammount");
					materialLore.add("");
					materialLore.add(g + "Applicable to:");
					materialLore.add(g + "LEGGINGS");
				}
			}
		}
		materialLore.add(" ");
		materialLore.add(materialRarity);
	}
	
	public void setMaterialName() {
		if (materialType.equalsIgnoreCase("Normal")) {
			ChatColor color = ChatColor.GRAY;
			if (mobLevel > 10 && mobLevel <= 20) color = ChatColor.BLUE;
			if (mobLevel > 20 && mobLevel <= 30) color = ChatColor.DARK_PURPLE;
			if (mobLevel > 30 && mobLevel <= 40) color = ChatColor.GOLD;
			if (mobLevel > 40 && mobLevel <= 50) color = ChatColor.RED;
			if (mobLevel > 50) color = ChatColor.DARK_RED;
			materialName = color + "Enchant Scripture";
		}
		if (materialType.equalsIgnoreCase("Custom")) materialName = ChatColor.GOLD + "Forbidden Scripture";
		if (materialType.equalsIgnoreCase("Skill")) materialName = ChatColor.DARK_RED + "Forgotten Scripture";
	}
	
	public void setMaterialRarity() {
		ChatColor color = ChatColor.DARK_GRAY;
		if (mobLevel <= 10 && mobLevel >= 5) materialRarity = color + "Normal Scroll";
		if (mobLevel > 10 && mobLevel <= 20) materialRarity = color + "Rare Scroll";
		if (mobLevel > 20 && mobLevel <= 30) materialRarity = color + "Very Rare Scroll";
		if (mobLevel > 30 && mobLevel <= 40) materialRarity = color + "Legendary Scroll";
		if (mobLevel > 40 && mobLevel <= 50) materialRarity = color + "Ancient Scroll";
		if (mobLevel > 50) materialRarity = color + "ᒷ⊣ꖎ||!¡⍑ᓵ╎ᒷ";
	}

	public String getMaterialName() {
		return materialName;
	}
	
	public List<String> getMaterial() {
		return materialLore;
	}
	
	public int getMobLevel() {
		return mobLevel;
	}
}
