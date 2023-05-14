package com.peepersoak.adventurecraftcore.utils;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class AllPotionEffect {
    public AllPotionEffect() {
        potionEffects = new ArrayList<>();
        potionEffects.add(new PotionEffect(PotionEffectType.SPEED, 6000, 1));
        potionEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 0));
        potionEffects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 6000, 3));
        potionEffects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, 6000, 0));
        potionEffects.add(new PotionEffect(PotionEffectType.REGENERATION, 6000, 2));
        potionEffects.add(new PotionEffect(PotionEffectType.ABSORPTION, 6000, 4));
    }
    private final List<PotionEffect> potionEffects;
    public List<PotionEffect> getPotionList() {
        return potionEffects;
    }
}
