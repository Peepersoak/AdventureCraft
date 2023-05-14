package com.peepersoak.adventurecraftcore.combat.levelmobs.warden;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

public class WardenEvent implements Listener {

    private final Material[] unbreakable = {Material.BEDROCK, Material.REINFORCED_DEEPSLATE, Material.END_PORTAL_FRAME};
    private final NamespacedKey INSIDE_DEEP_DARK = new NamespacedKey(AdventureCraftCore.getInstance(), "InsideDeepDark");

    @EventHandler
    public void onSonicBoom(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Warden warden)) return;
        if (!(e.getEntity() instanceof LivingEntity target)) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.SONIC_BOOM) return;

        Location loc1 = warden.getEyeLocation();
        Location loc2 = target.getEyeLocation();
        int radius = 2; // the radius of the cylinder

        Vector dir = loc2.toVector().subtract(loc1.toVector()).normalize();
        double distance = loc1.distance(loc2);
        Location current = loc1.clone();

        for (double i = 0; i <= distance; i += 0.5) {
            current = current.add(dir.clone().multiply(0.1));
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (Math.sqrt(x*x + y*y + z*z) <= radius) {
                            Location loc = current.clone().add(x, y, z);
                            Block block = loc.getBlock();

                            Material material = block.getType();
                            if (material == Material.AIR ||
                                    material == Material.CAVE_AIR ||
                                    material.isAir() ||
                                    !material.isSolid() ||
                                    List.of(unbreakable).contains(material)) continue;

                            block.breakNaturally(new ItemStack(Material.IRON_PICKAXE));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.getLocation().getBlock().getBiome() != Biome.DEEP_DARK) {
            if (player.getPersistentDataContainer().has(INSIDE_DEEP_DARK, PersistentDataType.INTEGER)) {
                player.getPersistentDataContainer().remove(INSIDE_DEEP_DARK);
            }
            return;
        }

        // This means they are inside deep dark
        player.setGliding(false);

        if (player.getPersistentDataContainer().has(INSIDE_DEEP_DARK, PersistentDataType.INTEGER)) return;
       player.getPersistentDataContainer().set(INSIDE_DEEP_DARK, PersistentDataType.INTEGER, 1);
    }

    @EventHandler
    public void onFly(EntityToggleGlideEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (player.getPersistentDataContainer().has(INSIDE_DEEP_DARK, PersistentDataType.INTEGER)) {
            e.setCancelled(true);
            player.sendMessage(Utils.color("&cFlying has been disabled!"));
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        if (!(e.getEntity() instanceof Warden warden)) return;

        List<Entity> entities = warden.getNearbyEntities(40, 40, 40);
        for (Entity ent : entities) {
            if (ent instanceof Player player) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3600, 4));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3600, 4));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 3600, 2));
            }
        }
    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() != Material.MILK_BUCKET) return;
        if (!e.getPlayer().getPersistentDataContainer().has(INSIDE_DEEP_DARK, PersistentDataType.INTEGER)) return;
        e.getPlayer().sendMessage(Utils.color("&cMilk has been disabled"));
        e.setCancelled(true);
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof EnderPearl pearl)) return;
        if (!(pearl.getShooter() instanceof Player player)) return;
        if (!player.getPersistentDataContainer().has(INSIDE_DEEP_DARK, PersistentDataType.INTEGER)) return;
        player.sendMessage(Utils.color("&cEnder Pearl has been disabled"));
        e.setCancelled(true);
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Warden warden)) return;
        Player player = warden.getKiller();

        if (player == null) return;
        Location loc = warden.getLocation();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp() || p.getName().equalsIgnoreCase("Maximma")) {
                p.sendMessage(Utils.color("&6" + player.getName() + " &eKilled a Warden at " + loc.getBlockX() + "x " + loc.getBlockY() + "y " + loc.getBlockZ() + "z"));
            }
        }
    }

    private final NamespacedKey shriekerKey = new NamespacedKey(AdventureCraftCore.getInstance(), "ShriekerTempKey");
    @EventHandler
    public void onPickUP(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (player.getPersistentDataContainer().has(shriekerKey, PersistentDataType.INTEGER)) {
            Integer count = player.getPersistentDataContainer().get(shriekerKey, PersistentDataType.INTEGER);
            if (count != null) {
                count++;
                player.getPersistentDataContainer().set(shriekerKey, PersistentDataType.INTEGER, count);
            }
        } else {
            player.getPersistentDataContainer().set(shriekerKey, PersistentDataType.INTEGER, 1);
        }
    }

    @EventHandler
    public void onCheck(PlayerInteractEvent e) {
        if (!e.getPlayer().isOp()) return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (!e.hasItem() || e.getItem().getType() != Material.BARRIER) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getPersistentDataContainer().has(shriekerKey, PersistentDataType.INTEGER)) {
                Integer count = p.getPersistentDataContainer().get(shriekerKey, PersistentDataType.INTEGER);
                if (count != null) {
                    e.getPlayer().sendMessage(Utils.color("&6" + p.getName() + " &ehas &b" + count + " &eshrieker"));
                }
            }
        }
    }
}
