package com.peepersoak.adventurecraftcore.combat.levelmobs;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.utils.ConfigPath;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class MobFactory {

    public MobFactory(LivingEntity entity) {
        this.entity = entity;
        Location spawnLocation = entity.getLocation();
        Location worldSpawnLocation = entity.getWorld().getSpawnLocation();
        this.distance = (int) Math.floor(spawnLocation.distance(worldSpawnLocation));

        this.isBoss = Utils.getRandom(1000) < 5;
        this.isBaby = Utils.getRandom(100) < 15;
        this.xpGoal = Utils.getRandom(10, 5);

        setBaby();
        setLevel();
        setHealth();
        setDamage();
        setName();
        setAttribute();
        setXPGoal();

        addInviSkill();
        addAntiKnockBackSkill(false);
    }

    public MobFactory(LivingEntity entity, int level) {
        this.entity = entity;
        this.level = level;
        this.distance = 0;
        this.isBoss = Utils.getRandom(500) < 5;
        this.isBaby = Utils.getRandom(100) < 25;

        setBaby();
        setHealth();
        setDamage();
        setName();
        setAttribute();

        addInviSkill();
        addAntiKnockBackSkill(true);
    }

    public MobFactory(int level, LivingEntity entity) {
        this.entity = entity;
        this.distance = 0;
        this.isBoss = Math.random() < 0.00005;
        this.level = level;

        setHealth();
        setDamage();
        setName();
        setAttribute();
    }

    private final LivingEntity entity;
    private final int distance;
    private final boolean isBoss;
    private boolean isBaby;
    private int xpGoal;
    private int level;
    private double newHealth;
    private double newDamage;


    @SuppressWarnings("deprecation")
    private void setBaby() {
        if (!(entity instanceof Zombie zombie)) return;
        zombie.setBaby(isBaby);
    }

    private void setXPGoal() {
        Utils.getPDC(entity).set(StringPath.MOB_XP_GOAL, PersistentDataType.INTEGER, xpGoal);
    }

    private void setDamage() {
        if (entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null) {
            double damage = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).getBaseValue();
            this.newDamage = damage + AdventureCraftCore.getInstance().getConfig().getDouble(ConfigPath.DAMAGE_MULTIPLIER) * level;

            if (isBoss) {
                this.newDamage *= AdventureCraftCore.getInstance().getConfig().getDouble(ConfigPath.BOSS_MULTIPLIERE);
            }
        }
    }

    private void setHealth() {
        double health = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue();
        this.newHealth = health + (AdventureCraftCore.getInstance().getConfig().getDouble(ConfigPath.HEALTH_MULTIPLIER) * level);
        if (isBoss) {
            this.newHealth *= AdventureCraftCore.getInstance().getConfig().getDouble(ConfigPath.BOSS_MULTIPLIERE);
        }
    }

    private void setLevel() {
        com.sk89q.worldedit.util.Location location = new com.sk89q.worldedit.util.Location(BukkitAdapter.adapt(entity.getWorld()), entity.getLocation().getBlockX(), entity.getLocation().getBlockY(), entity.getLocation().getBlockZ());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(location);
        if (!set.testState(null, AdventureCraftCore.LEVEL_MOBS)) {
            this.level = 0;
            return;
        }

        int threshold = AdventureCraftCore.getInstance().getConfig().getInt(ConfigPath.DISTANCE_THRESHOLD);

        if (entity.getWorld().getEnvironment() == World.Environment.NETHER) {
            threshold /= 8;
        }

        this.level = distance / threshold;

        int y = entity.getLocation().getBlockY();
        if (y < 63) {
            this.level += (63 - y) / 30;
        }

        Utils.getPDC(entity).set(StringPath.MOB_LEVEL_KEY, PersistentDataType.INTEGER, level);
    }

    private void setName() {
        if (this.level <= 0) return;

        String name = Utils.color("&cLVL &6" + level + " &c" + entity.getType());

        if (isBoss) {
            name += " BOSS";
        }

        entity.setCustomName(name);
    }

    private void setAttribute() {
        if (newHealth > AdventureCraftCore.getInstance().getConfig().getInt(ConfigPath.MAX_HEALTH)) {
            newHealth = AdventureCraftCore.getInstance().getConfig().getInt(ConfigPath.MAX_HEALTH);
        }

        if (entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null) {
            if (newDamage > AdventureCraftCore.getInstance().getConfig().getInt(ConfigPath.MAX_DAMAGE)) {
                newDamage = AdventureCraftCore.getInstance().getConfig().getInt(ConfigPath.MAX_DAMAGE);
            }
        }

        if (entity instanceof Hoglin hoglin) {
            Objects.requireNonNull(hoglin.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK)).setBaseValue(8);
        }

        Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(newHealth);
        this.entity.setHealth(newHealth);

        if (entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null) {
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(newDamage);
        }
    }

    private void addInviSkill() {
        if (Utils.getRandom(100) > 25) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (entity.isDead()) {
                    this.cancel();
                    return;
                }
                entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0));
            }
        }.runTaskTimer(AdventureCraftCore.getInstance(), 0, 100);
    }

    private void addAntiKnockBackSkill(boolean ignore) {
        if (!ignore && Utils.getRandom(100) > 65) return;
        Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(1.0D);
    }
}
