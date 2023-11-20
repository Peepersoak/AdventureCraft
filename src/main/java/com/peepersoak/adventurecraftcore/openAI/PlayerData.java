package com.peepersoak.adventurecraftcore.openAI;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import org.bukkit.NamespacedKey;

public class PlayerData {

    private final NamespacedKey BLOCK_BREAK = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Analytics.BlockBreak");
    private final NamespacedKey BLOCK_PLACE = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Analytics.BlockPlace");
    private final NamespacedKey CROP_HARVEST = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Analytics.PlantHarvest");
    private final NamespacedKey CROP_PLANT = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Analytics.CropHarvest");
    private final NamespacedKey WALK_DISTANCE = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Analytics.WalkDistance");
    private final NamespacedKey ITEM_PICKUP = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Analytics.ItemPickUp");
    private final NamespacedKey ITEM_DROP = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Analytics.ItemDrop");
    private final NamespacedKey KILL = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Analytics.Kill");
    private final NamespacedKey ENCHANTMENTS = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Analytics.Enchantments");
    private final NamespacedKey ENTER_NETHER = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Analytics.EnteringNether");
    private final NamespacedKey ENTER_THE_END = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Analytics.EnteringEnd");
    private final NamespacedKey ENTER_OVERWORLD = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Analytics.EnteringOverWorld");
    private final NamespacedKey TOOL_BREAK = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Analytics.ToolBreak");
    private final NamespacedKey DEATH_BY_MOBS = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Analytics.DeathByMobs");
    private final NamespacedKey DEATH_BY_NORMAL = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Analytics.DeathByNormalMeans");
    private final NamespacedKey DAMAGE_BY_MOBS = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Analytics.DamageByMobs");
    private final NamespacedKey ITEM_CONSUME = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Analytics.ItemConsume");
    private final NamespacedKey BREEDING = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Analytics.Breeding");
}
