package com.peepersoak.adventurecraftcore.world;

import com.peepersoak.adventurecraftcore.enchantment.ItemFactory;
import com.peepersoak.adventurecraftcore.items.arrows.ArrowFactory;
import com.peepersoak.adventurecraftcore.items.scrolls.ScrollFactory;
import com.peepersoak.adventurecraftcore.items.wards.WardFactory;
import com.peepersoak.adventurecraftcore.utils.Flags;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;


public class DropItem implements Listener {

    private final ScrollFactory scrollFactory = new ScrollFactory();
    private final WardFactory wardFactory = new WardFactory();
    private final ArrowFactory arrowFactory = new ArrowFactory();
    private final ItemFactory itemFactory = new ItemFactory();

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Monster monster)) return;

        Integer level = Utils.getPDC(monster).get(StringPath.MOB_LEVEL_KEY, PersistentDataType.INTEGER);
        if (level == null) return;

        Location location = monster.getLocation();

        if (!Utils.checkWGState(monster, Flags.ALLOW_DROPS)) return;

        int scrollChance = Utils.getWorldGuardValue(monster, Flags.SCROLL_CHANCE);
        int wardChance = Utils.getWorldGuardValue(monster, Flags.WARD_CHANCE);
        int arrowChance = Utils.getWorldGuardValue(monster, Flags.ARROW_CHANCE);

        Utils.dropItem(scrollFactory.createScroll(), scrollChance == -1 ? 5 : scrollChance, location);
        Utils.dropItem(wardFactory.createWard(), wardChance == -1 ? 5 : wardChance, location);
        Utils.dropItem(arrowFactory.createArrow(), arrowChance == -1 ? 5 : arrowChance, location);

        itemFactory.setPaper(monster);
        ItemStack paper = itemFactory.createPaper();
        if (paper == null) return;
        Utils.dropItem(paper, 5, location);
    }
}
