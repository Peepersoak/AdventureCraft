package com.peepersoak.adventurecraftcore.enchantment.skills;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class Rejuvination implements Listener {

	@EventHandler
	public void onBedExit(PlayerBedLeaveEvent e) {
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getChestplate();
		if (item != null && Objects.requireNonNull(item.getItemMeta()).getLore() != null) {
			Skill skill = new Skill();
			skill.setItem(item);
			if (skill.getLoreName() != null && skill.getLoreName().contains("REJUVENATION")) {
				player.sendMessage(ChatColor.GREEN + "You slept peacefully, your health will now regenerate!");
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 3));
			}
		}
	}
}
