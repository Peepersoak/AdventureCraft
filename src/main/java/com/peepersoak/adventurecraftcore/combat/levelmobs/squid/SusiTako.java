package com.peepersoak.adventurecraftcore.combat.levelmobs.squid;

import com.peepersoak.adventurecraftcore.combat.levelmobs.MobFactory;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class SusiTako extends Squid {

    public SusiTako(Location loc) {
        super(EntityType.SQUID, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.setPos(loc.getX(), loc.getY(), loc.getZ());

        LivingEntity squid = (LivingEntity) this.getBukkitEntity();

        squid.getPersistentDataContainer().set(StringPath.CUSTOM_SQUID, PersistentDataType.INTEGER, 1);
        new MobFactory(squid);
    }

//    private static Field attributeField;

//    static {
//        try {
//            attributeField = AttributeMapBase.class.getDeclaredField("b");
//            attributeField.setAccessible(true);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void registerGenericAttribute(LivingEntity entity, Attribute attribute) throws IllegalAccessException {
//       AttributeMapBase attributeMapBase = ((CraftLivingEntity)entity).getHandle().getAttributes();
//        Map<nAttributeBase,AttributeModifiable> map = (Map<AttributeBase,AttributeModifiable>) attributeField.get(attributeMapBase);
//        AttributeBase attributeBase = CraftAttributeMap.toMinecraft(attribute);
//        AttributeModifiable attributeModifiable = new AttributeModifiable(attributeBase, AttributeModifiable::getAttribute);
//        map.put(attributeBase, attributeModifiable);
//    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, ServerPlayer.class, true));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 2.0, true));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, ServerPlayer.class, 8.0F));

        this.goalSelector.addGoal(0, new SquidRandomMovementGoal(this));
    }

    private static class SquidRandomMovementGoal extends Goal {
        private final Squid squid;

        public SquidRandomMovementGoal(Squid entitysquid) {
            this.squid = entitysquid;
        }

        public boolean canUse() {
            return true;
        }

        public void tick() {
            int i = this.squid.getNoActionTime();
            if (i > 100) {
                this.squid.setMovementVector(0.0F, 0.0F, 0.0F);
            } else if (this.squid.getRandom().nextInt(reducedTickDelay(50)) == 0 || !this.squid.wasTouchingWater || !this.squid.hasMovementVector()) {
                float f = this.squid.getRandom().nextFloat() * 6.2831855F;
                float f1 = Mth.cos(f) * 0.2F;
                float f2 = -0.1F + this.squid.getRandom().nextFloat() * 0.2F;
                float f3 = Mth.sin(f) * 0.2F;
                this.squid.setMovementVector(f1, f2, f3);
            }

        }
    }
}
