package com.peepersoak.adventurecraftcore.utils;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.combat.levelmobs.MobFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {

    private static final Random rand = new Random();

    public static String color(String msg) {
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
}
