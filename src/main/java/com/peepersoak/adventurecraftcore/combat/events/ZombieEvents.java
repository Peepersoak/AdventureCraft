package com.peepersoak.adventurecraftcore.combat.events;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.combat.levelmobs.zombie.ZombieType;
import com.peepersoak.adventurecraftcore.combat.levelmobs.zombie.variation.AggresiveVirusZombie;
import com.peepersoak.adventurecraftcore.combat.levelmobs.zombie.variation.BoomerZombie;
import com.peepersoak.adventurecraftcore.enchantment.skills.Skill;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ZombieEvents implements Listener {

    private final Random rand = new Random();
    private final NamespacedKey key = new NamespacedKey(AdventureCraftCore.getInstance(), "PlayerZombie");

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) return;
        if (e.getEntity() instanceof Zombie zombie) {
            if (zombie.getType() == EntityType.ZOMBIE) {
                if (Utils.getPDC(zombie).has(StringPath.CUSTOM_ZOMBIE, PersistentDataType.STRING)) return;
                spawnRandomZombie(zombie.getLocation());
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Zombie zombie) {
            if (!Utils.getPDC(zombie).has(StringPath.CUSTOM_ZOMBIE, PersistentDataType.STRING)) return;
            String type = Utils.getPDC(zombie).get(StringPath.CUSTOM_ZOMBIE, PersistentDataType.STRING);
            if (type == null) return;
            if (!type.equalsIgnoreCase(ZombieType.BOOMER_ZOMBIE)) return;
            EntityDamageEvent.DamageCause cause = e.getCause();
            if (cause ==EntityDamageEvent.DamageCause.ENTITY_ATTACK
                    || cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK
                    || cause == EntityDamageEvent.DamageCause.PROJECTILE
                    || cause == EntityDamageEvent.DamageCause.THORNS) {
                explodeZombie(zombie);
            }
        }
    }

    @EventHandler
    public void onZombieKill(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        if (entity instanceof Player) return;
        if (!(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent event)) return;
        if (event.getDamager() instanceof Zombie zombie) {
            if (!Utils.getPDC(zombie).has(StringPath.CUSTOM_ZOMBIE, PersistentDataType.STRING)) return;
            Location loc = entity.getLocation();
            spawnRandomZombie(loc);
            Utils.zombieLevelUp(zombie);
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Zombie)) return;
        if (!(e.getEntity() instanceof Player player)) return;

        boolean shouldSnatch = rand.nextInt(100) + 1 <= 10;
        if (!shouldSnatch) return;

        List<ItemStack> items = new ArrayList<>();
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item == null || item.getType() == Material.AIR) continue;
            items.add(item);
        }

        if (items.isEmpty()) return;

        ItemStack item = items.get(rand.nextInt(items.size()));

        Skill skill = new Skill();
        skill.setItem(item);
        if (skill.getLoreName().contains("SOUL BOUND")) return;

        String material = item.getType().toString().toLowerCase();
        ItemStack air = new ItemStack(Material.AIR);

        if (material.contains("helmet")) {
            player.getInventory().setHelmet(air);
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        } else if (material.contains("chestplate")) {
            player.getInventory().setChestplate(air);
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        } else if (material.contains("leggings")) {
            player.getInventory().setLeggings(air);
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        } else if (material.contains("boots")) {
            player.getInventory().setBoots(air);
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();

        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();

        Location location = player.getLocation();

        String name = Utils.color("&c" + player.getName());

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);
        }
        skull.setItemMeta(meta);

        Zombie zombie = (Zombie) player.getWorld().spawnEntity(location, EntityType.ZOMBIE);
        zombie.getPersistentDataContainer().set(key, PersistentDataType.STRING, "PZombie");

        zombie.setCustomName(name);
        zombie.setCustomNameVisible(true);
        zombie.setCanPickupItems(false);
        zombie.setRemoveWhenFarAway(true);

        Objects.requireNonNull(zombie.getEquipment()).setHelmet(skull);
        zombie.getEquipment().setChestplate(chestplate);
        zombie.getEquipment().setLeggings(leggings);
        zombie.getEquipment().setBoots(boots);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Zombie zombie)) return;
        if (zombie.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            e.getDrops().clear();
        }
    }

    private void spawnRandomZombie(Location location) {
        if (location == null) return;
        ServerLevel world = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();

        if (Utils.getRandom(100, 1) < 15) {
            world.addFreshEntityWithPassengers(new BoomerZombie(location));
            return;
        }

        world.addFreshEntityWithPassengers(new AggresiveVirusZombie(location));
    }

    private void explodeZombie(Zombie zombie) {
        Location location = zombie.getLocation();
        zombie.remove();
        zombie.getWorld().createExplosion(location, 4.0F);
    }
}
