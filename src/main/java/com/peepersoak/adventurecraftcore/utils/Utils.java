package com.peepersoak.adventurecraftcore.utils;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.combat.levelmobs.MobFactory;
import com.peepersoak.adventurecraftcore.combat.levelmobs.zombie.variation.AggresiveVirusZombie;
import com.peepersoak.adventurecraftcore.combat.levelmobs.zombie.variation.BoomerZombie;
import com.peepersoak.adventurecraftcore.openAI.ObjectiveStrings;
import com.peepersoak.adventurecraftcore.openAI.QuestSetting;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Random rand = new Random();

    public static String color(String msg) {
        if (msg == null || msg.equals("")) return "";
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static PersistentDataContainer getPDC(LivingEntity entity) {
        return entity.getPersistentDataContainer();
    }

    public static PersistentDataContainer getPDC(ItemMeta meta) {
        return meta.getPersistentDataContainer();
    }

    public static List<EntityType> getNonLevelledMobs() {
        List<EntityType> types = new ArrayList<>();
        types.add(EntityType.ENDER_DRAGON);
        types.add(EntityType.WITHER);
        types.add(EntityType.IRON_GOLEM);
        types.add(EntityType.SNOWMAN);
        types.add(EntityType.SLIME);
        types.add(EntityType.MAGMA_CUBE);
        return types;
    }

    public static int getRandom(int maxValue) {
        return rand.nextInt(maxValue) + 1;
    }

    public static int getRandom(int max, int min) {
        return rand.nextInt(max - min) + min;
    }

    public static double getRandomDouble(double max, double min) {
        return rand.nextDouble(max - min) + min;
    }

    public static void zombieLevelUp(Zombie zombie) {
        Integer xpGoal = Utils.getPDC(zombie).get(StringPath.MOB_XP_GOAL, PersistentDataType.INTEGER);
        if (xpGoal == null) return;

        Integer level = Utils.getPDC(zombie).get(StringPath.MOB_LEVEL_KEY, PersistentDataType.INTEGER);
        if (level == null) return;

        Integer currentXp = Utils.getPDC(zombie).get(StringPath.MOB_XP, PersistentDataType.INTEGER);
        if (currentXp == null) currentXp = 0;

        currentXp++;
        if (currentXp <= xpGoal) {
            Utils.getPDC(zombie).set(StringPath.MOB_XP, PersistentDataType.INTEGER, currentXp);
            return;
        }

        level++;
        Utils.getPDC(zombie).set(StringPath.MOB_LEVEL_KEY, PersistentDataType.INTEGER, level);
        Utils.getPDC(zombie).set(StringPath.MOB_XP, PersistentDataType.INTEGER, 0);
        Utils.getPDC(zombie).set(StringPath.MOB_XP_GOAL, PersistentDataType.INTEGER, xpGoal + 5);
        new MobFactory(level, zombie);
    }

    public static int getDayPassed() {
        World world = Bukkit.getWorld("world");
        if (world == null) return 0;
        return (int) (world.getFullTime() / 24000);
    }

    public static void sendSyncMessage(Player player, String message) {
        Bukkit.getScheduler().runTask(AdventureCraftCore.getInstance(), () -> player.sendMessage(color(message)));
    }

    public static int getRemainingCooldown(long cooldown) {
        long current = System.currentTimeMillis();
        if (cooldown <= current) return 0;
        return (int) ((cooldown - current) / 1000);
    }

    public static void dropItem(ItemStack item, int chance, Location location) {
        if (getRandom(100) > chance) return;
        if (location == null) return;
        if (location.getWorld() == null) return;
        location.getWorld().dropItemNaturally(location, item);
    }

    public static Object deserialized(String data) {
        try {
            byte[] raw = Base64.getDecoder().decode(data);
            ByteArrayInputStream is = new ByteArrayInputStream(raw);
            BukkitObjectInputStream bs = new BukkitObjectInputStream(is);
            return bs.readObject();
        } catch (IOException | ClassNotFoundException e) {
            AdventureCraftCore.getInstance().getLogger().warning("Failed to deserialized data!");
            e.printStackTrace();
        }
        return null;
    }

    public static String serialized(Object object) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            BukkitObjectOutputStream bs = new BukkitObjectOutputStream(os);
            bs.writeObject(object);
            bs.flush();
            byte[] data = os.toByteArray();
            return Base64.getEncoder().encodeToString(data);
        } catch (IOException e) {
            AdventureCraftCore.getInstance().getLogger().warning("Failed to serialized data!");
            e.printStackTrace();
        }
        return null;
    }

    public static void spawnRandomZombie(Location location) {
        if (location == null) return;
        ServerLevel world = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();
        if (Utils.getRandom(100, 1) < 15) {
            world.addFreshEntityWithPassengers(new BoomerZombie(location));
            return;
        }
        world.addFreshEntityWithPassengers(new AggresiveVirusZombie(location));
    }

    public static boolean checkWGState(Entity entity, StateFlag flag) {
        com.sk89q.worldedit.util.Location location = new com.sk89q.worldedit.util.Location(BukkitAdapter.adapt(entity.getWorld()), entity.getLocation().getBlockX(), entity.getLocation().getBlockY(), entity.getLocation().getBlockZ());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(location);

        return set.testState(null, flag);
    }

    public static int getMobLevelThreshold(Entity entity, IntegerFlag flag) {
        com.sk89q.worldedit.util.Location location = new com.sk89q.worldedit.util.Location(BukkitAdapter.adapt(entity.getWorld()), entity.getLocation().getBlockX(), entity.getLocation().getBlockY(), entity.getLocation().getBlockZ());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(location);

        Integer customThreshold = set.queryValue(null, flag);
        int defaultThreshold = AdventureCraftCore.getInstance().getConfig().getInt(ConfigPath.DISTANCE_THRESHOLD);

        return customThreshold == null ? defaultThreshold : customThreshold;
    }

    public static int getWorldGuardValue(Entity entity, IntegerFlag flag) {
        com.sk89q.worldedit.util.Location location = new com.sk89q.worldedit.util.Location(BukkitAdapter.adapt(entity.getWorld()), entity.getLocation().getBlockX(), entity.getLocation().getBlockY(), entity.getLocation().getBlockZ());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(location);

        Integer customValue = set.queryValue(null, flag);

        return customValue != null ? customValue : -1;
    }

    public static void setProjectileDamage(LivingEntity mob, EntityDamageByEntityEvent e) {
        Integer level = Utils.getPDC(mob).get(StringPath.MOB_LEVEL_KEY, PersistentDataType.INTEGER);
        level = level != null ? level : 0;

        double additionDamage = AdventureCraftCore.getInstance().getConfig().getDouble(ConfigPath.DAMAGE_MULTIPLIER) * level;

        if (mob instanceof Ghast) {
            e.setDamage(e.getDamage() + additionDamage);
        }
        else if (mob instanceof Skeleton) {
            e.setDamage(e.getDamage() + additionDamage);
        }
        else if (mob instanceof Blaze) {
            e.setDamage(e.getDamage() + additionDamage);
        }
    }

    public static void giveItemToPlayer(ItemStack item, Player player) {
        HashMap<Integer, ItemStack> items = player.getInventory().addItem(item);
        for (ItemStack i : items.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), i);
        }
    }

    public static boolean isWeapon(Material material) {
        return isSword(material) || isAxe(material) || isBow(material) || material == Material.TRIDENT;
    }

    public static boolean isTool(Material material) {
        return isPickaxe(material) || isAxe(material) || isShovel(material);
    }

    public static boolean isArmor(Material material) {
        return isHelmet(material) || isChestplate(material) || isLeggings(material) || isBoots(material);
    }

    // Helper methods to check specific types
    private static boolean isSword(Material material) {
        return material.name().endsWith("_SWORD");
    }

    private static boolean isAxe(Material material) {
        return material.name().endsWith("_AXE");
    }

    private static boolean isBow(Material material) {
        return material == Material.BOW;
    }

    private static boolean isPickaxe(Material material) {
        return material.name().endsWith("_PICKAXE");
    }

    private static boolean isShovel(Material material) {
        return material.name().endsWith("_SHOVEL");
    }

    public static boolean isHelmet(Material material) {
        return material.name().endsWith("_HELMET");
    }

    public static boolean isChestplate(Material material) {
        return material.name().endsWith("_CHESTPLATE");
    }

    public static boolean isLeggings(Material material) {
        return material.name().endsWith("_LEGGINGS");
    }

    public static boolean isBoots(Material material) {
        return material.name().endsWith("_BOOTS");
    }

    public static boolean isValidBiome(String biome) {
        try {
            Biome.valueOf(biome);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            // This is not a valid biome
            System.out.println(biome);
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isValidBiome(String biome, String worldType) {
        try {
            Biome b = Biome.valueOf(biome);

            if (worldType.equalsIgnoreCase("OVERWORLD")) {
                return AdventureCraftCore.getInstance().getQuestListChecker().isOverworldBiome(b);
            } else if (worldType.equalsIgnoreCase("THE_NETHER")) {
                return AdventureCraftCore.getInstance().getQuestListChecker().isNetherBiome(b);
            } else if (worldType.equalsIgnoreCase("THE_END")) {
                return AdventureCraftCore.getInstance().getQuestListChecker().isEndBiome(b);
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            // This is not a valid biome
            System.out.println(biome);
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isValidMaterial(String material) {
        try {
            Material.valueOf(material);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println(material);
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isValidEntity(String entityType) {
        try {
            EntityType.valueOf(entityType);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println(entityType);
            e.printStackTrace();
        }
        return false;
    }

    public static boolean validAttribute(String attribute) {
        try {
            Attribute.valueOf(attribute);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println(attribute);
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isValidEnchantment(String enchantment) {
        Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(enchantment));
        if (ench != null) return true;
        System.out.println("=================================================");
        System.out.println(enchantment);
        System.out.println("=================================================");
        return false;
    }

    public static String cleanString(String string) {
        if (string == null || string.equalsIgnoreCase("")) return "";
        String[] split = string.split("\\.");
        String check = string;
        if (split.length == 2) {
            check = split[1];
        }
        
        String cleanedString = check
                .replace("Enchantment.", "")
                .replace("Attribute.", "")
                .replace("EntityType.", "")
                .replace("Material.", "")
                .replace("Biome.", "");

        String clearColor = ChatColor.stripColor(cleanedString).replace(" ", "_").trim();
        Pattern pattern = Pattern.compile("([&§]\\S)"); // Pattern to match special characters like "&6"
        Matcher matcher = pattern.matcher(clearColor);

        String finalStringToReturn = clearColor;
        while (matcher.find()) {
            finalStringToReturn = cleanedString.replace(matcher.group(), "");
        }

        return finalStringToReturn;
    }

    public static List<String> getLore(String input) {
        if (input == null || input.equalsIgnoreCase("")) return new ArrayList<>();
        List<String> resultList = new ArrayList<>();
        Pattern pattern = Pattern.compile("([&§]\\S)"); // Pattern to match special characters like "&6"
        String[] words = input.split("\\s+"); // Split the input text into words

        String lastSpecialChar = ""; // Track the last special character
        // Loop on each word

        StringBuilder lineBuilder = new StringBuilder();
        int characterCount = 0;
        for (String currentWord : words) {
            // Check if it exceed the limit
            if (characterCount + currentWord.length() > 28) {
                String colored = Utils.color(lineBuilder.toString().trim());
                resultList.add(colored);
                characterCount = 0;
                lineBuilder.setLength(0);
            }

            Matcher matcher = pattern.matcher(currentWord);
            if (matcher.find()) {
                lastSpecialChar = matcher.group();
            }
            lineBuilder.append(lastSpecialChar);
            lineBuilder.append(currentWord);
            lineBuilder.append(" ");
            characterCount += currentWord.length();
        }

        if (!lineBuilder.isEmpty()) {
            String colored = Utils.color(lineBuilder.toString().trim());
            resultList.add(colored);
        }

        return resultList;
    }

    public static String convertSecondsToTime(int totalSeconds) {
        if (totalSeconds < 0) {
            throw new IllegalArgumentException("Total seconds must be non-negative.");
        }
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        // Format the time as "hh:mm:ss"
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    public static boolean isBetweenTwoNumber(int number, int min, int max) {
        return number >= min && number <= max;
    }
    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.equalsIgnoreCase("")) {
            return input;
        }
        StringBuilder builder = new StringBuilder();
        String[] split = input.replace("_", " ").split(" ");

        for (String word : split) {
            String string = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
            builder.append(string).append(" ");
        }

        return builder.toString().trim();
    }
    public static boolean isEnchantable(String material, String enchantment) {
        ItemStack item = new ItemStack(Material.valueOf(material));
        Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(enchantment));
        if (enchant == null) return false;
        return enchant.canEnchantItem(item);
    }

    public static boolean isCraftable(String material) {
        ItemStack item = new ItemStack(Material.valueOf(material));
        List<Recipe> recipes = Bukkit.getRecipesFor(item);
        return !recipes.isEmpty();
    }

    public static boolean isValidBreakableBlock(String material) {
        ItemStack item = new ItemStack(Material.valueOf(material));
        return item.getType().isSolid();
    }
    public static Object getWeightedObject(HashMap<?, Double> weightedMap) {
        // Calculate total weight
        double totalWeight = weightedMap.values().stream().mapToDouble(Double::doubleValue).sum();

        Random random = new Random();
        // Generate a random number between 0 and the total weight
        double randomValue = random.nextDouble() * totalWeight;

        // Find the rank corresponding to the random number
        double cumulativeWeight = 0.0;
        for (Map.Entry<?, Double> entry : weightedMap.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (randomValue <= cumulativeWeight) {
                return entry.getKey();
            }
        }
        // This should not happen, but in case of rounding errors, return the last rank
        return weightedMap.keySet().iterator().next();
    }

    public static Object getRanomObject(List<?> list) {
        if (list.isEmpty()) return null;
        int total = list.size();
        Random rand = new Random();
        int random = rand.nextInt(total);
        return list.get(random);
    }

    public static boolean isElligibleForThisQuestRank(String questRank, long duration) {
        QuestSetting setting = AdventureCraftCore.getInstance().getQuestSetting();

        boolean addCommon = questRank.equalsIgnoreCase(ObjectiveStrings.COMMON) && duration > setting.getCommonDuration();
        boolean addUnCommon = questRank.equalsIgnoreCase(ObjectiveStrings.UNCOMMON) && duration > setting.getUncommonDuration();
        boolean addRare = questRank.equalsIgnoreCase(ObjectiveStrings.RARE) && duration > setting.getRareDuration();
        boolean addEpic = questRank.equalsIgnoreCase(ObjectiveStrings.EPIC) && duration > setting.getEpicDuration();
        boolean addLegendary = questRank.equalsIgnoreCase(ObjectiveStrings.LEGENDARY) && duration > setting.getLegendaryDuration();
        boolean addMythical = questRank.equalsIgnoreCase(ObjectiveStrings.MYTHICAL) && duration > setting.getMythicalDuration();
        boolean addFabled = questRank.equalsIgnoreCase(ObjectiveStrings.FABLED) && duration > setting.getFabledDuration();
        boolean addGodlike = questRank.equalsIgnoreCase(ObjectiveStrings.GODLIKE) && duration > setting.getGodlikeDuration();
        boolean addAscended = questRank.equalsIgnoreCase(ObjectiveStrings.ASCENDED) && duration > setting.getAscendedDuration();

        return addCommon ||
                addUnCommon ||
                addRare ||
                addEpic ||
                addLegendary ||
                addMythical ||
                addFabled ||
                addGodlike ||
                addAscended;
    }

    public static Long getSessionDuration(Player player) {
        Long totalSeconds = getPDC(player).get(ObjectiveStrings.KEY_SESSION_DURATION, PersistentDataType.LONG);
        if (totalSeconds == null) totalSeconds = 0L;
        return totalSeconds;
    }

    public static int[] getMinMaxGoal(String questRank, String type) {
        int min = 1;
        int max = 2;

        boolean isBreak = type.equalsIgnoreCase(ObjectiveStrings.TYPE_BREAK);
        boolean isPlace = type.equalsIgnoreCase(ObjectiveStrings.TYPE_PLACE);
        boolean isHarvest = type.equalsIgnoreCase(ObjectiveStrings.TYPE_HARVEST);
        boolean isPlant = type.equalsIgnoreCase(ObjectiveStrings.TYPE_PLANT);
        boolean isCraft = type.equalsIgnoreCase(ObjectiveStrings.TYPE_CRAFT);
        boolean isEnchant = type.equalsIgnoreCase(ObjectiveStrings.TYPE_ENCHANT);
        boolean isFishing = type.equalsIgnoreCase(ObjectiveStrings.TYPE_FISHING);
        boolean isWalk = type.equalsIgnoreCase(ObjectiveStrings.TYPE_WALK);
        boolean isFly = type.equalsIgnoreCase(ObjectiveStrings.TYPE_FLY);
        boolean isKill = type.equalsIgnoreCase(ObjectiveStrings.TYPE_KILL);

        if (Utils.cleanString(questRank).equalsIgnoreCase(ObjectiveStrings.COMMON)) {
            if (isEnchant) {
                max = 3;
            } else if (isWalk || isFly) {
                min = 120;
                max = 300;
            } else if (isBreak || isPlace) {
                min = 64;
                max = 500;
            } else if (isHarvest || isPlant) {
                min = 64;
                max = 128;
            } else if (isCraft) {
                min = 16;
                max = 64;
            } else if (isFishing) {
                min = 16;
                max = 64;
            } else if (isKill) {
                min = 100;
                max = 200;
            }
        } else if (Utils.cleanString(questRank).equalsIgnoreCase(ObjectiveStrings.UNCOMMON)) {
            if (isEnchant) {
                min = 2;
                max = 5;
            } else if (isWalk || isFly) {
                min = 300;
                max = 700;
            } else if (isBreak || isPlace) {
                min = 500;
                max = 800;
            } else if (isHarvest) {
                min = 120;
                max = 300;
            } else if (isCraft) {
                min = 30;
                max = 64;
            } else if (isFishing) {
                min = 30;
                max = 64;
            } else if (isKill) {
                min = 100;
                max = 200;
            }
        } else if (Utils.cleanString(questRank).equalsIgnoreCase(ObjectiveStrings.RARE)) {
            if (isEnchant) {
                min = 5;
                max = 8;
            } else if (isWalk || isFly) {
                min = 700;
                max = 1000;
            } else if (isBreak || isPlace) {
                min = 800;
                max = 1500;
            } else if (isHarvest) {
                min = 300;
                max = 500;
            } else if (isCraft) {
                min = 64;
                max = 120;
            } else if (isFishing) {
                min = 64;
                max = 100;
            } else if (isKill) {
                min = 100;
                max = 300;
            }
        } else if (Utils.cleanString(questRank).equalsIgnoreCase(ObjectiveStrings.EPIC)) {
            if (isEnchant) {
                min = 8;
                max = 12;
            } else if (isWalk || isFly) {
                min = 1000;
                max = 2000;
            } else if (isBreak || isPlace) {
                min = 1500;
                max = 2500;
            } else if (isHarvest) {
                min = 500;
                max = 800;
            } else if (isCraft) {
                min = 120;
                max = 300;
            } else if (isFishing) {
                min = 100;
                max = 150;
            } else if (isKill) {
                min = 300;
                max = 500;
            }
        } else if (Utils.cleanString(questRank).equalsIgnoreCase(ObjectiveStrings.LEGENDARY)) {
            if (isEnchant) {
                min = 800;
                max = 1200;
            } else if (isWalk || isFly) {
                min = 2000;
                max = 3000;
            } else if (isBreak || isPlace) {
                min = 2500;
                max = 3000;
            } else if (isHarvest) {
                min = 800;
                max = 1500;
            } else if (isCraft) {
                min = 300;
                max = 500;
            } else if (isFishing) {
                min = 150;
                max = 200;
            } else if (isKill) {
                min = 300;
                max = 500;
            }
        } else if (Utils.cleanString(questRank).equalsIgnoreCase(ObjectiveStrings.MYTHICAL)) {
            if (isEnchant) {
                min = 20;
                max = 30;
            } else if (isWalk || isFly) {
                min = 3000;
                max = 4000;
            } else if (isBreak || isPlace) {
                min = 3000;
                max = 3500;
            } else if (isHarvest) {
                min = 1000;
                max = 1500;
            } else if (isCraft) {
                min = 500;
                max = 800;
            } else if (isFishing) {
                min = 200;
                max = 350;
            } else if (isKill) {
                min = 500;
                max = 800;
            }
        } else if (Utils.cleanString(questRank).equalsIgnoreCase(ObjectiveStrings.FABLED)) {
            if (isEnchant) {
                min = 30;
                max = 50;
            } else if (isWalk || isFly) {
                min = 4000;
                max = 5000;
            } else if (isBreak || isPlace) {
                min = 3500;
                max = 4000;
            } else if (isHarvest) {
                min = 1000;
                max = 1500;
            } else if (isCraft) {
                min = 800;
                max = 1000;
            } else if (isFishing) {
                min = 350;
                max = 500;
            } else if (isKill) {
                min = 800;
                max = 1000;
            }
        } else if (Utils.cleanString(questRank).equalsIgnoreCase(ObjectiveStrings.GODLIKE)) {
            if (isEnchant) {
                min = 50;
                max = 80;
            } else if (isWalk || isFly) {
                min = 5000;
                max = 6000;
            } else if (isBreak || isPlace) {
                min = 4000;
                max = 4500;
            } else if (isHarvest) {
                min = 1500;
                max = 2000;
            } else if (isCraft) {
                min = 1000;
                max = 1500;
            } else if (isFishing) {
                min = 500;
                max = 800;
            } else if (isKill) {
                min = 1000;
                max = 1500;
            }
        } else if (Utils.cleanString(questRank).equalsIgnoreCase(ObjectiveStrings.ASCENDED)) {
            if (isEnchant) {
                min = 50;
                max = 100;
            } else if (isWalk || isFly) {
                min = 6000;
                max = 7200;
            } else if (isBreak || isPlace) {
                min = 4500;
                max = 5000;
            } else if (isHarvest) {
                min = 2000;
                max = 3000;
            } else if (isCraft) {
                min = 1500;
                max = 2000;
            } else if (isFishing) {
                min = 800;
                max = 1000;
            } else if (isKill) {
                min = 1500;
                max = 3000;
            }
        } else {
            min = 300;
            max = 3000;
        }

        return new int[]{min, max};
    }
    public static int getMinDuration(String type, int goal) {
        int minDuration;

        boolean isBreak = type.equalsIgnoreCase(ObjectiveStrings.TYPE_BREAK);
        boolean isPlace = type.equalsIgnoreCase(ObjectiveStrings.TYPE_PLACE);
        boolean isHarvest = type.equalsIgnoreCase(ObjectiveStrings.TYPE_HARVEST);
        boolean isPlant = type.equalsIgnoreCase(ObjectiveStrings.TYPE_PLANT);
        boolean isCraft = type.equalsIgnoreCase(ObjectiveStrings.TYPE_CRAFT);
        boolean isEnchant = type.equalsIgnoreCase(ObjectiveStrings.TYPE_ENCHANT);
        boolean isFishing = type.equalsIgnoreCase(ObjectiveStrings.TYPE_FISHING);
        boolean isWalk = type.equalsIgnoreCase(ObjectiveStrings.TYPE_WALK);
        boolean isFly = type.equalsIgnoreCase(ObjectiveStrings.TYPE_FLY);
        boolean isKill = type.equalsIgnoreCase(ObjectiveStrings.TYPE_KILL);

        // Will not change base on rank
        if (isBreak) {
            // Min duration will be 3 seconds per block, 3 seconds is the time it will take
            // To mine an obsidian block using netherite pickaxe with Efficiency 5 enchantment
            minDuration = goal * 3;
            return minDuration;
        } else if (isEnchant) {
            // Min duration is 60 seconds per enchantment.
            minDuration = goal * 60;
            return minDuration;
        } else {
            // For other task, it will just add additional 10 minutes, making sure that
            // The that each objective will not go below 10 minutes
            minDuration = goal + 600;
            return  minDuration;
        }
    }
}
