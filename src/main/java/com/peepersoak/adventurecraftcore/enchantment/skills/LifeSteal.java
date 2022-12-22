package com.peepersoak.adventurecraftcore.enchantment.skills;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.enchantment.SkillChance;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Random;

public class LifeSteal implements Listener {
	
	private final AdventureCraftCore plugin = AdventureCraftCore.getInstance();

	@EventHandler
	public void onDamageEvent(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof LivingEntity) {
			if (e.getDamager() instanceof Player player) {
				Skill skill = new Skill();
				ItemStack sword = player.getInventory().getItemInMainHand();
				String swordType = sword.getType().toString().toLowerCase();
				if (swordType.contains("sword") || swordType.contains("axe")) {
					if (Objects.requireNonNull(sword.getItemMeta()).getLore() == null) return;
					skill.setItem(sword);
					if (skill.getLoreName() != null && skill.getLoreName().contains("LIFE STEAL")) {
						if (castSkill()) {
							int level = skill.getLoreMap().get("LIFE STEAL");
							double damage = e.getFinalDamage();
							double health = (damage/5) + level;
							double playerHealth = player.getHealth() + health;
							player.setHealth(Math.min(playerHealth, Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue()));
							player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_1, 5.0F, 1.0F);
						}
					}
				}
			}
		}
	}
	
	public boolean castSkill() {
		Random rand = new Random();
		int random = rand.nextInt(100) + 1;
		int chance = SkillChance.LIFESTEAL;
		return random <= chance;
	}
}
