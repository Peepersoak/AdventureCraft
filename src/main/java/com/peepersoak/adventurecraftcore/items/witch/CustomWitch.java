package com.peepersoak.adventurecraftcore.items.witch;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.items.wards.WardType;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Witch;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class CustomWitch extends Witch {

    public CustomWitch(Location loc, WardType type) {
        super(EntityType.WITCH, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());

        this.setPos(loc.getX(), loc.getY(), loc.getZ());

        this.setCanPickUpLoot(false);
        this.setAggressive(true);
        this.setCanJoinRaid(false);

        org.bukkit.entity.Witch witch = (org.bukkit.entity.Witch) this.getBukkitEntity();
        witch.setRemoveWhenFarAway(true);
        witch.setCustomName(Utils.color("&e" + type.name().replace("_", " ") + " WARD"));
        witch.setCustomNameVisible(true);
        witch.getPersistentDataContainer().set(StringPath.WARD_WITCH, PersistentDataType.STRING, "CustomWitch");

        runTask(type, witch);
    }

    @Override
    public void registerGoals() {
//        this.goalSelector.addGoal(0, new AvoidEntityGoal<>(this, net.minecraft.world.entity.monster.Monster.class, 10.0F, 2.0, 2.0));

        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, ServerPlayer.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8));

        super.registerGoals();
    }

    @Override
    public void aiStep() {
        if (!this.level.isClientSide && this.isAlive()) {
            if (this.random.nextFloat() < 7.5E-4F) {
                this.level.broadcastEntityEvent(this, (byte)15);
            }
        }
        super.aiStep();
    }

    @Override
    public void performRangedAttack(LivingEntity entityliving, float f) {

    }

    private void runTask(WardType type, org.bukkit.entity.Witch witch) {
        new BukkitRunnable() {
            int count = 300;
            @Override
            public void run() {
                if (count <= 0) witch.remove();
                if (witch.isDead()) {
                    witch.remove();
                    this.cancel();
                    return;
                }
                switch (type) {
                    case HEALING, RESISTANCE, DAMAGE_BOOST, FIRE_RESISTANCE  -> {
                        for (Entity entity : witch.getNearbyEntities(10, 5, 10)) {
                            if (!(entity instanceof Player player)) continue;
                            player.addPotionEffect(new PotionEffect(getPotionEffect(type), 60, 2));
                        }
                    }
                    case POISON, WEAKNESS, SLOWNESS -> {
                        for (Entity entity : witch.getNearbyEntities(10, 5, 10)) {
                            if (!(entity instanceof Monster monster)) continue;
                            if (monster.getPersistentDataContainer().has(StringPath.WARD_WITCH, PersistentDataType.STRING)) continue;
                            monster.addPotionEffect(new PotionEffect(getPotionEffect(type), 60, 2));
                        }
                    }
                }
                count--;
            }
        }.runTaskTimer(AdventureCraftCore.getInstance(), 0, 20);
    }

    private PotionEffectType getPotionEffect(WardType type) {
        PotionEffectType potionEffectType;

        switch (type) {
            case HEALING -> potionEffectType = PotionEffectType.REGENERATION;
            case RESISTANCE -> potionEffectType = PotionEffectType.DAMAGE_RESISTANCE;
            case DAMAGE_BOOST -> potionEffectType = PotionEffectType.INCREASE_DAMAGE;
            case FIRE_RESISTANCE -> potionEffectType = PotionEffectType.FIRE_RESISTANCE;
            case POISON -> potionEffectType = PotionEffectType.WITHER;
            case WEAKNESS -> potionEffectType = PotionEffectType.WEAKNESS;
            case SLOWNESS -> potionEffectType = PotionEffectType.SLOW;
            default -> potionEffectType = null;
        }

        return potionEffectType;
    }
}
