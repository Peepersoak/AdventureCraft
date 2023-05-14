package com.peepersoak.adventurecraftcore.items.arrows;

import com.peepersoak.adventurecraftcore.utils.StringPath;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class ArrowEvents implements Listener {

    private final HashMap<UUID, String> playerShot = new HashMap<>();

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Arrow arrow)) return;
        if (!(e.getEntity() instanceof LivingEntity target)) return;
        if (!(arrow.getShooter() instanceof Player player)) return;
        if (!playerShot.containsKey(player.getUniqueId())) return;
        String type = playerShot.get(player.getUniqueId());
        if (type.equalsIgnoreCase("Lava") || type.equalsIgnoreCase("water")) return;
        castEffect(playerShot.get(player.getUniqueId()), target, player, target.getLocation(), arrow);
    }

//    @EventHandler
//    public void onBlockHit(ProjectileHitEvent e) {
//        if (!(e.getEntity() instanceof Arrow arrow)) return;
//        if (!(arrow.getShooter() instanceof Player player)) return;
//        if (!playerShot.containsKey(player.getUniqueId())) return;
//        String type = playerShot.get(player.getUniqueId());
//        if (type.equalsIgnoreCase("Lava") || type.equalsIgnoreCase("water")) {
//            if (e.getHitBlock() == null) return;
//            Block block = e.getHitBlock();
//            if (getRelativeAirBlock(block) == null) return;
//            Block airBlock = getRelativeAirBlock(block);
//            Location location = airBlock.getLocation();
//            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
//            castEffect(playerShot.get(player.getUniqueId()), null, player, location, arrow);
//        }
//    }

    @EventHandler
    public void onShootEvent(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (Objects.requireNonNull(e.getConsumable()).getType() != Material.ARROW) return;
        String arrowType = getArrowType(e.getConsumable());
        if (arrowType == null) return;
        e.setConsumeItem(true);
        if (arrowType.equalsIgnoreCase("flying")) {
            if (e.getProjectile() instanceof Arrow arrow) {
                castEffect(arrowType, null, player, null, arrow);
                return;
            }
        }
        playerShot.put(player.getUniqueId(), arrowType);
    }

    public void castEffect(String typeRaw, LivingEntity target, Player player, Location location, Arrow arrow) {
        ArrowEffects effect = new ArrowEffects(target, player);
        ArrowType type = ArrowType.valueOf(typeRaw);
        switch (type) {
            case LIGHTNING -> effect.strikeLightning();
            case EXPLOSIVE -> effect.explosion();
            case GRAVITY -> effect.gravity();
            case HEADSHOT -> effect.headShot();
            case BURROW -> effect.burrow();
            case POISON -> effect.poison();
            case PORTAL -> effect.portal();
            case SHOTGUN -> effect.shotgun();
            case FEATHER -> effect.feather();
            case WEB -> effect.web(location);
            case FLYING -> effect.flying(arrow);
        }
        playerShot.remove(player.getUniqueId());
    }

    public Block getRelativeAirBlock(Block block) {
        int rad = 1;
        for (int x = -rad; x <= rad ; x++) {
            for (int z = -rad; z <= rad ; z++) {
                for (int y = -rad; y <= rad ; y++) {
                    Block b = block.getRelative(x, y, z);
                    if (b.getType() == Material.AIR) {
                        if (y == 0) {
                            if (x != 0 && z != 0) continue;
                            return b;
                        } else {
                            if (x == 0 && z == 0) {
                                return b;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private String getArrowType(ItemStack arrow) {
        ItemMeta meta = arrow.getItemMeta();
        if (meta == null) return null;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if (data.has(StringPath.CUSTOM_ARROW, PersistentDataType.STRING)) {
            return data.get(StringPath.CUSTOM_ARROW, PersistentDataType.STRING);
        }
        return null;
    }
}
