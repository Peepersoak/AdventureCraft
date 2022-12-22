package com.peepersoak.adventurecraftcore.combat.levelmobs.zombie.variation;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.combat.levelmobs.MobFactory;
import com.peepersoak.adventurecraftcore.combat.levelmobs.zombie.DestroyBlock;
import com.peepersoak.adventurecraftcore.combat.levelmobs.zombie.ZombieType;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class AggresiveVirusZombie extends Zombie {

    public AggresiveVirusZombie(Location loc) {
        super(EntityType.ZOMBIE, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());

        this.setPos(loc.getX(), loc.getY(), loc.getZ());

        this.setCanPickUpLoot(true);
        this.setAggressive(true);
        this.setCanBreakDoors(true);

        LivingEntity zombie = (LivingEntity) this.getBukkitEntity();
        Utils.getPDC(zombie).set(StringPath.CUSTOM_ZOMBIE, PersistentDataType.STRING, ZombieType.AGGRESIVE_ZOMBIE);

        new MobFactory(zombie);
        new DestroyBlock((org.bukkit.entity.Zombie) zombie).runTaskTimer(AdventureCraftCore.getInstance(), 100, 100);
    }

    @Override
    public void registerGoals() {
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, ServerPlayer.class, true));
        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));

        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Cow.class, true));
        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Sheep.class, true));
        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Pig.class, true));
        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Chicken.class, true));

        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Donkey.class, true));
        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Horse.class, true));
        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Llama.class, true));

        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Wolf.class, true));
        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Cat.class, true));
        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Parrot.class, true));

        this.goalSelector.addGoal(0, new LeapAtTargetGoal(this, 0.7F));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 2.0, true));

        this.goalSelector.addGoal(2, new OpenDoorGoal(this, true));

        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, ServerPlayer.class, 8.0F));

        this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));

        this.goalSelector.addGoal(5, new FloatGoal(this));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));

    }
}
