package com.peepersoak.adventurecraftcore.utils;

import com.peepersoak.adventurecraftcore.openAI.ObjectiveStrings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.*;

public class QuestListChecker {

    private final List<EntityType> disabledEntityType = new ArrayList<>();
    private final List<Material> disabledBlocks = new ArrayList<>();
    private final List<Material> rareItems = new ArrayList<>();
    private final List<Enchantment> forbiddenEnchants = new ArrayList<>();
    private final List<String> objectiveList = new ArrayList<>();
    private final List<String> mythologies = new ArrayList<>();
    private List<Biome> overworldBiome = new ArrayList<>();
    private List<Biome> theNetherBiome = new ArrayList<>();
    private List<Biome> theEndBiome = new ArrayList<>();


    private final List<Material> weapons = new ArrayList<>();


    private final HashMap<Material, Double> harvestable = new HashMap<>();
    private final HashMap<Material, Double> fishable = new HashMap<>();
    private final HashMap<Enchantment, Integer> enchantmentMaxLevel = new HashMap<>();
    private final HashMap<String, TrimMaterial> trimMaterials = new HashMap<>();
    private final HashMap<String, TrimPattern> trimPatterns = new HashMap<>();

    public QuestListChecker() {
        setHarvestable();
        setDisabledEntityType();
        setDisabledBlocks();
        setForbiddenEnchantments();
        setFishaable();
        setWeapons();
        setRareItems();
        setTrimMaterial();
        setTrimPatterns();
        setObjectiveList();
        setMythologies();
        setBiomes();
    }

    private void setHarvestable() {
        harvestable.put(Material.CARROTS, 14.3);
        harvestable.put(Material.POTATOES, 14.3);
        harvestable.put(Material.WHEAT, 14.3);
        harvestable.put(Material.BEETROOTS, 14.3);
        harvestable.put(Material.MELON, 14.3);
        harvestable.put(Material.PUMPKIN, 14.3);
        harvestable.put(Material.COCOA, 14.3);
    }
    private void setRareItems() {
        rareItems.add(Material.DIAMOND);
        rareItems.add(Material.DIAMOND_BLOCK);
        rareItems.add(Material.ANCIENT_DEBRIS);
        rareItems.add(Material.NETHERITE_SCRAP);
        rareItems.add(Material.EMERALD);
        rareItems.add(Material.EMERALD_BLOCK);
        rareItems.add(Material.ENCHANTED_GOLDEN_APPLE);
        rareItems.add(Material.ECHO_SHARD);
        rareItems.add(Material.ELYTRA);
        rareItems.add(Material.SPONGE);
        rareItems.add(Material.DRAGON_BREATH);
        rareItems.add(Material.SEA_LANTERN);
        rareItems.add(Material.NETHER_STAR);
        rareItems.add(Material.HEART_OF_THE_SEA);
        rareItems.add(Material.CONDUIT);
        rareItems.add(Material.END_CRYSTAL);
        rareItems.add(Material.BEACON);
        rareItems.add(Material.DRAGON_EGG);
        rareItems.add(Material.SHULKER_SHELL);

        rareItems.add(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        rareItems.add(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE);
        rareItems.add(Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE);
        rareItems.add(Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE);
        rareItems.add(Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE);
        rareItems.add(Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE);
        rareItems.add(Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE);
        rareItems.add(Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE);
        rareItems.add(Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE);
        rareItems.add(Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE);
        rareItems.add(Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE);
        rareItems.add(Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE);
        rareItems.add(Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE);
        rareItems.add(Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE);
        rareItems.add(Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);
        rareItems.add(Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE);
        rareItems.add(Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE);
    }
    private void setTrimMaterial() {
        trimMaterials.put("NETHERITE" ,TrimMaterial.NETHERITE);
        trimMaterials.put("DIAMOND", TrimMaterial.DIAMOND);
        trimMaterials.put("EMERALD", TrimMaterial.EMERALD);
        trimMaterials.put("GOLD", TrimMaterial.GOLD);
        trimMaterials.put("COPPER", TrimMaterial.COPPER);
        trimMaterials.put("REDSTONE", TrimMaterial.REDSTONE);
        trimMaterials.put("AMETHYST", TrimMaterial.AMETHYST);
        trimMaterials.put("IRON", TrimMaterial.IRON);
        trimMaterials.put("LAPIS", TrimMaterial.LAPIS);
        trimMaterials.put("QUARTZ", TrimMaterial.QUARTZ);
    }
    private void setTrimPatterns() {
        trimPatterns.put("SENTRY", TrimPattern.SENTRY);
        trimPatterns.put("VEX", TrimPattern.VEX);
        trimPatterns.put("WILD", TrimPattern.WILD);
        trimPatterns.put("COAST", TrimPattern.COAST);
        trimPatterns.put("DUNE", TrimPattern.DUNE);
        trimPatterns.put("WAYFINDER", TrimPattern.WAYFINDER);
        trimPatterns.put("RAISER", TrimPattern.RAISER);
        trimPatterns.put("SHAPER", TrimPattern.SHAPER);
        trimPatterns.put("HOST", TrimPattern.HOST);
        trimPatterns.put("WARD", TrimPattern.WARD);
        trimPatterns.put("SILENCE", TrimPattern.SILENCE);
        trimPatterns.put("TIDE", TrimPattern.TIDE);
        trimPatterns.put("SNOUT", TrimPattern.SNOUT);
        trimPatterns.put("RIB", TrimPattern.RIB);
        trimPatterns.put("EYE", TrimPattern.EYE);
        trimPatterns.put("SPIRE", TrimPattern.SPIRE);
    }
    private void setDisabledEntityType() {
        disabledEntityType.add(EntityType.GIANT);
        disabledEntityType.add(EntityType.ILLUSIONER);
        disabledEntityType.add(EntityType.ZOMBIE_HORSE);
    }
    private void setDisabledBlocks() {
        disabledBlocks.add(Material.BEDROCK);
        disabledBlocks.add(Material.BARRIER);
        disabledBlocks.add(Material.REINFORCED_DEEPSLATE);
        disabledBlocks.add(Material.COMMAND_BLOCK);
        disabledBlocks.add(Material.STRUCTURE_VOID);
        disabledBlocks.add(Material.STRUCTURE_BLOCK);
        disabledBlocks.add(Material.END_PORTAL_FRAME);
        disabledBlocks.add(Material.END_GATEWAY);
        disabledBlocks.add(Material.END_PORTAL);
        disabledBlocks.add(Material.JIGSAW);
    }
    private void setForbiddenEnchantments() {
        forbiddenEnchants.add(Enchantment.BINDING_CURSE);
        forbiddenEnchants.add(Enchantment.VANISHING_CURSE);
        forbiddenEnchants.add(Enchantment.MENDING);
        forbiddenEnchants.add(Enchantment.FROST_WALKER);
        forbiddenEnchants.add(Enchantment.SOUL_SPEED);
        forbiddenEnchants.add(Enchantment.SWIFT_SNEAK);

        enchantmentMaxLevel.put(Enchantment.DAMAGE_ARTHROPODS, 4);
        enchantmentMaxLevel.put(Enchantment.DAMAGE_UNDEAD, 4);
        enchantmentMaxLevel.put(Enchantment.DAMAGE_ALL, 4);
        enchantmentMaxLevel.put(Enchantment.DIG_SPEED, 4);
        enchantmentMaxLevel.put(Enchantment.THORNS, 2);
    }
    private void setFishaable() {
        // Fish
        fishable.put(Material.COD, 30.0);
        fishable.put(Material.SALMON, 15.0);
        fishable.put(Material.TROPICAL_FISH, 5.0);
        fishable.put(Material.PUFFERFISH, 10.0);

        // Treasure
        fishable.put(Material.BOOK, 2.5);
        fishable.put(Material.BOW, 2.5);
        fishable.put(Material.FISHING_ROD, 2.5);
        fishable.put(Material.NAME_TAG, 2.5);
        fishable.put(Material.NAUTILUS_SHELL, 2.5);
        fishable.put(Material.SADDLE, 2.5);

        // Junks
        fishable.put(Material.LILY_PAD, 10.0);
        fishable.put(Material.BOWL, 10.0);
        fishable.put(Material.LEATHER, 2.5);
        fishable.put(Material.LEATHER_BOOTS, 2.5);
        fishable.put(Material.ROTTEN_FLESH, 2.5);
        fishable.put(Material.GLASS_BOTTLE, 2.5);
        fishable.put(Material.BONE, 2.5);
        fishable.put(Material.TRIPWIRE_HOOK, 2.5);
        fishable.put(Material.STICK, 5.0);
        fishable.put(Material.STRING, 5.0);
        fishable.put(Material.INK_SAC, 5.0);
    }
    private void setWeapons() {
        // Sword
        weapons.add(Material.STONE_SWORD);
        weapons.add(Material.IRON_SWORD);
        weapons.add(Material.GOLDEN_SWORD);
        weapons.add(Material.DIAMOND_SWORD);
        weapons.add(Material.NETHERITE_SWORD);

        // Axe
        weapons.add(Material.STONE_AXE);
        weapons.add(Material.IRON_AXE);
        weapons.add(Material.GOLDEN_AXE);
        weapons.add(Material.DIAMOND_AXE);
        weapons.add(Material.NETHERITE_AXE);

        // Shovel
        weapons.add(Material.STONE_SHOVEL);
        weapons.add(Material.IRON_SHOVEL);
        weapons.add(Material.GOLDEN_SHOVEL);
        weapons.add(Material.DIAMOND_SHOVEL);
        weapons.add(Material.NETHERITE_SHOVEL);

        // Hoe
        weapons.add(Material.STONE_HOE);
        weapons.add(Material.IRON_HOE);
        weapons.add(Material.GOLDEN_HOE);
        weapons.add(Material.DIAMOND_HOE);
        weapons.add(Material.NETHERITE_HOE);

        // Pickaxe
        weapons.add(Material.STONE_PICKAXE);
        weapons.add(Material.IRON_PICKAXE);
        weapons.add(Material.GOLDEN_PICKAXE);
        weapons.add(Material.DIAMOND_PICKAXE);
        weapons.add(Material.NETHERITE_PICKAXE);

        // Range
        weapons.add(Material.TRIDENT);
        weapons.add(Material.BOW);
    }
    private void setObjectiveList() {
        objectiveList.add(ObjectiveStrings.TYPE_BREAK);
        objectiveList.add(ObjectiveStrings.TYPE_HARVEST);
        objectiveList.add(ObjectiveStrings.TYPE_FISHING);
        objectiveList.add(ObjectiveStrings.TYPE_KILL);
//        objectiveList.add(ObjectiveStrings.TYPE_PLANT);
//        objectiveList.add(ObjectiveStrings.TYPE_PLACE);
        objectiveList.add(ObjectiveStrings.TYPE_FLY);
        objectiveList.add(ObjectiveStrings.TYPE_WALK);
        objectiveList.add(ObjectiveStrings.TYPE_CRAFT);
        objectiveList.add(ObjectiveStrings.TYPE_ENCHANT);
    }
    private void setMythologies() {
        mythologies.add("Greek Mythology");
        mythologies.add("Roman Mythology");
        mythologies.add("Norse Mythology");
        mythologies.add("Egyptian Mythology");
        mythologies.add("Hindu Mythology");
        mythologies.add("Chinese Mythology");
        mythologies.add("Japanese Mythology");
        mythologies.add("Mesopotamian Mythology");
        mythologies.add("Celtic Mythology");
        mythologies.add("African Mythology");
        mythologies.add("Native American Mythology");
        mythologies.add("Aztec and Mayan Mythology");
        mythologies.add("Inuit Mythology");
        mythologies.add("Oceanian Mythology");
        mythologies.add("Arthurian Legends");
        mythologies.add("Sumerian Mythology");
        mythologies.add("Slavic Mythology");
        mythologies.add("Arthurian Legends");
        mythologies.add("Aboriginal Australian Mythology");
        mythologies.add("Inca Mythology");
    }
    private void setBiomes() {
        for (World world : Bukkit.getWorlds()) {
            BiomeProvider provider = world.getBiomeProvider();
            if (provider == null) continue;
            List<Biome> biomes = provider.getBiomes(world);
            if (biomes.isEmpty()) continue;

            if (world.getEnvironment() == World.Environment.NORMAL) {
                System.out.println("OVERWORLD");
                overworldBiome = new ArrayList<>(biomes);
            } else if (world.getEnvironment() == World.Environment.NETHER) {
                System.out.println("NETHER");
                theNetherBiome = new ArrayList<>(biomes);
            } else if (world.getEnvironment() == World.Environment.THE_END) {
                System.out.println("END");
                theEndBiome = new ArrayList<>(biomes);
            }
            for (Biome b : biomes) {
                System.out.println(b.getKey().getKey());
            }
        }
    }
    public boolean isHarvestable(String material) {
        return harvestable.containsKey(Material.valueOf(material));
    }
    public boolean isDisabledEntity(String entityType) {
        return disabledEntityType.contains(EntityType.valueOf(entityType));
    }
    public boolean isUnbreakable(String material) {
        return disabledBlocks.contains(Material.valueOf(material));
    }
    public boolean isForbiddenEnchantment(Enchantment enchantment) {
        return forbiddenEnchants.contains(enchantment);
    }
    public boolean getFishable(String material) {
        return fishable.containsKey(Material.valueOf(material));
    }
    public boolean itemIsRare(ItemStack item) {
        Material material1 = item.getType();
        return  rareItems.contains(material1);
    }
    public boolean isValidObjective(String objective) {
        return objectiveList.contains(objective);
    }
    public boolean isOverworldBiome(Biome biome) {
        return overworldBiome.contains(biome);
    }
    public boolean isNetherBiome(Biome biome) {
        return theNetherBiome.contains(biome);
    }
    public boolean isEndBiome(Biome biome) {
        return theEndBiome.contains(biome);
    }
    public int getMaxLevelEnchantment(Enchantment enchantment) {
        if (enchantmentMaxLevel.containsKey(enchantment)) {
            return enchantmentMaxLevel.get(enchantment);
        }
        return enchantment.getMaxLevel();
    }
    public Material getRandomWeapon() {
        return (Material) Utils.getRanomObject(weapons);
    }
    public TrimPattern getTrimPattern(String pattern) {
        for (String str : trimPatterns.keySet()) {
            if (str.equalsIgnoreCase(pattern)) {
                return trimPatterns.get(str);
            }
        }
        return null;
    }
    public TrimMaterial getTrimMaterial(String material) {
        for (String str : trimMaterials.keySet()) {
            if (str.equalsIgnoreCase(material)) {
                return trimMaterials.get(str);
            }
        }
        return null;
    }
    public List<String> getObjectiveList() {
        return objectiveList;
    }
    public String getRandomMythologies() {
        return (String) Utils.getRanomObject(mythologies);
    }

}
