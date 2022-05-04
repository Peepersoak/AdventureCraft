package com.peepersoak.adventurecraftcore.world;

import com.peepersoak.adventurecraftcore.items.arrows.ArrowFactory;
import com.peepersoak.adventurecraftcore.items.scrolls.ScrollFactory;
import com.peepersoak.adventurecraftcore.items.wards.WardFactory;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

public class DropItem implements Listener {

    private final ArrowFactory arrowFactory = new ArrowFactory();
    private final ScrollFactory scrollFactory = new ScrollFactory();
    private final WardFactory wardFactory = new WardFactory();

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Monster monster)) return;
        Integer level = Utils.getPDC(monster).get(StringPath.MOB_LEVEL_KEY, PersistentDataType.INTEGER);
        if (level == null || level < 5) return;

        Location location = e.getEntity().getLocation();
        Utils.dropItem(arrowFactory.createArrow(), 5, location);
        Utils.dropItem(scrollFactory.createScroll(), 10, location);
        Utils.dropItem(wardFactory.createWard(), 10, location);
    }
}
