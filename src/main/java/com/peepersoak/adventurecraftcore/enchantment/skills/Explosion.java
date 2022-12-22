package com.peepersoak.adventurecraftcore.enchantment.skills;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.enchantment.SkillChance;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Random;

public class Explosion implements Listener {
	
	private final AdventureCraftCore plugin = AdventureCraftCore.getInstance();
	
	@EventHandler
	public void onDamageEvent(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof LivingEntity) {
			if (e.getDamager() instanceof Arrow arrow) {
				if (arrow.getShooter() instanceof Player player) {
					Skill skill = new Skill();
					LivingEntity target = (LivingEntity) e.getEntity();
					ItemStack item = player.getInventory().getItemInMainHand();
					if (item.getType().toString().toLowerCase().contains("bow")) {
						if (Objects.requireNonNull(item.getItemMeta()).getLore() == null) return;
						skill.setItem(item);
						if (skill.getLoreName() != null && skill.getLoreName().contains("EXPLOSION")) {
							if (castSkill()) {
								int level = skill.getLoreMap().get("EXPLOSION");
								Location loc = target.getLocation();
								Objects.requireNonNull(loc.getWorld()).createExplosion(loc, (4F + level), true, false);
							}
						}
					}
				}
			}
		}
	}
	
	public boolean castSkill() {
		Random rand = new Random();
		int random = rand.nextInt(100) + 1;
		int chance = SkillChance.EXPLOSION;
		return random <= chance;
	}
}
