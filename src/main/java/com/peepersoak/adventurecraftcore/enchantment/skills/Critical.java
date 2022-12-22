package com.peepersoak.adventurecraftcore.enchantment.skills;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.enchantment.SkillChance;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Critical implements Listener {
	
	private final AdventureCraftCore plugin = AdventureCraftCore.getInstance();

	@EventHandler
	public void onDamageEvent(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof LivingEntity) {
			if (e.getDamager() instanceof Player) {
				Skill skill = new Skill();
				Player player = (Player) e.getDamager();
				ItemStack sword = player.getInventory().getItemInMainHand();
				if (sword.getType().toString().toLowerCase().contains("sword") || sword.getType().toString().toLowerCase().contains("axe")) {
					if (sword.getItemMeta().getLore() == null) return;
					skill.setItem(sword);
					if (skill.getLoreName() != null && skill.getLoreName().contains("CRITICAL")) {
						if (castSkill()) {
							int level = skill.getLoreMap().get("CRITICAL");
							double damage = e.getFinalDamage() + (level * 5);
							e.setDamage(damage);
							player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 5.0F, 1.0F);
						}
					}
				}
			}
		}
	}
	
	public boolean castSkill() {
		Random rand = new Random();
		int random = rand.nextInt(100) + 1;
		int chance = SkillChance.CRITICAL;
		return random <= chance;
	}
}
