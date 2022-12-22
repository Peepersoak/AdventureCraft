package com.peepersoak.adventurecraftcore.enchantment.skills;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class SpeedBoost implements Listener {
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent e) {
		Inventory inv = e.getInventory();
		if (inv.getType() == InventoryType.CRAFTING) {
			Player player = (Player) e.getPlayer();
			ItemStack boots = player.getInventory().getHelmet();
			
			if (boots != null && Objects.requireNonNull(boots.getItemMeta()).getLore() != null) {
				Skill skill = new Skill();
				skill.setItem(boots);
				if (skill.getLoreName() != null && skill.getLoreName().contains("SPEED")) {
					if (player.getWalkSpeed() != 0.8F) {
						player.setWalkSpeed(0.8F);
						player.sendMessage(ChatColor.GOLD + "Your speed has increase!");
					}
					return;
				}
			}
			
			if (player.getWalkSpeed() != 0.2F) {
				player.setWalkSpeed(0.2F);
				player.sendMessage(ChatColor.GOLD + "Your speed is now back to normal");
			}
		}
	}
}
