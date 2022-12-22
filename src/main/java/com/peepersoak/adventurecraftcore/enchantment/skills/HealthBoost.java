package com.peepersoak.adventurecraftcore.enchantment.skills;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.achievement.AchievementHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class HealthBoost implements Listener {

	public HealthBoost() {
		runUpdater();
	}
	private final AchievementHandler achievement = new AchievementHandler();


//	@EventHandler
//	public void oninvClose(InventoryCloseEvent e) {
//		Inventory inv = e.getInventory();
//		if (inv.getType() != InventoryType.CRAFTING) return;
//		Player player = (Player) e.getPlayer();
//
//		for (ItemStack armor : player.getInventory().getArmorContents()) {
//			if (armor == null || Objects.requireNonNull(armor.getItemMeta()).getLore() == null) continue;
//			Skill skill = new Skill();
//			skill.setItem(armor);
//			if (skill.getLoreName().contains("HEALTH BOOST")) {
//				int level = skill.getLoreMap().get("HEALTH BOOST");
//				setHealth(player, level);
//				return;
//			}
//		}
//		resetHealth(player);
//	}

	private final Skill skill = new Skill();
	private void runUpdater() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					boolean found = false;
					for (ItemStack armor : player.getInventory().getArmorContents()) {
						if (armor == null || Objects.requireNonNull(armor.getItemMeta()).getLore() == null) continue;
						skill.setItem(armor);
						if (skill.getLoreName() != null && skill.getLoreName().contains("HEALTH BOOST")) {
							int level = skill.getLoreMap().get("HEALTH BOOST");

							AttributeInstance health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
							double newHealth = (level + 1) * 20;
							assert health != null;
							health.setBaseValue(newHealth);
							achievement.addGodAmongMen(player.getName());
							player.setHealthScale(20);

//							setHealth(player, level);
							found = true;
							break;
						}
					}
					if (!found) resetHealth(player);
				}
			}
		}.runTaskTimer(AdventureCraftCore.getInstance(), 0, 20);
	}

//	public void setHealth(Player player, int level) {
//		AttributeInstance health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
//		assert health != null;
//		if (health.getBaseValue() <= 20) {
//			double newHealth = (level + 1) * 20;
//			health.setBaseValue(newHealth);
//			player.sendMessage(ChatColor.GOLD + "You increase your health to " + ChatColor.GREEN + (newHealth / 2) + " hearts!");
//
//			achievement.addGodAmongMen(player.getName());
//		}
//	}
	
	public void resetHealth(Player player) {
		AttributeInstance health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		assert health != null;
		if (health.getBaseValue() > 20) {
			health.setBaseValue(20.0);
			player.sendMessage(ChatColor.RED + "Your health points has been reset");
		}
	}
}
