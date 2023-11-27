package com.peepersoak.adventurecraftcore.openAI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.utils.EnchantmentKeys;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class OpenAI {
    private final EnchantmentKeys enchantmentKeys = new EnchantmentKeys();
    private final String OPEN_AI_KEY;
    private final String OPEN_AI_MODEL;
    private final Random random = new Random();

    public OpenAI() {
        OPEN_AI_KEY = AdventureCraftCore.getInstance().getConfig().getString("OPEN_AI_KEY");
        OPEN_AI_MODEL = AdventureCraftCore.getInstance().getConfig().getString("MODEL");
    }

    public String generate(String message, String instruction) {
        try {
            String ENDPOINT = "https://api.openai.com/v1/chat/completions";
            URL url = new URL(ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            // Set headers, including the Authorization header with your API key
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + OPEN_AI_KEY);
            // Enable input and output streams
            connection.setDoOutput(true);

            // Send the request body (JSON payload)
            // Adjust the payload based on the specific OpenAI API and task
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode messages = objectMapper.createArrayNode();

            ObjectNode system = objectMapper.createObjectNode();
            system.put("role", "system");
            system.put("content", instruction);

            ObjectNode user = objectMapper.createObjectNode();
            user.put("role", "user");
            user.put("content", message);

            messages.add(system);
            messages.add(user);

            ObjectNode format = objectMapper.createObjectNode();
            format.put("type", "json_object");

            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("model", OPEN_AI_MODEL);
            objectNode.set("messages", messages);
            objectNode.set("response_format", format);

            String jsonString = objectMapper.writeValueAsString(objectNode);

            // Get the output stream from the connection
            try (OutputStream outputStream = connection.getOutputStream()) {
                // Write the JSON string to the output stream
                byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }

            // Get the API response
            int responseCode = connection.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            ObjectMapper reponseMap = new ObjectMapper();
            JsonNode responseNode = reponseMap.readTree(response.toString());

            String finalSettings = null;
            if (responseNode.has("choices") && responseNode.get("choices").isArray()) {
                ArrayNode choices = (ArrayNode) responseNode.get("choices");
                if (!choices.isEmpty()) {
                    JsonNode choice = choices.get(0);
                    if (choice.has("message")) {
                        JsonNode messageObj = choice.get("message");
                        if (messageObj.has("content")) {
                            finalSettings = messageObj.get("content").asText();
                        }
                    }
                }
            }

            return finalSettings;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public QuestData createQuest(String message, UUID playerUUID) {
        // This that I must generate my self
        // Duration
        // All objectives
        //


        String instruction =
            """
            In the vast and ever-changing world of Minecraft, you play the crucial role of the Game Master, overseeing the game and responsible for creating captivating quests and rewards that significantly impact players' adventures in the pixelated realm. These quests and items are categorized into five distinct ranks: Common, Uncommon, Rare, Epic, Legendary, Mythical, Fabled, Godlike, and Ascended. It's essential to align each quest with players' ongoing activities, seamlessly integrating with their current pursuits. Higher-difficulty quests come with more multiple challenging objectives and requirements, often in the range of thousands but with better rewards.
            
            When adding color, use the Spigot API color code (e.g., &c for red, &b for aqua etc.).
                        
            Here are the list of objectives that you can use:
            1. Break
            2. Walk
            3. Fly
            4. Kill
            5. Harvest
            6. Craft
            7. Enchant
            8. Fishing
                
            Make sure you only use enum found in Minecraft or Spigot API for Material, EntityType, Biome, Enchantment, and Attribute.
                
            Final output should be a valid JSON object.
            {
                "Rank": "A color coded quest rank",
                "Quest Name": "A color coded quest name",
                "Quest Lore": "A color coded quest lore",
                "Duration": "The duration for this quest in seconds, higher difficulty have higher duration",
                
                // A minimum of 3 objectives is required. Assign a higher number of objectives for higher-ranked quests.
                "Objectives": [
                    {
                        "Objective": "Type of objective (e.g., Break, Kill etc.)"
                        "Title": "A color coded title",
                        "Material": "Type of material, If the objective is, Break, Harvest, Craft, Enchant, Fishing",
                        "EntityType": "Type of the entity if the objective is Kill",
                        "Enchantment": "Type of enchantment if the objective is Enchant",
                        "Level" : "Enchantment level required if the objective is Enchant",
                        "Count": "Target amount (e.g Blocks to break, crops to plant, items to enchant) in numbers",
                        "Optional": {
                            "World": "World type to use, choose one of the following (OVERWORLD, THE_NETHER, THE_END)",
                            "Biome": "A valid biome found in Spigot API (e.g., Dessert) must be present in the given world type",
                            "StartY": "Start Y coordinate",
                            "EndY": "End Y coordinate,
                            "TimeStart": "Time of day, range (0-23000)",
                            "TimeEnd": "Time of day, range (0-23000)"
                        }
                    }
                    // Add 2 more objectives.
                ]
                "Rewards": {
                    "Money": "Amount",
                    "Experience": "Amount",
                    
                    // Can be given to any ranks but commonly found in lower ranks
                    "Regular Items": [
                        {
                            "Material": "Material of item found in Spigot API (e.g., DIAMOND_PICKAXE)",
                            "Amount": "Amount of items to give",
                        }
                    ]
                    
                    // Custom item is optional, most commonly found in high rank quest.
                    "Custom Item": {
                        "Rank": "A color coded rank",
                        "Type": "Valid Material from Spigot API (e.g., EMERALD)",
                        "Name": "A color coded name for the item",
                        "Lore": "A color coded in-depth lore for this item",
                        "Optional": {
                            // Optional is most commonly found in high rank items.
                            "Enchantments": [
                                {
                                    "Enchantment": "The enchantment key enum found in Minecraft (e.g., SHARPNESS), can be a negative enchantment (e.g., BINDING_CURSE)"
                                    "Level": "The enchantment level can range from 1 to 100, higher rank gives higher level, exceeding the default maximum level."
                                }
                                // Add more enchantment if needed
                            ],
                            "Attributes": [
                                {
                                    "Attribute": "This attribute is the enum found in Spigot API, (e.g., GENERIC_MAX_HEALTH). The modifier can be a negative value to balance it out."
                                    "Modifier": "A double number which can be a positive or negative, to be applied to the attribute (e.g., -0.2)"
                                }
                                // Add more attribute if needed
                            ],
                            "Trim Pattern": "Choose one from this, SENTRY, VEX, WILD, COAST, DUNE, WAYFINDER, RAISER, SHAPER, HOST, WARD, SILENCE, TIDE, SNOUT, RIB, EYE, SPIRE",
                            "Trim Material": "Choose one from this, NETHERITE, DIAMOND, EMERALD, GOLD, COPPER, REDSTONE, AMETHYST, IRON, LAPIS, QUARTZ"
                        },
                        "Extra Lore": "A color coded lore to give more details about the enchantment and attributes (e.g., The user will gain more vitality)"
                    }
                },
            }
            """;
        String dataObject = generate(message, instruction);
        System.out.println(dataObject);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode object = mapper.readTree(dataObject);

            String questRank;
            String questName;
            String questLore;
            int duration;
            int totalDuration;

            // List of objectives
            // String is the objectives UUID
            // String is objective key e.g., Material, EntityTYpe, Title etc.
            // Object is the objective value e.g., DIAMOND, ZOMBIE
            HashMap<UUID, Objective> allObjectives = new HashMap<>();

            // Get the all quest data
            if (!object.has(ObjectiveStrings.QUEST_RANK)) {
                System.out.println("NO RANK");
                return null;
            }
            questRank = object.get(ObjectiveStrings.QUEST_RANK).asText();

            if (!object.has(ObjectiveStrings.QUEST_NAME)) {
                System.out.println("NO NAME");
                return null;
            }
            questName = object.get(ObjectiveStrings.QUEST_NAME).asText();

            if (!object.has(ObjectiveStrings.QUEST_LORE)) {
                System.out.println("NO LORE");
                return null;
            }
            questLore = object.get(ObjectiveStrings.QUEST_LORE).asText();

            if (!object.has(ObjectiveStrings.QUEST_DURATION)) {
                System.out.println("NO DURATION");
                return null;
            }
            duration = object.get(ObjectiveStrings.QUEST_DURATION).asInt();
            // This will make sure that every task has a minimum duration of 10 minutes
            if (duration <= 600) {
                duration = 600;
            }


            // Get the objectives
            if (!object.has(ObjectiveStrings.QUEST_OBJECTIVES) || !object.get(ObjectiveStrings.QUEST_OBJECTIVES).isArray()) {
                System.out.println("NO OBJECTIVES");
                return null;
            }

            ArrayNode listOfObjectives = (ArrayNode) object.get(ObjectiveStrings.QUEST_OBJECTIVES);

            int minimumCalculatedDuration = 0;
            for (int i = 0; i < listOfObjectives.size() ; i ++) {
                // This is the objective json object
                JsonNode objective = listOfObjectives.get(i);

                // This is the objective type like Break, Walk
                String type = null;
                String title = null;
                String material = null;
                String entityType = null;
                String enchantment = null;
                int level = -1;
                int totalCount = -1;
                String world = null;
                String biome = null;
                int startY = -300;
                int endY = -300;
                int timeStart = -1;
                int timeEnd = -1;

                if (!objective.has(ObjectiveStrings.QUEST_OBJECTIVE)) continue;
                type = objective.get(ObjectiveStrings.QUEST_OBJECTIVE).asText();
                if (!AdventureCraftCore.getInstance().getQuestListChecker().isValidObjective(type)) continue;

                if (!objective.has(ObjectiveStrings.QUEST_OBJECTIVE_TITLE)) continue;
                title = objective.get(ObjectiveStrings.QUEST_OBJECTIVE_TITLE).asText();

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

                if (isBreak ||
                isPlace ||
                isHarvest ||
                isPlant ||
                isCraft ||
                isEnchant ||
                isFishing) {
                    if (!objective.has(ObjectiveStrings.QUEST_OBJECTIVE_MATERIAL)) continue;
                    String givenMaterial = objective.get(ObjectiveStrings.QUEST_OBJECTIVE_MATERIAL).asText();
                    if (givenMaterial == null) continue;
                    String convert = Utils.cleanString(givenMaterial).toUpperCase();
                    if (!Utils.isValidMaterial(convert)) continue;
                    if (isCraft) {
                        if (!Utils.isCraftable(convert)) continue;
                    }
                    if (isHarvest) {
                        if (!AdventureCraftCore.getInstance().getQuestListChecker().isHarvestable(convert)) continue;
                    }
                    if (isBreak || isPlace) {
                        if (AdventureCraftCore.getInstance().getQuestListChecker().isUnbreakable(convert)) continue;
                        if (!Utils.isValidBreakableBlock(convert)) continue;;
                    }
                    if (isFishing) {
                        if (!AdventureCraftCore.getInstance().getQuestListChecker().getFishable(convert)) continue;
                    }
                    material = convert;
                }

                if (isKill) {
                    if (!objective.has(ObjectiveStrings.QUEST_OBJECTIVE_ENTITY_TYPE)) continue;
                    String givenEntityType = objective.get(ObjectiveStrings.QUEST_OBJECTIVE_ENTITY_TYPE).asText();
                    material = AdventureCraftCore.getInstance().getQuestListChecker().getRandomWeapon().getKey().getKey();
                    if (givenEntityType == null) continue;
                    String convert = Utils.cleanString(givenEntityType).toUpperCase();
                    if (!Utils.isValidEntity(convert)) continue;
                    if (AdventureCraftCore.getInstance().getQuestListChecker().isDisabledEntity(convert)) continue;
                    entityType = convert;
                }

                if (isEnchant) {
                    if (!objective.has(ObjectiveStrings.QUEST_OBJECTIVE_ENCHANTMENT) || !objective.has(ObjectiveStrings.QUEST_OBJECTIVE_LEVEL)) continue;
                    String rawEnchantment = objective.get(ObjectiveStrings.QUEST_OBJECTIVE_ENCHANTMENT).asText();
                    enchantment = enchantmentKeys.convertToMinecraftKey(Utils.cleanString(rawEnchantment));
                    level = objective.get(ObjectiveStrings.QUEST_OBJECTIVE_LEVEL).asInt();
                    if (!Utils.isValidEnchantment(enchantment)) continue;
                    Enchantment en = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(enchantment));
                    if (en == null) continue;
                    if (AdventureCraftCore.getInstance().getQuestListChecker().isForbiddenEnchantment(en)) continue;
                    if (en.isTreasure() || !en.canEnchantItem(new ItemStack(Material.valueOf(Utils.cleanString(material))))) continue;
                    int maxLevel = AdventureCraftCore.getInstance().getQuestListChecker().getMaxLevelEnchantment(en);
                    if (level > maxLevel) level = maxLevel;
                    if (!Utils.isEnchantable(material, enchantment)) continue;
                }

                if (!objective.has(ObjectiveStrings.QUEST_OBJECTIVE_COUNT)) continue;
                totalCount = objective.get(ObjectiveStrings.QUEST_OBJECTIVE_COUNT).asInt();

                int[] minmax = Utils.getMinMaxGoal(Utils.cleanString(questRank), type);
                int min = minmax[0];
                int max = minmax[1];

                if (totalCount < min || totalCount > max) {
                    totalCount = random.nextInt(max - min + 1) + min;
                }

                int durationForThisTask = Utils.getMinDuration(type, totalCount);
                minimumCalculatedDuration += durationForThisTask;

                System.out.println(objective);
                System.out.println("Checking Options");
                // This are the optional requirements
                boolean hasOptions = objective.has(ObjectiveStrings.QUEST_OBJECTIVE_OPTIONS);
                if (hasOptions) {
                    JsonNode options = objective.get(ObjectiveStrings.QUEST_OBJECTIVE_OPTIONS);

                    System.out.println("Has Options");
                    boolean hasWorldOptions = options.has(ObjectiveStrings.QUEST_OBJECTIVE_OPTION_WORLD);

                    if (hasWorldOptions && !isKill) {
                        String givenWorld = options.get(ObjectiveStrings.QUEST_OBJECTIVE_OPTION_WORLD).asText();
                        if (givenWorld != null) {
                            String convert = Utils.cleanString(givenWorld).toUpperCase();
                            if (convert.equalsIgnoreCase("OVERWORLD") || convert.equalsIgnoreCase("THE_NETHER") || convert.equalsIgnoreCase("THE_END")) {
                                world = convert;
                            }
                        }
                    }

                    boolean hasBiomeOptions = options.has(ObjectiveStrings.QUEST_OBJECTIVE_OPTION_BIOME);

                    if (hasBiomeOptions && !isKill) {
                        String givenBiome = options.get(ObjectiveStrings.QUEST_OBJECTIVE_OPTION_BIOME).asText();
                        if (givenBiome != null) {
                            String covert = Utils.cleanString(givenBiome).toUpperCase();
                            if (world != null) {
                                if (Utils.isValidBiome(covert, world)) {
                                    biome = covert;
                                }
                            } else {
                                if (Utils.isValidBiome(covert)) {
                                    biome = covert;
                                }
                            }
                        }
                    }
                    if (objective.has(ObjectiveStrings.QUEST_OBJECTIVE_OPTION_START_Y)) {
                        startY = objective.get(ObjectiveStrings.QUEST_OBJECTIVE_OPTION_START_Y).asInt();
                    }
                    if (objective.has(ObjectiveStrings.QUEST_OBJECTIVE_OPTION_END_Y)) {
                        endY = objective.get(ObjectiveStrings.QUEST_OBJECTIVE_OPTION_END_Y).asInt();
                    }
                    if (objective.has(ObjectiveStrings.QUEST_OBJECTIVE_OPTION_TIME_START)) {
                        timeStart = objective.get(ObjectiveStrings.QUEST_OBJECTIVE_OPTION_TIME_START).asInt();
                    }
                    if (objective.has(ObjectiveStrings.QUEST_OBJECTIVE_OPTION_TIME_END)) {
                        timeEnd = objective.get(ObjectiveStrings.QUEST_OBJECTIVE_OPTION_TIME_END).asInt();
                    }
                }

                UUID objectiveUUID = UUID.randomUUID();
                Objective finalObjective = new Objective(
                        type,
                        title,
                        material,
                        entityType,
                        enchantment,
                        level,
                        0,
                        totalCount,
                        world,
                        biome,
                        startY,
                        endY,
                        timeStart,
                        timeEnd,
                        objectiveUUID
                );
                allObjectives.put(objectiveUUID, finalObjective);
            }
            // This will check if the calculated minimum duration is greater than the one provided
            // and forcefully change it to the minimum duration.
            if (duration <= minimumCalculatedDuration) {
                duration = minimumCalculatedDuration;
            }
            totalDuration = duration;

            // No registered objectives
            if (allObjectives.isEmpty()) {
                System.out.println("NO REGISTERED OBJECTIVES");
                return null;
            }

            // Get the rewards
            if (!object.has(ObjectiveStrings.QUEST_REWARDS)) {
                System.out.println("NO REWARDS");
                return null;
            }
            JsonNode rewards = object.get(ObjectiveStrings.QUEST_REWARDS);

            int money = 1000;
            if (rewards.has(ObjectiveStrings.QUEST_REWARDS_MONEY)) {
                money =  rewards.get(ObjectiveStrings.QUEST_REWARDS_MONEY).asInt();
                money = Math.min(money, 500000);
            }

            int experience = 5000;
            if (rewards.has(ObjectiveStrings.QUEST_REWARDS_EXPERIENCE)) {
                experience = rewards.get(ObjectiveStrings.QUEST_REWARDS_EXPERIENCE).asInt();
                experience = Math.min(experience, 100000);
            }

            // This is the list of items in this structure material:amount
            List<String> regularItemList = new ArrayList<>();
            // Array of regular items
            if (rewards.has(ObjectiveStrings.QUEST_REWARDS_REGULAR_ITEMS) && rewards.get(ObjectiveStrings.QUEST_REWARDS_REGULAR_ITEMS).isArray()) {
                ArrayNode itemsObject = (ArrayNode) rewards.get(ObjectiveStrings.QUEST_REWARDS_REGULAR_ITEMS);
                for (JsonNode itemData : itemsObject) {
                    if (!itemData.has(ObjectiveStrings.QUEST_REWARDS_REGULAR_ITEMS_MATERIAL) || !itemData.has(ObjectiveStrings.QUEST_REWARDS_REGULAR_ITEMS_AMOUNT)) continue;
                    String material = itemData.get(ObjectiveStrings.QUEST_REWARDS_REGULAR_ITEMS_MATERIAL).asText();
                    String convert = Utils.cleanString(material).toUpperCase();
                    int amount = itemData.get(ObjectiveStrings.QUEST_REWARDS_REGULAR_ITEMS_AMOUNT).asInt();
                    // Add the item
                    if (Utils.isValidMaterial(convert)) {
                        regularItemList.add(convert + ":" + amount);
                    }
                }
            }

            CustomItem item = null;
            // Custom Item
            if (rewards.has(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM)) {
                JsonNode customItem = rewards.get(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM);
                String itemRank = null;
                String itemType = null;
                String itemName = null;
                String itemLore = null;
                String extraLore = null;

                String trimPattern = null;
                String trimMaterial = null;

                List<String> enchantments = new ArrayList<>();
                List<String> attributes = new ArrayList<>();

                if (customItem.has(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_RANK)) {
                    itemRank = customItem.get(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_RANK).asText();
                }
                if (customItem.has(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_TYPE)) {
                    itemType = customItem.get(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_TYPE).asText();
                }
                if (customItem.has(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_NAME)) {
                    itemName = customItem.get(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_NAME).asText();
                }
                if (customItem.has(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_LORE)) {
                    itemLore = customItem.get(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_LORE).asText();
                }
                if (customItem.has(ObjectiveStrings.QUEST_REWRADS_CUSTOM_ITEM_OPTIONS)) {
                    JsonNode options = customItem.get(ObjectiveStrings.QUEST_REWRADS_CUSTOM_ITEM_OPTIONS);

                    if (options.has(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_ENCHANTMENTS) &&
                            options.get(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_ENCHANTMENTS).isArray()) {
                        ArrayNode enchantmentDataList = (ArrayNode) options.get(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_ENCHANTMENTS);
                        for (JsonNode enchantmenData : enchantmentDataList) {
                            if (enchantmenData.has(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_ENCHANTMENT) &&
                            enchantmenData.has(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_LEVEL)) {
                                String enchantment = enchantmenData.get(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_ENCHANTMENT).asText();
                                String convert = enchantmentKeys.convertToMinecraftKey(Utils.cleanString(enchantment));
                                int level = enchantmenData.get(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_LEVEL).asInt();
                                if (Utils.isValidEnchantment(convert)) {
                                    enchantments.add(convert + ":" + level);
                                }
                            }
                        }
                    }
                    if (options.has(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_ATTRIBUTES) &&
                            options.get(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_ATTRIBUTES).isArray()) {
                        ArrayNode attributesDataList = (ArrayNode) options.get(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_ATTRIBUTES);
                        for (JsonNode attributeData : attributesDataList) {
                            if (attributeData.has(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_ATTRIBUTE) &&
                            attributeData.has(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_MODIFIER)) {
                                String attribute = attributeData.get(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_ATTRIBUTE).asText();
                                String covert = Utils.cleanString(attribute).toUpperCase();
                                int modifier = attributeData.get(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_MODIFIER).asInt();
                                if (modifier == 0) continue;
                                if (Utils.validAttribute(covert)) {
                                    attributes.add(covert + ":" + modifier);
                                }
                            }
                        }
                    }
                    if (options.has(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_TRIM_PATTERN) && options.has(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_TRIM_MATERIAL)) {
                        String pattern = options.get(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_TRIM_PATTERN).asText();
                        String material = options.get(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_TRIM_MATERIAL).asText();
                        if (AdventureCraftCore.getInstance().getQuestListChecker().getTrimMaterial(material) != null &&
                        AdventureCraftCore.getInstance().getQuestListChecker().getTrimPattern(pattern) != null) {
                            trimPattern = pattern;
                            trimMaterial = material;
                        }
                    }
                }
                if (customItem.has(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_EXTRA_LORE)) {
                    extraLore = customItem.get(ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_EXTRA_LORE).asText();
                }

                if (itemRank != null &&
                itemType != null &&
                itemName != null &&
                itemLore != null) {
                    // Item is valid
                    item = new CustomItem(
                            itemRank,
                            itemType,
                            itemName,
                            itemLore,
                            extraLore,
                            enchantments,
                            attributes,
                            trimPattern,
                            trimMaterial
                    );
                }
            }

            UUID questUUID = UUID.randomUUID();
            return new QuestData(questRank,
                    questName,
                    questLore,
                    duration,
                    totalDuration,
                    money,
                    experience,
                    allObjectives,
                    item,
                    regularItemList,
                    false,
                    questUUID,
                    playerUUID);
        } catch (Exception e) {
            System.out.println("Not a valid json or the server was not able to get any information from the ai");
//            e.printStackTrace();
        }
        return null;
    }
}
