package com.peepersoak.adventurecraftcore.items.enchantment;

public class EnchantmentFactory {

//    public EnchantmentFactory(LivingEntity mob, FileConfiguration config) {
//        this.config = config;
//        normalChance = config.getInt(ScriptureSetting.DROP_CHANCE_NORMAL);
//        customChance = config.getInt(ScriptureSetting.DROP_CHANCE_CUSTOM);
//        customSkill = config.getInt(ScriptureSetting.DROP_CHANCE_SKILL);
//
//        this.mob = mob;
//        this.level = this.getLevel();
//        this.enchantmentType = getEnchantmentType();
//        this.materialRarity = getRarity();
//        this.materialLore = getLore();
//    }
//
//    private final LivingEntity mob;
//    private final int level;
//    private final EnchantmentType enchantmentType;
//
//    private final int normalChance;
//    private final int customChance;
//    private final int customSkill;
//
//    private final String enchantmentName;
//    private final String materialName;
//    private final String materialRarity;
//
//    private final List<String> materialLore;
//
//    private final FileConfiguration config;
//
//    private int getLevel() {
//        Integer level = Utils.getPDC(this.mob).get(StringPath.MOB_LEVEL_KEY, PersistentDataType.INTEGER);
//        return Objects.requireNonNullElse(level, 1);
//    }
//
//    private EnchantmentType getEnchantmentType() {
//        if (this.level < 15) {
//            return EnchantmentType.NORMAL;
//        } else if (this.level < 30) {
//            int random = Utils.getRandom(100);
//            if (random < normalChance) {
//                return EnchantmentType.NORMAL;
//            } else {
//                return EnchantmentType.CUSTOM;
//            }
//        } else {
//            int random = Utils.getRandom(100);
//            if (random < customChance) {
//                return EnchantmentType.CUSTOM;
//            } else if (random < customSkill) {
//                return EnchantmentType.SKILL;
//            } else {
//                return EnchantmentType.NORMAL;
//            }
//        }
//    }
//
//    private List<String> getLore(CustomEnchantment customEnchant) {
//        List<String> materialLore = new ArrayList<>();
//
//        if (this.enchantmentType == EnchantmentType.NORMAL) {
//            materialLore.add(enchantmentName + ": ");
//        } else {
//            switch (customEnchant) {
//                case CRITICAL, HEADSHOT -> {
//                    materialLore.add("&7Chance to deal an additional");
//                    materialLore.add("&75 more damage per level");
//                }
//                case SPEED -> materialLore.add("&7Increase your speed by 400%");
//                case HEALTH_BOOST -> {
//                    materialLore.add("&7Increase your health by");
//                    materialLore.add("&720 points per level");
//                }
//                case LAST_RESORT ->{
//                    materialLore.add("&7When taking a lethal damage");
//                    materialLore.add("&7there's a chance to avoid death");
//                }
//                case LIFE_STEAL -> {
//                    materialLore.add("&7There's a chance to recover");
//                    materialLore.add("&7a portion of your life base");
//                    materialLore.add("&7on your damage");
//                }
//                case REJUVINATION -> {
//                    materialLore.add("&7Regain you health after");
//                    materialLore.add("&7Sleeping peacefully");
//                }
//                case SOUL_BOUND -> {
//                    materialLore.add("&7Allow an item to be bound");
//                    materialLore.add("&7to your soul that even death");
//                    materialLore.add("&7won't be able to take it away!");
//                }
//                case LIGHTNING_STRIKE -> {
//                    materialLore.add("&7A chance to hit a target with");
//                    materialLore.add("&7a lightning strike dealing an");
//                    materialLore.add("&7additional 5 more damage per level");
//                }
//                case EXPLOSION -> {
//                    materialLore.add("&7A chance to make the target");
//                    materialLore.add("&7explode after hitting them");
//                }
//                case ARISE -> {
//                    materialLore.add("&7A chance to summon your follower,");
//                    materialLore.add("&71 follower per level");
//                }
//                case GRAVITY -> {
//                    materialLore.add("&7Summon a gravitational pull on");
//                    materialLore.add("&7your target, every hostile mobs");
//                    materialLore.add("&7in a 15 block radius will be");
//                    materialLore.add("&7pulled towards your target.");
//                }
//                case RAGE -> {
//                    materialLore.add("&7When taking a lethal damage");
//                    materialLore.add("&7there will be a chance that");
//                    materialLore.add("&7you will enter rage mode,");
//                    materialLore.add("&7Increasing your health, damage");
//                    materialLore.add("&7and speed for 5 seconds, depending");
//                    materialLore.add("&7on the number of enemy around you.");
//                }
//                case FULL_COUNTER -> {
//                    materialLore.add("&7A chance to reflect the damage");
//                    materialLore.add("&7to your attacker with twice");
//                    materialLore.add("&7the ammount");
//                }
//            }
//        }
//
//        materialLore.add("");
//        materialLore.add(this.materialRarity);
//
//        return materialLore;
//    }
//
//    private String getRarity() {
//        ChatColor color = ChatColor.DARK_GRAY;
//        if (this.level <= 10 && this.level >= 5) return color + "Normal Scripture";
//        if (this.level > 10 && this.level <= 20) return color + "Rare Scripture";
//        if (this.level > 20 && this.level <= 30) return color + "Very Rare Scripture";
//        if (this.level > 30 && this.level <= 40) return color + "Legendary Scripture";
//        if (this.level > 40 && this.level <= 50) return color + "Ancient Scripture";
//        return color + "á’·âŠ£ê–Ž||!Â¡â�‘á“µâ•Žá’·";
//    }
//
//    public void getNormalEnchantLevel() {
//        if (this.level <= 10 && this.level >= 5) {
//            String enchantName = enchantmentName.toLowerCase().replace(" ", "_");
//            int enchantMaxLevel = Enchantment.getByKey(NamespacedKey.minecraft(enchantName)).getMaxLevel();
//            int random = rand.nextInt(10) + 1;
//            materialLevel = enchantMaxLevel + random;
//        }
//        if (mobLevel > 10) materialLevel = mobLevel;
//    }
}
