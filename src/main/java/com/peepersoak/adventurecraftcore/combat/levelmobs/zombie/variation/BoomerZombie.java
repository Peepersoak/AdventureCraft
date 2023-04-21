package com.peepersoak.adventurecraftcore.combat.levelmobs.zombie.variation;

import com.peepersoak.adventurecraftcore.combat.levelmobs.MobFactory;
import com.peepersoak.adventurecraftcore.combat.levelmobs.zombie.ZombieType;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Zombie;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class BoomerZombie extends Zombie {

    public BoomerZombie(Location loc) {
        super(EntityType.ZOMBIE, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());

        this.setPos(loc.getX(), loc.getY(), loc.getZ());

        this.setCanPickUpLoot(false);
        this.setAggressive(true);
        this.setCanBreakDoors(true);
        this.setDropChance(EquipmentSlot.CHEST, 0.0F);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        this.setDropChance(EquipmentSlot.OFFHAND, 0.0F);

        LivingEntity zombie = (LivingEntity) this.getBukkitEntity();
        Utils.getPDC(zombie).set(StringPath.CUSTOM_ZOMBIE, PersistentDataType.STRING, ZombieType.BOOMER_ZOMBIE);

        Objects.requireNonNull(zombie.getEquipment()).setChestplate(getBombChestplate());
        zombie.getEquipment().setItemInMainHand(new ItemStack(Material.TNT));
        zombie.getEquipment().setItemInOffHand(new ItemStack(Material.TNT));

        new MobFactory(zombie);
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, ServerPlayer.class, true));
        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));

        this.goalSelector.addGoal(0, new LeapAtTargetGoal(this, 0.7F));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 2.0, true));

        this.goalSelector.addGoal(2, new OpenDoorGoal(this, true));

        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, ServerPlayer.class, 8.0F));

        this.goalSelector.addGoal(5, new FloatGoal(this));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    private ItemStack getBombChestplate() {
        ItemStack bomb = new ItemStack(Material.GOLDEN_CHESTPLATE);
        ItemMeta meta = bomb.getItemMeta();
        if (meta == null) return null;
        meta.setDisplayName("Boombie Chestplate");
        bomb.setItemMeta(meta);
        return bomb;
    }
}
