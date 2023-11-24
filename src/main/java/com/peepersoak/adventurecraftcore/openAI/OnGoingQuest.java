package com.peepersoak.adventurecraftcore.openAI;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.utils.Data;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class OnGoingQuest {
    private final HashMap<UUID, List<QuestData>> onGoingQuest = new HashMap<>();
    private final HashMap<String, Double> ranks = new HashMap<>();
    private final OpenAI openAI = new OpenAI();
    private final Data questData;
    public OnGoingQuest() {
        questData = AdventureCraftCore.getInstance().getOnGoingQuestData();
        startSavingQuest();
        restoreQuest();

        ranks.put(ObjectiveStrings.COMMON, 20.0);
        ranks.put(ObjectiveStrings.UNCOMMON, 18.0);
        ranks.put(ObjectiveStrings.RARE, 15.0);
        ranks.put(ObjectiveStrings.EPIC, 4.0);
        ranks.put(ObjectiveStrings.LEGENDARY, 1.0);
        ranks.put(ObjectiveStrings.MYTHICAL, 0.2);
        ranks.put(ObjectiveStrings.FABLED, 0.03);
        ranks.put(ObjectiveStrings.GODLIKE, 0.005);
        ranks.put(ObjectiveStrings.ASCENDED, 0.001);
    }
    private void restoreQuest() {
        for (String uuid : questData.getConfig().getKeys(false)) {
            String startingPath = uuid + ".";
            ConfigurationSection questSection = questData.getConfig().getConfigurationSection(uuid);
            UUID playerUUID = UUID.fromString(uuid);

            if (questSection == null) continue;

            List<QuestData> questList = new ArrayList<>();

            for (String questUUIDKey : questSection.getKeys(false)) {
                String questSettingPath = startingPath + questUUIDKey + ".";

                String questDifficultyPath = questSettingPath + ObjectiveStrings.QUEST_RANK;
                String questTitlePath = questSettingPath + ObjectiveStrings.QUEST_NAME;
                String questDescriptionPath = questSettingPath + ObjectiveStrings.QUEST_LORE;
                String questDurationPath = questSettingPath + ObjectiveStrings.QUEST_DURATION;
                String questTotalDurationPath = questSettingPath + ObjectiveStrings.QUEST_TOTAL_DURATION;
                String questIsActivePath = questSettingPath + ObjectiveStrings.QUEST_ACTIVE;

                String questRank = questData.getConfig().getString(questDifficultyPath);
                String questName = questData.getConfig().getString(questTitlePath);
                String questLore = questData.getConfig().getString(questDescriptionPath);
                int duration = questData.getConfig().getInt(questDurationPath);
                int totalDuration = questData.getConfig().getInt(questTotalDurationPath);
                boolean isActive = questData.getConfig().getBoolean(questIsActivePath);

                HashMap<UUID, Objective> listOfAllObjectives = new HashMap<>();

                String objectivesPath = questSettingPath + "Objectives";

                ConfigurationSection listOfObjectives = questData.getConfig().getConfigurationSection(objectivesPath);
                if (listOfObjectives != null) {
                    for (String objectivesUUID : listOfObjectives.getKeys(false)) {
                        String path = objectivesPath + "." + objectivesUUID + ".";

                        String objectivePath = path + ObjectiveStrings.QUEST_OBJECTIVE;
                        String titlePath = path + ObjectiveStrings.QUEST_OBJECTIVE_TITLE;
                        String materialPath = path + ObjectiveStrings.QUEST_OBJECTIVE_MATERIAL;
                        String entityTypePath = path + ObjectiveStrings.QUEST_OBJECTIVE_ENTITY_TYPE;
                        String enchantmentPath = path + ObjectiveStrings.QUEST_OBJECTIVE_ENCHANTMENT;
                        String levelPath = path + ObjectiveStrings.QUEST_OBJECTIVE_LEVEL;
                        String countPath = path + ObjectiveStrings.QUEST_OBJECTIVE_COUNT;
                        String totalCountPath = path + ObjectiveStrings.QUEST_OBJECTIVE_TOTAL_COUNT;
                        String worldPath = path + ObjectiveStrings.QUEST_OBJECTIVE_OPTION_WORLD;
                        String biomePath = path + ObjectiveStrings.QUEST_OBJECTIVE_OPTION_BIOME;
                        String startYPath = path + ObjectiveStrings.QUEST_OBJECTIVE_OPTION_START_Y;
                        String endYPath = path + ObjectiveStrings.QUEST_OBJECTIVE_OPTION_END_Y;
                        String timeStartPath = path + ObjectiveStrings.QUEST_OBJECTIVE_OPTION_TIME_START;
                        String timeEndPath = path + ObjectiveStrings.QUEST_OBJECTIVE_OPTION_TIME_END;

                        String objective = questData.getConfig().getString(objectivePath);
                        String title = questData.getConfig().getString(titlePath);
                        String material = questData.getConfig().getString(materialPath);
                        String entityType = questData.getConfig().getString(entityTypePath);
                        String enchantment = questData.getConfig().getString(enchantmentPath);
                        int level = questData.getConfig().getInt(levelPath);
                        int count = questData.getConfig().getInt(countPath);
                        int totalCount = questData.getConfig().getInt(totalCountPath);
                        String world = questData.getConfig().getString(worldPath);
                        String biome = questData.getConfig().getString(biomePath);
                        int startY = questData.getConfig().getInt(startYPath);
                        int endY = questData.getConfig().getInt(endYPath);
                        int timeStart = questData.getConfig().getInt(timeStartPath);
                        int timeEnd = questData.getConfig().getInt(timeEndPath);

                        Objective obj = new Objective(
                                objective,
                                title,
                                material,
                                entityType,
                                enchantment,
                                level,
                                count,
                                totalCount,
                                world,
                                biome,
                                startY,
                                endY,
                                timeStart,
                                timeEnd,
                                UUID.fromString(objectivesUUID)
                        );

                        listOfAllObjectives.put(obj.getObjectiveUUID(), obj);
                    }
                }

                String questMoneyRewardPath = questSettingPath + ObjectiveStrings.QUEST_REWARDS_MONEY;
                String questExperienceRewardPath = questSettingPath + ObjectiveStrings.QUEST_REWARDS_EXPERIENCE;
                String questRegularItemsPath = questSettingPath + ObjectiveStrings.QUEST_REWARDS_REGULAR_ITEMS;
                String questItemRankPath = questSettingPath + ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_RANK;
                String questItemTypePath = questSettingPath + ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_TYPE;
                String questItemNamePath = questSettingPath + ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_NAME;
                String questItemLorePath = questSettingPath + ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_LORE;
                String questItemExtraLorePath = questSettingPath + ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_EXTRA_LORE;
                String questItemEnchantments = questSettingPath + ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_ENCHANTMENTS;
                String questItemAttributes = questSettingPath + ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_ATTRIBUTES;
                String questItemTrimPattern = questSettingPath + ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_TRIM_PATTERN;
                String questItemTrimMaterial = questSettingPath + ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_TRIM_MATERIAL;

                int moneyReward = questData.getConfig().getInt(questMoneyRewardPath);
                int experienceReward = questData.getConfig().getInt(questExperienceRewardPath);
                List<String> regularItemsList = questData.getConfig().getStringList(questRegularItemsPath);
                String itemRank = questData.getConfig().getString(questItemRankPath);
                String itemType = questData.getConfig().getString(questItemTypePath);
                String itemName = questData.getConfig().getString(questItemNamePath);
                String itemLore = questData.getConfig().getString(questItemLorePath);
                String extraLore = questData.getConfig().getString(questItemExtraLorePath);
                List<String> enchantments = questData.getConfig().getStringList(questItemEnchantments);
                List<String> attributes = questData.getConfig().getStringList(questItemAttributes);
                String trimPattern = questData.getConfig().getString(questItemTrimPattern);
                String trimMaterial = questData.getConfig().getString(questItemTrimMaterial);

                CustomItem customItem = new CustomItem(
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

                QuestData data = new QuestData(
                        questRank,
                        questName,
                        questLore,
                        duration,
                        totalDuration,
                        moneyReward,
                        experienceReward,
                        listOfAllObjectives,
                        customItem,
                        regularItemsList,
                        isActive,
                        UUID.fromString(questUUIDKey),
                        UUID.fromString(uuid)
                );
                questList.add(data);
            }
            onGoingQuest.put(playerUUID, questList);
        }
    }
    // This will save the settings every 5 minutes
    private void startSavingQuest() {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveData();
            }
        }.runTaskTimerAsynchronously(AdventureCraftCore.getInstance(), 1200, 6000);
    }
    // This will update all duration
    public void createANewQuest(Player player, String message) {
        UUID playerUUID = player.getUniqueId();
        // This just check if the player already have 9 quest
        if (onGoingQuest.containsKey(playerUUID) && onGoingQuest.get(playerUUID).size() >= 9) return;
        player.sendMessage(Utils.color("&6Creating quest..."));

        HashMap<String, Double> dynamicRanks = getDynamicranks(player);
        if (dynamicRanks == null || dynamicRanks.isEmpty()) return;

        String rank = (String) Utils.getWeightedObject(dynamicRanks);
        String mythology = AdventureCraftCore.getInstance().getQuestListChecker().getRandomMythologies();

        String finalMessage = message.replace("%rank%", rank).replace("%mythology%", mythology);
        System.out.println(finalMessage);

        new BukkitRunnable() {
            @Override
            public void run() {
                QuestData quest = openAI.createQuest(finalMessage, playerUUID);
                if (quest == null) return;
                if (onGoingQuest.containsKey(playerUUID)) {
                    List<QuestData> questList = onGoingQuest.get(playerUUID);
                    questList.add(quest);
                    onGoingQuest.replace(playerUUID, questList);
                } else {
                    List<QuestData> questList = new ArrayList<>();
                    questList.add(quest);
                    onGoingQuest.put(playerUUID, questList);
                }
                player.sendMessage(Utils.color("&bQuest created"));
            }
        }.runTaskAsynchronously(AdventureCraftCore.getInstance());
    }
    public void removeQuest(UUID playerUUID, UUID questUUID, boolean save) {
        if (!onGoingQuest.containsKey(playerUUID)) return;
        List<QuestData> questList = onGoingQuest.get(playerUUID);
        questList.removeIf(quest -> {
            boolean shouldRemove = quest.getQuestUUID().equals(questUUID);
            if (shouldRemove) {
                String path = playerUUID + "." + questUUID;
                // Remove the data
                AdventureCraftCore.getInstance().getOnGoingQuestData().getConfig().set(path, null);
                if (save) {
                    // Save the data to all quest list
                    Data config = AdventureCraftCore.getInstance().getAllQuestData();
                    String AllDataPath = questUUID + ".";
                    saveQuestData(quest, config, AllDataPath, true);
                }
                return true;
            }
            return false;
        });
    }
    public QuestData getQuest(UUID playerUUID, UUID questUUID) {
        if (onGoingQuest.containsKey(playerUUID)) {
            List<QuestData> questDataList = onGoingQuest.get(playerUUID);
            return questDataList.stream().filter(quest -> quest.getQuestUUID().equals(questUUID)).findFirst().orElse(null);
        }
        return null;
    }
    public List<QuestData> getAllQuest(UUID playerUUID) {
        if (onGoingQuest.containsKey(playerUUID)) {
            return onGoingQuest.get(playerUUID);
        }
        return null;
    }
    public void saveData() {
        AdventureCraftCore.getInstance().getLogger().info("Saving All Quest...");
        for (UUID uuid : onGoingQuest.keySet()) {
            String startingPath = uuid + ".";
            List<QuestData> questList = onGoingQuest.get(uuid);
            for (QuestData quest : questList) {
                UUID questUUID = quest.getQuestUUID();
                if (quest.isExpired()) {
                    String pathToRemove = startingPath + questUUID;
                    questData.getConfig().set(pathToRemove, null);
                    continue;
                }
                String questSettingPath = startingPath + questUUID + ".";
                saveQuestData(quest, questData, questSettingPath, false);
            }
        }
        AdventureCraftCore.getInstance().getLogger().info("All quest has been saved");
    }
    private void saveQuestData(QuestData quest, Data questData, String questSettingPath, boolean isDone) {
        String questName = quest.getQuestName();
        String questDescription = quest.getQuestLore();
        String questDifficulty = quest.getQuestRank();
        int duration = isDone ?  quest.getTotalDuration() :  quest.getDuration();
        int totalDuration = quest.getTotalDuration();
        boolean isActive = !isDone && quest.isActive();

        String questDifficultyPath = questSettingPath + ObjectiveStrings.QUEST_RANK;
        String questTitlePath = questSettingPath + ObjectiveStrings.QUEST_NAME;
        String questDescriptionPath = questSettingPath + ObjectiveStrings.QUEST_LORE;
        String questDurationPath = questSettingPath + ObjectiveStrings.QUEST_DURATION;
        String questTotalDurationPath = questSettingPath + ObjectiveStrings.QUEST_TOTAL_DURATION;
        String questIsActivePath = questSettingPath + ObjectiveStrings.QUEST_ACTIVE;

        questData.writeString(questTitlePath, questName);
        questData.writeString(questDescriptionPath, questDescription);
        questData.writeString(questDifficultyPath, questDifficulty);
        questData.writeInt(questDurationPath, duration);
        questData.writeInt(questTotalDurationPath, totalDuration);
        questData.writeBoolean(questIsActivePath, isActive);

        HashMap<UUID, Objective> questObjectives = quest.getListOfObjectives();
        for (UUID objectiveUUID : questObjectives.keySet()) {
            Objective objectiveData = questObjectives.get(objectiveUUID);
            String path = questSettingPath + "Objectives." + objectiveUUID + ".";

            String objectivePath = path + ObjectiveStrings.QUEST_OBJECTIVE;
            String titlePath = path + ObjectiveStrings.QUEST_OBJECTIVE_TITLE;
            String materialPath = path + ObjectiveStrings.QUEST_OBJECTIVE_MATERIAL;
            String entityTypePath = path + ObjectiveStrings.QUEST_OBJECTIVE_ENTITY_TYPE;
            String enchantmentPath = path + ObjectiveStrings.QUEST_OBJECTIVE_ENCHANTMENT;
            String levelPath = path + ObjectiveStrings.QUEST_OBJECTIVE_LEVEL;
            String countPath = path + ObjectiveStrings.QUEST_OBJECTIVE_COUNT;
            String totalCountPath = path + ObjectiveStrings.QUEST_OBJECTIVE_TOTAL_COUNT;
            String worldPath = path + ObjectiveStrings.QUEST_OBJECTIVE_OPTION_WORLD;
            String biomePath = path + ObjectiveStrings.QUEST_OBJECTIVE_OPTION_BIOME;
            String startYPath = path + ObjectiveStrings.QUEST_OBJECTIVE_OPTION_START_Y;
            String endYPath = path + ObjectiveStrings.QUEST_OBJECTIVE_OPTION_END_Y;
            String timeStartPath = path + ObjectiveStrings.QUEST_OBJECTIVE_OPTION_TIME_START;
            String timeEndPath = path + ObjectiveStrings.QUEST_OBJECTIVE_OPTION_TIME_END;

            String objective = objectiveData.getObjective();
            String title = objectiveData.getTitle();
            String material = objectiveData.getMaterial();
            String entityTYpe = objectiveData.getEntityType();
            String enchantment = objectiveData.getEnchantment();
            int level = objectiveData.getLevel();
            int count = isDone ? objectiveData.getTotalCount() : objectiveData.getCount();
            int totalCount = objectiveData.getTotalCount();
            String world = objectiveData.getWorld();
            String biome = objectiveData.getBiome();
            int startY = objectiveData.getStartY();
            int endY = objectiveData.getEndY();
            int timeStart = objectiveData.getTimeStart();
            int timeEnd = objectiveData.getTimeEnd();

            questData.writeString(objectivePath, objective);
            questData.writeString(titlePath, title);
            questData.writeString(materialPath, material);
            questData.writeString(entityTypePath, entityTYpe);
            questData.writeString(enchantmentPath, enchantment);
            questData.writeInt(levelPath, level);
            questData.writeInt(countPath, count);
            questData.writeInt(totalCountPath, totalCount);
            questData.writeString(worldPath, world);
            questData.writeString(biomePath, biome);
            questData.writeInt(startYPath, startY);
            questData.writeInt(endYPath, endY);
            questData.writeInt(timeStartPath, timeStart);
            questData.writeInt(timeEndPath, timeEnd);
        }

        int moneyReward = isDone ? quest.getRewardMoney() / 2 : quest.getRewardMoney();
        int experienceReward = isDone ? quest.getRewardExperience() / 2 : quest.getRewardExperience();
        List<String> listOfRegularItems = isDone ? new ArrayList<>() : quest.getRewardRegularItem();

        String questMoneyRewardPath = questSettingPath + ObjectiveStrings.QUEST_REWARDS_MONEY;
        String questExperienceRewardPath = questSettingPath + ObjectiveStrings.QUEST_REWARDS_EXPERIENCE;
        String questRegularItemsPath = questSettingPath + ObjectiveStrings.QUEST_REWARDS_REGULAR_ITEMS;

        questData.writeInt(questMoneyRewardPath, moneyReward);
        questData.writeInt(questExperienceRewardPath, experienceReward);
        questData.writeList(questRegularItemsPath, listOfRegularItems);

        if (!isDone && quest.getRewardCustomItem() != null) {
            String itemRewardRank = quest.getRewardCustomItem().getItemRank();
            String itemRewardType = quest.getRewardCustomItem().getItemType();
            String itemRewardName = quest.getRewardCustomItem().getItemName();
            String itemRewardLore = quest.getRewardCustomItem().getItemLore();
            String itemExtraLore = quest.getRewardCustomItem().getExtraLore();
            List<String> itemRewardEnchantments = quest.getRewardCustomItem().getEnchantmenst();
            List<String> itemRewardAttributes = quest.getRewardCustomItem().getAttributes();
            String trimPattern = quest.getRewardCustomItem().getTrimPattern();
            String trimMaterial = quest.getRewardCustomItem().getTrimMaterial();

            String questItemRankPath = questSettingPath + ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_RANK;
            String questItemTypePath = questSettingPath + ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_TYPE;
            String questItemNamePath = questSettingPath + ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_NAME;
            String questItemLorePath = questSettingPath + ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_LORE;
            String questItemExtraLorePath = questSettingPath + ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_EXTRA_LORE;
            String questItemEnchantments = questSettingPath + ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_ENCHANTMENTS;
            String questItemAttributes = questSettingPath + ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_ATTRIBUTES;
            String questItemTrimPattern = questSettingPath + ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_TRIM_PATTERN;
            String questItemTrimMaterial = questSettingPath + ObjectiveStrings.QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_TRIM_MATERIAL;

            questData.writeString(questItemRankPath, itemRewardRank);
            questData.writeString(questItemTypePath, itemRewardType);
            questData.writeString(questItemNamePath, itemRewardName);
            questData.writeString(questItemLorePath, itemRewardLore);
            questData.writeString(questItemExtraLorePath, itemExtraLore);
            questData.writeList(questItemEnchantments, itemRewardEnchantments);
            questData.writeList(questItemAttributes, itemRewardAttributes);
            questData.writeString(questItemTrimPattern, trimPattern);
            questData.writeString(questItemTrimMaterial, trimMaterial);
        }
    }

    private HashMap<String, Double> getDynamicranks(Player player) {
        Long duration = Utils.getPDC(player).get(ObjectiveStrings.KEY_SESSION_DURATION, PersistentDataType.LONG);
        if (duration == null) return null;

        HashMap<String, Double> newHashMap = new HashMap<>();
        QuestSetting setting = AdventureCraftCore.getInstance().getQuestSetting();

        for (String key : ranks.keySet()) {
            boolean addCommon = key.equalsIgnoreCase(ObjectiveStrings.COMMON) && duration > setting.getCommonDuration();
            boolean addUnCommon = key.equalsIgnoreCase(ObjectiveStrings.UNCOMMON) && duration > setting.getUncommonDuration();
            boolean addRare = key.equalsIgnoreCase(ObjectiveStrings.RARE) && duration > setting.getRareDuration();
            boolean addEpic = key.equalsIgnoreCase(ObjectiveStrings.EPIC) && duration > setting.getEpicDuration();
            boolean addLegendary = key.equalsIgnoreCase(ObjectiveStrings.LEGENDARY) && duration > setting.getLegendaryDuration();
            boolean addMythical = key.equalsIgnoreCase(ObjectiveStrings.MYTHICAL) && duration > setting.getMythicalDuration();
            boolean addFabled = key.equalsIgnoreCase(ObjectiveStrings.FABLED) && duration > setting.getFabledDuration();
            boolean addGodlike = key.equalsIgnoreCase(ObjectiveStrings.GODLIKE) && duration > setting.getGodlikeDuration();
            boolean addAscended = key.equalsIgnoreCase(ObjectiveStrings.ASCENDED) && duration > setting.getAscendedDuration();

            newHashMap.put(key, ranks.get(key));

//            if (addCommon ||
//            addUnCommon ||
//            addRare ||
//            addEpic ||
//            addLegendary ||
//            addMythical ||
//            addFabled ||
//            addGodlike ||
//            addAscended) {
//                newHashMap.put(key, ranks.get(key));
//            }
        }

        return newHashMap;
    }
}
