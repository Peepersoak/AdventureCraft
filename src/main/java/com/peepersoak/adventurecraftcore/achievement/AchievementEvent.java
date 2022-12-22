package com.peepersoak.adventurecraftcore.achievement;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.enchantment.Enchantments;
import com.peepersoak.adventurecraftcore.enchantment.crafting.Book;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class AchievementEvent implements Listener {

	private final AchievementHandler achievement = new AchievementHandler();
	private final AdventureCraftCore plugin = AdventureCraftCore.getInstance();
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		if (inv != null) {
			if (inv.getType() == InventoryType.ANVIL) {
				if (e.getSlot() == 2) {
					ItemStack item = e.getCurrentItem();
					assert item != null;
					if (item.getType() == Material.AIR) return;
					if (Objects.requireNonNull(item.getItemMeta()).getLore() == null) return;
					Player player = (Player) e.getWhoClicked();
					String playerName = player.getName();
					ItemStack[] items = e.getInventory().getContents();
					Book book = new Book();
					Enchantments enchants = new Enchantments();
					
					book.setItem(items[1]);
					enchants.setAllEnchantment();
					
					if (book.getIsNormal()) {
						Enchantment ench = book.getEnchantment();
						int maxLevel = ench.getMaxLevel();
						int bookLevel = book.getEnchantmentLevel();
						if (bookLevel > maxLevel) {
							achievement.addExceedingLimits(playerName);
						}
					} else {
						if (enchants.getSkills().contains(book.getEnchantName())) {
							achievement.addTouchingTheUnkown(playerName);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onMobKill(EntityDeathEvent e) {
		if (e.getEntity() instanceof Mob) {
			if (e.getEntity().getKiller() instanceof Player) {
				Mob mob = (Mob) e.getEntity();
				Player player = e.getEntity().getKiller();
				PersistentDataContainer data = mob.getPersistentDataContainer();
				int level = 1;
				if (data.has(new NamespacedKey(plugin, "level"), PersistentDataType.INTEGER)) {
					level = data.get(new NamespacedKey(plugin, "level"), PersistentDataType.INTEGER);
				}
				if (level >= 50) {
					achievement.addToForbiddenLan(player.getName().toString());
				}
				if (level >= 100) {
					achievement.addTheLegendary(player.getName().toString());
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityHit(ProjectileHitEvent e) {
		if (e.getEntity().getType() == EntityType.TRIDENT) {
			if (e.getHitEntity() != null && e.getHitEntity().getType() == EntityType.GHAST) {
				if (e.getEntity().getShooter() instanceof Player player) {
					String name = player.getName();
					achievement.addToMarshmallow(name);
				}
			}
		}
	}
}
