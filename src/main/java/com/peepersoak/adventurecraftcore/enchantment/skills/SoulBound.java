package com.peepersoak.adventurecraftcore.enchantment.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SoulBound implements Listener {

	private final HashMap<Player, List<ItemStack>> playerSoulItem = new HashMap<>();

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		List<ItemStack> soulBoundItem = new ArrayList<>();
		Player player = e.getEntity();
		Skill skill = new Skill();
		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null)
				continue;
			if (Objects.requireNonNull(item.getItemMeta()).getLore() == null)
				continue;
			skill.setItem(item);
			if (skill.getLoreName() != null && skill.getLoreName().contains("SOUL BOUND")) {
				soulBoundItem.add(item);
				e.getDrops().remove(item);
			}
		}
		if (soulBoundItem.isEmpty())
			return;
		playerSoulItem.put(player, soulBoundItem);

	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		if (playerSoulItem.containsKey(player)) {
			for (ItemStack item : playerSoulItem.get(player)) {
				player.getInventory().addItem(item);
			}
		}
	}
}
