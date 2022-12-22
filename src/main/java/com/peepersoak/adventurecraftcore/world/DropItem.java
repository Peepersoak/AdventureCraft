package com.peepersoak.adventurecraftcore.world;

import com.peepersoak.adventurecraftcore.enchantment.ItemFactory;
import com.peepersoak.adventurecraftcore.items.arrows.ArrowFactory;
import com.peepersoak.adventurecraftcore.items.scrolls.ScrollFactory;
import com.peepersoak.adventurecraftcore.items.wards.WardFactory;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class DropItem implements Listener {

    private final ScrollFactory scrollFactory = new ScrollFactory();
    private final WardFactory wardFactory = new WardFactory();
    private final ArrowFactory arrowFactory = new ArrowFactory();
    private final ItemFactory itemFactory = new ItemFactory();

    @EventHandler
    public void onDeath(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Monster monster)) return;
        if (!(e.getDamager() instanceof Player)) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        if (e.getDamage() >= monster.getHealth()) {
            Integer level = Utils.getPDC(monster).get(StringPath.MOB_LEVEL_KEY, PersistentDataType.INTEGER);
            if (level == null || level < 5) return;

            Location location = e.getEntity().getLocation();
            if (Objects.requireNonNull(location.getWorld()).getEnvironment() != World.Environment.NORMAL) return;
            Utils.dropItem(scrollFactory.createScroll(), 5, location);
            Utils.dropItem(wardFactory.createWard(), 5, location);
            Utils.dropItem(arrowFactory.createArrow(), 5, location);

            itemFactory.setPaper(monster);
            Utils.dropItem(itemFactory.createPaper(), 5, location);
        }
    }
}
