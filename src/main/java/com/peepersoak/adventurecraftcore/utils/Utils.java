package com.peepersoak.adventurecraftcore.utils;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.combat.levelmobs.MobFactory;
import com.peepersoak.adventurecraftcore.combat.levelmobs.zombie.variation.AggresiveVirusZombie;
import com.peepersoak.adventurecraftcore.combat.levelmobs.zombie.variation.BoomerZombie;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class Utils {

    private static final Random rand = new Random();

    public static String color(String msg) {
        if (msg == null || msg.equals("")) return "";
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static PersistentDataContainer getPDC(LivingEntity entity) {
        return entity.getPersistentDataContainer();
    }

    public static PersistentDataContainer getPDC(ItemMeta meta) {
        return meta.getPersistentDataContainer();
    }

    public static List<EntityType> getNonLevelledMobs() {
        List<EntityType> types = new ArrayList<>();
        types.add(EntityType.ENDER_DRAGON);
        types.add(EntityType.WITHER);
        types.add(EntityType.IRON_GOLEM);
        types.add(EntityType.SNOWMAN);
        types.add(EntityType.SLIME);
        types.add(EntityType.MAGMA_CUBE);
        return types;
    }

    public static int getRandom(int maxValue) {
        return rand.nextInt(maxValue) + 1;
    }

    public static int getRandom(int max, int min) {
        return rand.nextInt(max - min) + min;
    }

    public static double getRandomDouble(double max, double min) {
        return rand.nextDouble(max - min) + min;
    }

    public static void zombieLevelUp(Zombie zombie) {
        Integer xpGoal = Utils.getPDC(zombie).get(StringPath.MOB_XP_GOAL, PersistentDataType.INTEGER);
        if (xpGoal == null) return;

        Integer level = Utils.getPDC(zombie).get(StringPath.MOB_LEVEL_KEY, PersistentDataType.INTEGER);
        if (level == null) return;

        Integer currentXp = Utils.getPDC(zombie).get(StringPath.MOB_XP, PersistentDataType.INTEGER);
        if (currentXp == null) currentXp = 0;

        currentXp++;
        if (currentXp <= xpGoal) {
            Utils.getPDC(zombie).set(StringPath.MOB_XP, PersistentDataType.INTEGER, currentXp);
            return;
        }

        level++;
        Utils.getPDC(zombie).set(StringPath.MOB_LEVEL_KEY, PersistentDataType.INTEGER, level);
        Utils.getPDC(zombie).set(StringPath.MOB_XP, PersistentDataType.INTEGER, 0);
        Utils.getPDC(zombie).set(StringPath.MOB_XP_GOAL, PersistentDataType.INTEGER, xpGoal + 5);
        new MobFactory(level, zombie);
    }

    public static int getDayPassed() {
        World world = Bukkit.getWorld("world");
        if (world == null) return 0;
        return (int) (world.getFullTime() / 24000);
    }

    public static void sendSyncMessage(Player player, String message) {
        Bukkit.getScheduler().runTask(AdventureCraftCore.getInstance(), () -> player.sendMessage(color(message)));
    }

    public static int getRemainingCooldown(long cooldown) {
        long current = System.currentTimeMillis();
        if (cooldown <= current) return 0;
        return (int) ((cooldown - current) / 1000);
    }

    public static void dropItem(ItemStack item, int chance, Location location) {
        if (getRandom(100) > chance) return;
        if (location == null) return;
        if (location.getWorld() == null) return;
        location.getWorld().dropItemNaturally(location, item);
    }

    public static Object deserialized(String data) {
        try {
            byte[] raw = Base64.getDecoder().decode(data);
            ByteArrayInputStream is = new ByteArrayInputStream(raw);
            BukkitObjectInputStream bs = new BukkitObjectInputStream(is);
            return bs.readObject();
        } catch (IOException | ClassNotFoundException e) {
            AdventureCraftCore.getInstance().getLogger().warning("Failed to deserialized data!");
        }
        return null;
    }

    public static String serialized(Object object) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            BukkitObjectOutputStream bs = new BukkitObjectOutputStream(os);
            bs.writeObject(object);
            bs.flush();
            byte[] data = os.toByteArray();
            return Base64.getEncoder().encodeToString(data);
        } catch (IOException e) {
            AdventureCraftCore.getInstance().getLogger().warning("Failed to serialized data!");
        }
        return null;
    }

    public static void spawnRandomZombie(Location location) {
        if (location == null) return;
        ServerLevel world = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();
        if (Utils.getRandom(100, 1) < 15) {
            world.addFreshEntityWithPassengers(new BoomerZombie(location));
            return;
        }
        world.addFreshEntityWithPassengers(new AggresiveVirusZombie(location));
    }

    public static boolean checkWGState(Entity entity, StateFlag flag) {
        com.sk89q.worldedit.util.Location location = new com.sk89q.worldedit.util.Location(BukkitAdapter.adapt(entity.getWorld()), entity.getLocation().getBlockX(), entity.getLocation().getBlockY(), entity.getLocation().getBlockZ());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(location);

        return set.testState(null, flag);
    }

    public static int getMobLevelThreshold(Entity entity, IntegerFlag flag) {
        com.sk89q.worldedit.util.Location location = new com.sk89q.worldedit.util.Location(BukkitAdapter.adapt(entity.getWorld()), entity.getLocation().getBlockX(), entity.getLocation().getBlockY(), entity.getLocation().getBlockZ());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(location);

        Integer customThreshold = set.queryValue(null, flag);
        int defaultThreshold = AdventureCraftCore.getInstance().getConfig().getInt(ConfigPath.DISTANCE_THRESHOLD);

        return customThreshold == null ? defaultThreshold : customThreshold;
    }

    public static int getWorldGuardValue(Entity entity, IntegerFlag flag) {
        com.sk89q.worldedit.util.Location location = new com.sk89q.worldedit.util.Location(BukkitAdapter.adapt(entity.getWorld()), entity.getLocation().getBlockX(), entity.getLocation().getBlockY(), entity.getLocation().getBlockZ());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(location);

        Integer customValue = set.queryValue(null, flag);

        return customValue != null ? customValue : -1;
    }

    public static void setProjectileDamage(LivingEntity mob, EntityDamageByEntityEvent e) {
        Integer level = Utils.getPDC(mob).get(StringPath.MOB_LEVEL_KEY, PersistentDataType.INTEGER);
        level = level != null ? level : 0;

        double additionDamage = AdventureCraftCore.getInstance().getConfig().getDouble(ConfigPath.DAMAGE_MULTIPLIER) * level;

        if (mob instanceof Ghast) {
            e.setDamage(e.getDamage() + additionDamage);
        }
        else if (mob instanceof Skeleton) {
            e.setDamage(e.getDamage() + additionDamage);
        }
        else if (mob instanceof Blaze) {
            e.setDamage(e.getDamage() + additionDamage);
        }
    }

    public static void giveItemToPlayer(ItemStack item, Player player) {
        HashMap<Integer, ItemStack> items = player.getInventory().addItem(item);
        for (ItemStack i : items.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), i);
        }
    }
}
