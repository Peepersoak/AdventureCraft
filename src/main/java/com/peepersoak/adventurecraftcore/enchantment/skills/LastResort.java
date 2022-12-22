package com.peepersoak.adventurecraftcore.enchantment.skills;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.enchantment.SkillChance;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class LastResort implements Listener {
	
	private final AdventureCraftCore plugin = AdventureCraftCore.getInstance();

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player player) {
			ItemStack item = player.getInventory().getHelmet();
			if (item != null && Objects.requireNonNull(item.getItemMeta()).getLore() != null) {
				Skill skill = new Skill();
				skill.setItem(item);
				if (skill.getLoreName() != null &&  skill.getLoreName().contains("LAST RESORT")) {
					double damage = e.getFinalDamage();
					double health = player.getHealth();
					if (damage >= health) {
						if (castSkill()) {
							e.setCancelled(true);
							player.addPotionEffects(getPotionEffect());
							player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 5.0F, 1.0F);
							player.sendMessage(ChatColor.AQUA + "Not Today!!");
						}
					}
				}
			}
		}
	}
	
	public List<PotionEffect> getPotionEffect() {
		List<PotionEffect> potionEffects = new ArrayList<>();
		potionEffects.add(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0));
		potionEffects.add(new PotionEffect(PotionEffectType.SPEED, 200, 3));
		potionEffects.add(new PotionEffect(PotionEffectType.ABSORPTION, 200, 4));
		potionEffects.add(new PotionEffect(PotionEffectType.REGENERATION, 200, 2));
		potionEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 1));
		return potionEffects;
	}
	
	public boolean castSkill() {
		Random rand = new Random();
		int random = rand.nextInt(100) + 1;
		int chance = SkillChance.LAST_RESORT;
		return random < chance;
	}
}
