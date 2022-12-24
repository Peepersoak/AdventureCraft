package com.peepersoak.adventurecraftcore.combat.events;

import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EndermanEvents implements Listener {

    private final Integer[] RADIUS = {-5,-4,-3,-2,-1,0,1,2,3,4,5};

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!(e.getDamager() instanceof Enderman)) return;
        if (Utils.getRandom(100) < 25) {
            randomTeleport(player);
        }
    }

    private void randomTeleport(Player player) {
        final List<Integer> arr = Arrays.asList(RADIUS);
        final Location pLoc = player.getLocation();
        Collections.shuffle(arr);
        int x = arr.get(0);
        Collections.shuffle(arr);
        int z = arr.get(0);
        Block block = pLoc.getBlock();
        if (pLoc.getWorld() == null) return;
        Location targetLocation = pLoc.getWorld().getHighestBlockAt(block.getRelative(x, 0,z).getLocation()).getLocation().add(0.5,1.5,0.5);
        if (!targetLocation.getBlock().getType().isAir()) return;
        if (!targetLocation.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) return;
        player.teleport(targetLocation);
        player.getWorld().playSound(targetLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }
}
