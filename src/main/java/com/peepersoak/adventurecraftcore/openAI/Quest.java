package com.peepersoak.adventurecraftcore.openAI;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Quest {
    private final Random rand = new Random();
    private String name;
    private String description;
    private String difficulty;
    private int duration;
    private ItemStack itemRewards;
    private int experienceRewards = -1;
    private int moneyRewards = -1;
    private String itemType;
    private String itemRank;
    private String itemName;
    private HashMap<String, HashMap<String, Object>> objectives = new HashMap<>();
    private UUID questUUID;
    private UUID playerUUID;

    public Quest(Player player, String message) {
        this.playerUUID = player.getUniqueId();
        getNewQuest(message, player);
    };

    public Quest(
            String name,
            String description,
            String difficulty,
            int duration,
            String serializedItemStack,
            int experienceRewards,
            int moneyRewards,
            String itemType,
            String itemRank,
            String itemName,
            String serializedObjectives,
            String questUUID) {

        this.name = name;
        this.description = description;
        this.duration = duration;
        this.difficulty = difficulty;
        this.experienceRewards = experienceRewards;
        this.moneyRewards = moneyRewards;
        this.itemType = itemType;
        this.itemRank = itemRank;
        this.itemName = itemName;
        this.questUUID = UUID.fromString(questUUID);

        this.itemRewards = (ItemStack) Utils.deserialized(serializedItemStack);

        Object object = Utils.deserialized(serializedObjectives);
        if (object instanceof HashMap<?, ?> rawObjects) {
            HashMap<String, HashMap<String, Object>> objectives = new HashMap<>();
            for (Object key : rawObjects.keySet()) {
                Object value = rawObjects.get(key);
                if (key instanceof String && value instanceof HashMap<?, ?> rawObjectsTwo) {
                    HashMap<String, Object> objective = new HashMap<>();
                    for (Object key2 : rawObjectsTwo.keySet()) {
                        Object value2 = rawObjectsTwo.get(key2);
                        if (key2 instanceof String && value2 != null) {
                            objective.put((String) key2, value2);
                        }
                    }
                    objectives.put((String) key, objective);
                }
            }
            this.objectives = objectives;
        }
    }

    public boolean updateDuration(int seconds) {
        duration -= seconds;
        return duration <= 0;
    }

    public void getNewQuest(String message, Player player) {
        String instruction =
        """
                In the vast and ever-changing world of Minecraft, you play the crucial role of the Game Master, overseeing the game and responsible for creating captivating quests that significantly impact players' adventures in the pixelated realm. These quests are categorized into five distinct difficulties: Common, Uncommon, Rare, Epic, Legendary, Mythical, Fabled, Godlike, and Ascended. It's essential to align each quest with players' ongoing activities, seamlessly integrating with their current pursuits, and considering the duration of their immersion in the game. Higher-difficulty quests come with more challenging objectives and requirements, often in the range of hundreds or even thousands. Make it visually appealing by adding multiple colors on each sentence using the Spigot API color codes like, &c for Red, &b for Aqua, etc.
                        
                When designing a quest, you can choose one or more objectives, with a maximum of five objectives, you can include multiple instances of the same objective, for example, two Break objectives. Quests with lower difficulty levels should have fewer objectives, while higher-difficulty quests will have more objectives and more conditions. Multiple goals, such as killing two entities, should be specified separately. Adjust the duration in seconds based on the difficulty of the objectives, higher difficulty should have more time.
                        
                Here are the types of objectives and their corresponding requirements:
                        
                1. Break
                Type: Type of material
                Count: Number of blocks to be mined
                Biome: Target biome where the player should mine (e.g., DESERT; the specified block type should be present in this biome). Make sure the this is a valid biome found in Spigot API.
                StartY and EndY: Start and end of Y coordinate; the block is counted only if mined between these two locations.
                        
                2. Walk - Walk a Distance Away from the Initial Spot
                Type: Choose one of the following, NORMAL, NETHER, THE_END
                Biome: Target biome, this biome must be present in the type of world. Make sure the this is a valid biome found in Spigot API.
                Count: Target distance from the initial location
                StartY and EndY: Start and end of Y coordinate; the distance is counted only if the player walks between these two locations.
                TimeStart and TimeEnd: Time range (0-23000); the kill is counted only if the entity is killed between this time.
                
                3. Fly - Glide using an elytra.
                Similar parameters as the "Walk" objective.
               
                4. Kill - Kill Entities
                Type: Type of entities
                Count: Number of entities to kill
                Biome: Target biome; the kill is counted only if the entity is killed in this biome, and the entity should be present in this biome. Make sure the this is a valid biome found in Spigot API.
                TimeStart and TimeEnd: Minecraft Game Time in Ticks (0-24000); the kill is counted only if the entity is killed between this time.
                        
                5. Harvest - Harvest Some Plants or Crops (e.g., Carrots or Potato)
                Type: Item to plant or harvest
                Count: Number of times to perform this action.
                        
                6. Plant - Plant Some Plants or Crops (e.g., Carrots or Potato)
                Similar parameters as the "Harvest" objective.
                        
                7. Craft - Craft a Craftable Item (e.g., Arrow)
                Type: Item to craft
                Count: Number of items to craft.
                        
                8. Enchant - Enchant an Enchantable Item (e.g., Diamond Sword)
                Type: Item to Enchant
                Enchantment: Target enchantment to add to the item. The enchantment must be applicable to the type of the item.
                Level: The level of the enchantment. It must not exceed the maximum level of the enchantment.
                Count: Number of items to enchant.
                Biome: Target biome. The player should be enchanted in this biome for it to count. Make sure the this is a valid biome found in Spigot API.
                TimeStart and TimeEnd: Time range (0-23000). The enchantment is counted only if performed between this time.
                        
                Rewards, you can choose one or more rewards from this, Money, Experience, Items, or Custom Items.
                Money: An amount of money to be given to the player. Higher difficulty should provide more money that can reach thousands.
                Experience: The amount of experience given to the player, is the same as money, higher difficulty should provide more experience.
                Item Type: The Material for this item.
                Item Rank: Choose one of the following, Common, Uncommon, Rare, Epic, Legendary, Mythical, Fabled, Godlike, and Ascended. Higher item rank for higher quest rank.
                Item Name: The name of the item, should match the context of the quest and the rank. Use color
                        
                For the items, entities, materials, biomes, and color codes use the Spigot API enum to get their correct values.
                        
                Format the final output in a single line. Just provide the quest data, don't include redundant information. Make sure the pattern is correct.
                [Rank<=>Colored Rank][Quest Name<=>Name][Quest Lore<=>Lore][Duration<=>Seconds][Objectives<=>{Objective%Objective Type}{Title%Objective Title}{Type%The type}{Count%100}<->{Objective%Objective Type}{Title%Objective Title}{Type%The type}{Count%100}][Rewards<=>{Money%Amount}{Experience%Amount}{Item Type&Type}{Item Rank%Rank}{Item Name%Name}]""";

        String json = AdventureCraftCore.getInstance().getOpenai().generate(message, instruction);
        if (json == null) {
            AdventureCraftCore.getInstance().getLogger().info("No quest found");
            return;
        }

        Pattern bracket = Pattern.compile("\\[(.*?)]", Pattern.DOTALL);
        Matcher bracketMatch = bracket.matcher(json);

        while (bracketMatch.find()) {
            String[] keyValue = bracketMatch.group(1).split("<=>");
            if (keyValue.length != 2) continue;
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            System.out.println("KEY: " + key);
            System.out.println("VALUE: " + value);

            if (key.equalsIgnoreCase("Rank")) {
                this.difficulty = Utils.color(value);
            }

            if (key.equalsIgnoreCase("Quest Name")) {
                name = Utils.color(value);
            }

            if (key.equalsIgnoreCase("Quest Lore")) {
                description = value;
            }

            if (key.equalsIgnoreCase("Duration")) {
                int questDuration = rand.nextInt(3600) + 300;
                try {
                    questDuration = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    //
                }
                duration = questDuration;
            }

            if (key.equalsIgnoreCase("Rewards")) {
                Pattern bracketPattern = Pattern.compile("\\{(.*?)}");
                Matcher objectivesMatch = bracketPattern.matcher(value);

                while (objectivesMatch.find()) {
                    String[] rewardKeyValue = objectivesMatch.group(1).split("%");
                    if (rewardKeyValue.length != 2) continue;
                    String rewardType = rewardKeyValue[0].trim();
                    String rewardValue = rewardKeyValue[1].trim();

                    if (rewardType.equalsIgnoreCase("Money")) {
                        try {
                            moneyRewards = Integer.parseInt(rewardValue);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    if (rewardType.equalsIgnoreCase("Experience")) {
                        try {
                            experienceRewards = Integer.parseInt(rewardValue);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    if (rewardType.equalsIgnoreCase("Item Type")) {
                        itemType = rewardValue;
                    }

                    if (rewardType.equalsIgnoreCase("Item Rank")) {
                        itemRank = rewardValue;
                    }

                    if (rewardType.equalsIgnoreCase("Item Name")) {
                        itemName = rewardValue;
                    }
                }
            }

            if (key.equalsIgnoreCase("Objectives")) {
                // Separated each objective using <->
                String[] listOfObjectives = value.split("<->");

                for (String obj : listOfObjectives) {
                    Pattern bracketPattern = Pattern.compile("\\{(.*?)}");
                    Matcher objectivesMatch = bracketPattern.matcher(obj);

                    // This is the main objective object.
                    final HashMap<String, Object> objectivesObject = new HashMap<>();

                    // This will loop on all parameters
                    while (objectivesMatch.find()) {
                        String[] objectivesKeyValue = objectivesMatch.group(1).split("%");
                        if (objectivesKeyValue.length != 2) continue;

                        String objectiveKey = objectivesKeyValue[0].trim();
                        String objectiveValue = objectivesKeyValue[1].trim();

                        System.out.println("OBJECTIVE KEY: " + objectiveKey);
                        System.out.println("OBJECTIVE VALUE: " + objectiveValue);

                        // Type of objective like break walk kill
                        if (objectiveKey.equalsIgnoreCase("Objective")) {
                            objectivesObject.put(ObjectiveStrings.OBJECTIVE_OBJECTIVE, objectiveValue);
                            String perms = null;
                            if (objectiveValue.equalsIgnoreCase("BREAK")) {
                                perms = ObjectiveStrings.BREAK;
                            } else if (objectiveValue.equalsIgnoreCase("WALK")) {
                                perms = ObjectiveStrings.WALK;
                            } else if (objectiveValue.equalsIgnoreCase("KILL")) {
                                perms = ObjectiveStrings.KILL;
                            } else if (objectiveValue.equalsIgnoreCase("HARVEST")) {
                                perms = ObjectiveStrings.HARVEST;
                            } else if (objectiveValue.equalsIgnoreCase("PLANT")) {
                                perms = ObjectiveStrings.PLANT;
                            } else if (objectiveValue.equalsIgnoreCase("CRAFT")) {
                                perms = ObjectiveStrings.CRAFT;
                            } else if (objectiveValue.equalsIgnoreCase("ENCHANT")) {
                                perms = ObjectiveStrings.ENCHANT;
                            } else if (objectiveValue.equalsIgnoreCase("FLY")) {
                                perms = ObjectiveStrings.FLY;
                            }
                            if (perms == null) continue;
                            player.addAttachment(AdventureCraftCore.getInstance(), perms, true);
                        }

                        // The title of the objective
                        if (objectiveKey.equalsIgnoreCase(ObjectiveStrings.OBJECTIVE_TITLE)) {
                            objectivesObject.put(objectiveKey, objectiveValue);
                        }

                        // Type of materials or entities or environment like overworld nether end
                        if (objectiveKey.equalsIgnoreCase(ObjectiveStrings.OBJECTIVE_TYPE)) {
                            objectivesObject.put(objectiveKey, cleanString(objectiveValue));
                        }

                        // Enchant levels
                        if (objectiveKey.equalsIgnoreCase(ObjectiveStrings.OBJECTIVE_ENCHANT_LEVEL)) {
                            int level = rand.nextInt(3) + 1;
                            try {
                                level = Integer.parseInt(objectiveValue);
                            } catch (NumberFormatException e) {
                                //
                            }
                            objectivesObject.put(objectiveKey, level);
                        }

                        // The biome in which the quest should be made
                        if (objectiveKey.equalsIgnoreCase(ObjectiveStrings.OBJECTIVE_BIOME)) {
                            try {
                                String biomeName = cleanString(objectiveValue);
                                Biome biome = Biome.valueOf(biomeName.toUpperCase());
                                objectivesObject.put(objectiveKey, biome);
                            } catch (IllegalArgumentException | NullPointerException e) {
                                AdventureCraftCore.getInstance().getLogger().warning("Biomes Exception");
                                e.printStackTrace();
                            }
                        }

                        // Times to do the action
                        if (objectiveKey.equalsIgnoreCase(ObjectiveStrings.OBJECTIVE_COUNT)) {
                            int objectiveCount = rand.nextInt(100) + 1;
                            try {
                                objectiveCount = Integer.parseInt(objectiveValue);
                            } catch (NumberFormatException e) {
                                //
                            }
                            objectivesObject.put(objectiveKey, objectiveCount);
                        }


                        // Y Coordinate
                        if (objectiveKey.equalsIgnoreCase(ObjectiveStrings.OBJECTIVE_START_Y)) {
                            int objectiveStartY = rand.nextInt(200) + 1;
                            try {
                                objectiveStartY = Integer.parseInt(objectiveValue);
                            } catch (NumberFormatException e) {
                                //
                            }
                            objectivesObject.put(objectiveKey, objectiveStartY);
                        }

                        // Y Coordinate
                        if (objectiveKey.equalsIgnoreCase(ObjectiveStrings.OBJECTIVE_END_Y)) {
                            int objectiveEndY = rand.nextInt(200) + 1;
                            try {
                                objectiveEndY = Integer.parseInt(objectiveValue);
                            } catch (NumberFormatException e) {
                                //
                            }
                            objectivesObject.put(objectiveKey, objectiveEndY);
                        }

                        // The time of the day
                        if (objectiveKey.equalsIgnoreCase(ObjectiveStrings.OBJECTIVE_TIME_START)) {
                            int objectiveTimeStart = rand.nextInt(200) + 1;
                            try {
                                objectiveTimeStart = Integer.parseInt(objectiveValue);
                            } catch (NumberFormatException e) {
                                //
                            }
                            objectivesObject.put(objectiveKey, objectiveTimeStart);
                        }

                        // The time of the day
                        if (objectiveKey.equalsIgnoreCase(ObjectiveStrings.OBJECTIVE_TIME_END)) {
                            int objectiveTimeEnd = rand.nextInt(200) + 1;
                            try {
                                objectiveTimeEnd = Integer.parseInt(objectiveValue);
                            } catch (NumberFormatException e) {
                                //
                            }
                            objectivesObject.put(objectiveKey, objectiveTimeEnd);
                        }
                        objectivesObject.put(ObjectiveStrings.OBJECTIVE_PROGRESS, 0);
                    }

                    this.objectives.put(UUID.randomUUID().toString(), objectivesObject);
                }
            }
        }
        addRewards();
        questUUID = UUID.randomUUID();
        player.sendMessage(Utils.color("&6A new quest has arrive!"));
    }

    private void addRewards() {
        String instruction =
            """
            In the expansive and ever-evolving world of Minecraft, you assume the crucial role of the Game Master, overseeing the game and responsible for creating new captivating items as rewards that significantly impact players' adventures in the pixelated realm. These items are categorized into nine distinct ranks: Common, Uncommon, Rare, Epic, Legendary, Mythical, Fabled, Godlike, and Ascended. These rewards should be related to the type of quest they received. The item's rank should also match the quest difficulty, there are 5 quest difficulty, Novice, Intermediate, Advanced, Heroic, and Legendary. Low-rank items for low-rank quests and high-rank items for high-rank quests. Make it visually appealing by adding multiple colors on each sentence using the Spigot API color codes like, &c for Red, &b for Aqua, etc.
                                                                                                                                                                                                                                        
            When creating a custom item, you can use the following options.
            Type: The material for this item.
            Name: The name of this item.
            Lore: A list of lores separated by "%".
            Enchantments: The enchantments that should be applied to this item; the enchantment level can exceed the default maximum level up to level 100. Enchantment must be able to apply to the item. Can be a negative enchantment to balance it out.
            Attributes: The added attributes if the item is equipped or is being held on the main hand or off-hand. This attribute is the enum found in Spigot API, (e.g., GENERIC_MAX_HEALTH). The modifier can be a negative value to balance it out.
            Additional Lore: The lore for the enchantment and attributes, should match the one you provided.
            
            For the materials and color codes, use the Spigot API enum to get their correct values.
            
            Format the final output in a single line. Just provide the item data, don't include redundant information. Make sure the pattern is correct.
            [Type|Material][Name|Item Name][Lore|{Lore 1%Lore 2%Lore 3}][Rank|Item Rank][Enchantments|{Enchants%Level}{Enchants%Level}][Attributes|{Attribute%Double Modifier}][Additional Lore|{Attribute Lore 1%Attribute Lore 2}]""";

        String message =
            "Quest Difficulty: " + cleanString(difficulty) +"\n" +
            "Quest Title: " + name + "\n" +
            "Quest Description: " + description + "\n" +
            "Quest Duration: " + duration + "\n\n" +
            "Create a " + itemType + " item reward named " + itemName + " with a rank of " + itemRank;

        String json = AdventureCraftCore.getInstance().getOpenai().generate(message, instruction);
        if (json == null) {
            AdventureCraftCore.getInstance().getLogger().info("No item found");
            return;
        }

        Pattern bracket = Pattern.compile("\\[(.*?)]", Pattern.DOTALL);
        Matcher bracketMatch = bracket.matcher(json);

        Material material = null;
        String itemName = "";
        List<String> lores = new ArrayList<>();
        HashMap<Enchantment, Integer> enchantments = new HashMap<>();
        HashMap<Attribute, AttributeModifier> attributes = new HashMap<>();

        while (bracketMatch.find()) {
            String[] keyValue = bracketMatch.group(1).split("\\|");
            if (keyValue.length != 2) continue;
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            if (key.equalsIgnoreCase("Type")) {
                try {
                    material = Material.valueOf(cleanString(value));
                } catch (IllegalArgumentException | NullPointerException e) {
                    e.printStackTrace();
                }
            }

            if (key.equalsIgnoreCase("Name")) {
                itemName = Utils.color(value);
            }

            if (key.equalsIgnoreCase("Lore")) {
                Pattern percentage = Pattern.compile("\\{(.*?)}");
                Matcher percentageMatch = percentage.matcher(value);

                while (percentageMatch.find()) {
                    String[] additionalLoreKeys = percentageMatch.group(1).split("%");

                    for (String str : additionalLoreKeys) {
                        lores.addAll(getLore(Utils.color(str)));
                        lores.add("");
                    }
                }
            }

            if (key.equalsIgnoreCase("Enchantments")) {
                Pattern percentage = Pattern.compile("\\{(.*?)}");
                Matcher percentageMatch = percentage.matcher(value);

                while (percentageMatch.find()) {
                    String[] enchantmentsKeyValue = percentageMatch.group(1).split("%");
                    if (enchantmentsKeyValue.length != 2) continue;
                    Enchantment enchantment = null;
                    try {
                        String enchantName = cleanString(enchantmentsKeyValue[0]).toLowerCase();
                        System.out.println(enchantName);
                        enchantment = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(enchantName));
                    } catch (IllegalArgumentException | NullPointerException e) {
                        e.printStackTrace();
                    }
                    if (enchantment == null) continue;
                    int level = rand.nextInt(enchantment.getMaxLevel()) + 1;
                    try {
                        level = Integer.parseInt(enchantmentsKeyValue[1]);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    enchantments.put(enchantment, level);
                }
            }

            if (key.equalsIgnoreCase("Attributes")) {
                Pattern percentage = Pattern.compile("\\{(.*?)}");
                Matcher percentageMatch = percentage.matcher(value);

                while (percentageMatch.find()) {
                    String[] attributeKeyvalue = percentageMatch.group(1).split("%");
                    if (attributeKeyvalue.length != 2) continue;
                    String attributeToModify = attributeKeyvalue[0];
                    String modifier = attributeKeyvalue[1];

                    Attribute attribute = null;
                    try {
                        attribute = Attribute.valueOf(cleanString(attributeToModify));
                    } catch (IllegalArgumentException | NullPointerException e) {
                        e.printStackTrace();
                    }
                    if (attribute == null) continue;

                    double modiferAmount = 0.0;
                    try {
                        modiferAmount = Double.parseDouble(modifier);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    AttributeModifier attributeModifier = new AttributeModifier(UUID.randomUUID(), attributeToModify, modiferAmount, AttributeModifier.Operation.ADD_NUMBER);
                    attributes.put(attribute, attributeModifier);
                }
            }

            if (key.equalsIgnoreCase("Additional Lore")) {
                Pattern percentage = Pattern.compile("\\{(.*?)}");
                Matcher percentageMatch = percentage.matcher(value);

                while (percentageMatch.find()) {
                    String[] additionalLoreKeys = percentageMatch.group(1).split("%");

                    for (String str : additionalLoreKeys) {
                        lores.add("");
                        lores.addAll(getLore(Utils.color(str)));
                    }
                }
            }
        }

        if (material == null) return;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.setDisplayName(itemName);
        meta.setLore(lores);

        for (Enchantment ench : enchantments.keySet()) {
            int level = enchantments.get(ench);
            meta.addEnchant(ench, level, true);
        }

        for (Attribute attribute : attributes.keySet()) {
            AttributeModifier modifier = attributes.get(attribute);
            meta.addAttributeModifier(attribute, modifier);
        }

        item.setItemMeta(meta);
        itemRewards = item;
    }

    private List<String> getLore(String input) {
        if (input == null || input.equals("")) return new ArrayList<>();
        List<String> resultList = new ArrayList<>();
        Pattern pattern = Pattern.compile("(&|§)."); // Pattern to match special characters like "&6"

        String[] words = input.split("\\s+"); // Split the input text into words

        String lastSpecialChar = ""; // Track the last special character

        for (int i = 0; i < words.length; i += 5) {
            StringBuilder lineBuilder = new StringBuilder();
            for (int j = i; j < i + 5 && j < words.length; j++) {
                String currentWord = words[j];

                // Check for a special character in the current word
                Matcher matcher = pattern.matcher(currentWord);
                if (matcher.find()) {
                    System.out.println("COLOR: " + lastSpecialChar);
                    lastSpecialChar = matcher.group();
                }

                // Append the word to the line
                lineBuilder.append(lastSpecialChar).append(currentWord).append(" ");
            }

            System.out.println("COLOR: " + lastSpecialChar);

            // Add the line to the result list
            String colored = Utils.color(lineBuilder.toString().trim());
            resultList.add(colored);
        }
        return resultList;
    }

    private String cleanString(String input) {
        if (input == null || input == "") return "";
        String[] split = input.split("\\.");

        String value = input;
        if (split.length >= 2) {
            value = split[1];
        }

        String finalString = value.toUpperCase().replace(" ", "_");
        return ChatColor.stripColor(finalString);
    }

    public void updateQuest(String type, Player player, Location location, Material material, EntityType entityType, HashMap<Enchantment, Integer> enchantments) {
        HashMap<String, Object> objectives = null;
        if (this.objectives.containsKey(type)) {
            objectives = this.objectives.get(type);
        }
        if (objectives == null) return;

        if (type.equalsIgnoreCase(ObjectiveStrings.BREAK) || type.equalsIgnoreCase(ObjectiveStrings.CRAFT)) {
            if (objectives.containsKey(ObjectiveStrings.OBJECTIVE_TYPE)) {
                String targetMaterial = (String) objectives.get(ObjectiveStrings.OBJECTIVE_TYPE);
                if (!targetMaterial.equalsIgnoreCase(material.toString())) return;
            }
        } else if (type.equalsIgnoreCase(ObjectiveStrings.WALK) || type.equalsIgnoreCase(ObjectiveStrings.FLY)) {
            String targetEnvironment = (String) objectives.get(ObjectiveStrings.OBJECTIVE_TYPE);
            String environment = String.valueOf(Objects.requireNonNull(location.getWorld()).getEnvironment());
            if (!targetEnvironment.equalsIgnoreCase(environment)) return;
        } else if (type.equalsIgnoreCase(ObjectiveStrings.KILL)) {
            String targetEntity = (String) objectives.get(ObjectiveStrings.OBJECTIVE_TYPE);
            if (!entityType.toString().equalsIgnoreCase(targetEntity)) return;
        } else if (type.equalsIgnoreCase(ObjectiveStrings.HARVEST) || type.equalsIgnoreCase(ObjectiveStrings.PLANT)) {
            String targetPlantType = (String) objectives.get(ObjectiveStrings.OBJECTIVE_TYPE);
            if (!material.toString().equalsIgnoreCase(targetPlantType)) return;
        } else if (type.equalsIgnoreCase(ObjectiveStrings.ENCHANT)) {
            String targetEnchantment = (String) objectives.get(ObjectiveStrings.OBJECTIVE_TYPE);
            int targetLevel = (int) objectives.get(ObjectiveStrings.OBJECTIVE_ENCHANT_LEVEL);
            boolean proceed = false;
            for (Enchantment ench : enchantments.keySet()) {
                String enchant = ench.getKey().getKey();
                int level = enchantments.get(ench);
                if (enchant.equalsIgnoreCase(targetEnchantment)) continue;
                if (level != targetLevel) continue;
                proceed = true;
                break;
            }
            if (!proceed) return;
        }

        // Check the biome
        if (objectives.containsKey(ObjectiveStrings.OBJECTIVE_BIOME)) {
            Biome targetBiome = (Biome) objectives.get(ObjectiveStrings.OBJECTIVE_BIOME);
            if (!targetBiome.equals(location.getBlock().getBiome())) return;
        }

        // Check the y Coords
        if (objectives.containsKey(ObjectiveStrings.OBJECTIVE_START_Y) && objectives.containsKey(ObjectiveStrings.OBJECTIVE_END_Y)) {
            int yCoordinate = location.getBlockY();
            // Check Y Coordinate
            int minY = (int) objectives.get(ObjectiveStrings.OBJECTIVE_START_Y);
            int maxY = (int) objectives.get(ObjectiveStrings.OBJECTIVE_END_Y);

            if (!isBetweenTwoNumber(yCoordinate, minY, maxY)) return;
        }

        // Check the time
        if (objectives.containsKey(ObjectiveStrings.OBJECTIVE_TIME_START) && objectives.containsKey(ObjectiveStrings.OBJECTIVE_TIME_END)) {
            int time = (int) player.getWorld().getTime();
            // Check Time
            int minTime = (int) objectives.get(ObjectiveStrings.OBJECTIVE_TIME_START);
            int maxTime = (int) objectives.get(ObjectiveStrings.OBJECTIVE_TIME_END);

            if (!isBetweenTwoNumber(time, minTime, maxTime)) return;
        }

        int targetCount = (int) objectives.get(ObjectiveStrings.OBJECTIVE_COUNT);
        int progress = (int) objectives.get(ObjectiveStrings.OBJECTIVE_PROGRESS);
        progress++;

        if (progress < targetCount) {
            objectives.replace(ObjectiveStrings.OBJECTIVE_PROGRESS, progress);
            this.objectives.replace(type, objectives);
            return;
        }

        List<String> completionMessage = new ArrayList<>();
        String message = "&aQuest Completed: &a%type%&r - Rank: &c%rank%&r - Success! You received a reward!";
        String coloredMessage = Utils.color(message.replace("%type%", cleanString(name)).replace("%rank%", cleanString(difficulty)));

        completionMessage.add("");
        completionMessage.add("");
        completionMessage.add(coloredMessage);
        completionMessage.add("");
        completionMessage.add("&bRewards");
        if (moneyRewards != -1) {
            completionMessage.add("&6- $" + moneyRewards);
        }
        if (experienceRewards != -1) {
            completionMessage.add("&2- " + experienceRewards + " exp.");
        }
        if (itemName != null) {
            String name = Utils.color(itemName) + " " + Utils.color(getItemRankColored(itemRank));
            completionMessage.add(name);
        }

        // Add rewards
        Utils.giveItemToPlayer(itemRewards, player);

        for (String str : completionMessage) {
            player.sendMessage(Utils.color(str));
        }

        // Clear the quest
        AdventureCraftCore.getInstance().getOnGoingQuest().removeQuest(playerUUID, questUUID);
    }

    public ItemStack getFormattedQuestPaper() {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        if (meta == null) return null;
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>(getLore(description));
        lore.add("");
        lore.add(Utils.color("&3Objectives"));
        for (String uuid : objectives.keySet()) {
            System.out.println("UUID: " + uuid);

            HashMap<String, Object> objective = objectives.get(uuid);

            String title = (String) objective.get(ObjectiveStrings.OBJECTIVE_TITLE);
            String type = (String) objective.get(ObjectiveStrings.OBJECTIVE_TYPE);
            String objectiveType = (String) objective.get(ObjectiveStrings.OBJECTIVE_OBJECTIVE);

            lore.add(Utils.color("&d◆ " + title));
            lore.add(Utils.color("&b➤ &7Action: &3" + cleanString(objectiveType)));

            String finalTargetType = "&3" + cleanString(capitalizeFirstLetter(type).replace("_", " "));
            if (objectiveType.equalsIgnoreCase("Break")) {
                lore.add(Utils.color("&b➤ &7Block: " + finalTargetType));
            } else if (objectiveType.equalsIgnoreCase("Craft")) {
                lore.add(Utils.color("&b➤ &7Item: " + finalTargetType));
            } else if (objectiveType.equalsIgnoreCase("Walk") || objectiveType.equalsIgnoreCase("Fly")) {
                lore.add(Utils.color("&b➤ &7World Type: " + finalTargetType));
            } else if (objectiveType.equalsIgnoreCase("Kill")) {
                lore.add(Utils.color("&b➤ &7Entity: " + finalTargetType));
            } else if (objectiveType.equalsIgnoreCase("Harvest") || objectiveType.equalsIgnoreCase("Plant")) {
                lore.add(Utils.color("&b➤ &7Crops: " + finalTargetType));
            } else if (objectiveType.equalsIgnoreCase("Enchant")) {
                int level = (int) objective.get(ObjectiveStrings.OBJECTIVE_ENCHANT_LEVEL);
                lore.add(Utils.color("&b➤ &7Enchantment: " + finalTargetType));
                lore.add(Utils.color("&b➤ &7Enchantment: " + level));
            }

            if (objective.containsKey(ObjectiveStrings.OBJECTIVE_COUNT)) {
                int count = (int) objective.get(ObjectiveStrings.OBJECTIVE_COUNT);
                lore.add(Utils.color("&b➤ &7Goal: &3" + count + "x"));
            }
            if (objective.containsKey(ObjectiveStrings.OBJECTIVE_BIOME)) {
                Biome targetBiome = (Biome) objective.get(ObjectiveStrings.OBJECTIVE_BIOME);
                String biome = capitalizeFirstLetter(targetBiome.name().replace("_", " "));
                lore.add(Utils.color("&b➤ &7Biome: &3" + biome));
            }
            // Check the y Coords
            if (objective.containsKey(ObjectiveStrings.OBJECTIVE_START_Y) && objective.containsKey(ObjectiveStrings.OBJECTIVE_END_Y)) {
                // Check Y Coordinate
                int minY = (int) objective.get(ObjectiveStrings.OBJECTIVE_START_Y);
                int maxY = (int) objective.get(ObjectiveStrings.OBJECTIVE_END_Y);
                lore.add(Utils.color("&b➤ &7Between &3" + minY + "y&7 and &3" + maxY + "y"));
            }
            // Check the time
            if (objective.containsKey(ObjectiveStrings.OBJECTIVE_TIME_START) && objective.containsKey(ObjectiveStrings.OBJECTIVE_TIME_END)) {
                // Check Time
                int minTime = (int) objective.get(ObjectiveStrings.OBJECTIVE_TIME_START);
                int maxTime = (int) objective.get(ObjectiveStrings.OBJECTIVE_TIME_END);
                lore.add(Utils.color("&b➤ &7Between the time of &3" + minTime + "&7 and &3" + maxTime + "&7"));
            }
            lore.add("");
        }
        if (difficulty != null && !difficulty.equals("")) {
            lore.add(Utils.color("&3Quest Rank: " + difficulty));
            lore.add("");
        }
        lore.add(Utils.color("&bRewards"));
        if (moneyRewards != -1) {
            lore.add(Utils.color("&6- $" + moneyRewards));
        }
        if (experienceRewards != -1) {
            lore.add(Utils.color("&2- " + experienceRewards + " exp."));
        }
        if (itemName != null) {
            String name = Utils.color("&b➤ Item: " + itemName) + " " + Utils.color(getItemRankColored(itemRank));
            lore.add("");
            lore.add(Utils.color("&bRanked Item"));
            lore.add(name);
            lore.add(Utils.color("&b➤ &7Rank: " + getItemRankColored(itemRank)));
        }
        lore.add("");
        lore.add(Utils.color("&cTime &7Remaining: " + convertSecondsToTime(duration)));
        meta.setLore(lore);
        paper.setItemMeta(meta);
        return paper;
    }

    private String getItemRankColored(String rank) {
        if (rank.equalsIgnoreCase(ObjectiveStrings.UNCOMMON)) {
            return "&a" + rank;
        } else if (rank.equalsIgnoreCase(ObjectiveStrings.RARE)) {
            return "&9" + rank;
        } else if (rank.equalsIgnoreCase(ObjectiveStrings.EPIC)) {
            return "&5" + rank;
        } else if (rank.equalsIgnoreCase(ObjectiveStrings.LEGENDARY)) {
            return "&6" + rank;
        } else if (rank.equalsIgnoreCase(ObjectiveStrings.MYTHICAL)) {
            return "&d" + rank;
        } else if (rank.equalsIgnoreCase(ObjectiveStrings.FABLED)) {
            return "&b" + rank;
        } else if (rank.equalsIgnoreCase(ObjectiveStrings.GODLIKE)) {
            return "&4" + rank;
        } else if (rank.equalsIgnoreCase(ObjectiveStrings.ASCENDED)) {
            return "&e" + rank;
        } else {
            return "&7" + rank;
        }
    }

    private static String convertSecondsToTime(int totalSeconds) {
        if (totalSeconds < 0) {
            throw new IllegalArgumentException("Total seconds must be non-negative.");
        }

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        // Format the time as "hh:mm:ss"
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private boolean isBetweenTwoNumber(int number, int min, int max) {
        return number >= min && number <= max;
    }
    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getDifficulty() {
        return difficulty;
    }
    public int getDuration() {
        return duration;
    }
    public ItemStack getItemRewards() {
        return itemRewards;
    }
    public int getExperienceRewards() {
        return experienceRewards;
    }
    public int getMoneyRewards() {
        return moneyRewards;
    }
    public String getItemType() {
        return itemType;
    }
    public String getItemRank() {
        return itemRank;
    }
    public String getItemName() {
        return itemName;
    }
    public HashMap<String, HashMap<String, Object>> getObjectives() {
        return objectives;
    }
    public UUID getQuestUUID() {
        return questUUID;
    }
}