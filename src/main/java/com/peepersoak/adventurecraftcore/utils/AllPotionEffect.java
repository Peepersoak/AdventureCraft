package com.peepersoak.adventurecraftcore.utils;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class AllPotionEffect {
    public AllPotionEffect() {
        potionEffects = new ArrayList<>();
        potionEffects.add(new PotionEffect(PotionEffectType.SPEED, 1200, 1));
        potionEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1200, 0));
        potionEffects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1200, 3));
        potionEffects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, 1200, 0));
        potionEffects.add(new PotionEffect(PotionEffectType.REGENERATION, 1200, 2));
        potionEffects.add(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 4));
    }
    private final List<PotionEffect> potionEffects;
    public List<PotionEffect> getPotionList() {
        return potionEffects;
    }
}
