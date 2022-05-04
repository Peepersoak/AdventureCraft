package com.peepersoak.adventurecraftcore.modelengine;

import com.peepersoak.adventurecraftcore.items.arrows.ArrowFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class SampleClass implements Listener {

    private final ArrowFactory factory = new ArrowFactory();

    @EventHandler
    public void onAttack(PlayerInteractEvent e) {
        e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), factory.createArrow());
    }
}
