package com.peepersoak.adventurecraftcore.openAI;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.utils.Data;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class OnGoingQuest {

    private final HashMap<UUID, List<Quest>> onGoingQuest = new HashMap<>();
    private final HashMap<String, Double> ranks = new HashMap<>();
    private final Data questData;
    public OnGoingQuest() {
        questData = AdventureCraftCore.getInstance().getOnGoingQuestData();
        startSavingQuest();
        restoreQuest();
        startUpdatingQuestDuration();

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

            List<Quest> questList = new ArrayList<>();
            for (String questName : questSection.getKeys(false)) {
                String questSettingPath = startingPath + questName + ".";

                String questUUIDPath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_UUID;
                String questTitlePath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_TITLE;
                String questDescriptionPath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_DESCRIPTION;
                String questDifficultyPath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_DIFFICULTY;
                String questDurationPath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_DURATION;
                String questExperienceRewardPath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_REWARDS_EXPERIENCE;
                String questMoneyRewardPath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_REWARDS_MONEY;
                String questItemStackRewardPath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_REWARDS_ITEM_ITEMSTACK;
                String questItemNamePath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_REWARDS_ITEM_NAME;
                String questItemRankPath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_REWARDS_ITEM_RANK;
                String questItemTypePath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_REWARDS_ITEM_TYPE;
                String objectivesPath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_OBJECTIVE;

                String questUUID = questData.getConfig().getString(questUUIDPath);
                String questTitle = questData.getConfig().getString(questTitlePath);
                String questDescription = questData.getConfig().getString(questDescriptionPath);
                String questDifficulty = questData.getConfig().getString(questDifficultyPath);
                int questDuration = questData.getConfig().getInt(questDurationPath);
                int questExperienceReward = questData.getConfig().getInt(questExperienceRewardPath);
                int questMoneyReward = questData.getConfig().getInt(questMoneyRewardPath);
                String questItemStackSerialize = questData.getConfig().getString(questItemStackRewardPath);
                String questItemName = questData.getConfig().getString(questItemNamePath);
                String questItemRank = questData.getConfig().getString(questItemRankPath);
                String questItemType = questData.getConfig().getString(questItemTypePath);
                String objectiveSerialized = questData.getConfig().getString(objectivesPath);

                Quest quest = new Quest(questTitle,
                        questDescription,
                        questDifficulty,
                        questDuration,
                        questItemStackSerialize,
                        questExperienceReward,
                        questMoneyReward,
                        questItemType,
                        questItemRank,
                        questItemName,
                        objectiveSerialized,
                        questUUID);
                questList.add(quest);
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
    private void startUpdatingQuestDuration() {
        new BukkitRunnable() {
            @Override
            public void run() {
                List<UUID> remove = new ArrayList<>();
                for (UUID uuid : onGoingQuest.keySet()) {
                    for (Quest quest : onGoingQuest.get(uuid)) {
                        boolean isTimeOut = quest.updateDuration(5);
                        if (isTimeOut) remove.add(uuid);
                    }
                }
                for (UUID uuid : remove) {
                    onGoingQuest.remove(uuid);
                }
            }
        }.runTaskTimerAsynchronously(AdventureCraftCore.getInstance(), 100, 100);
    }
    public void createANewQuest(Player player, String message) {
        String rank = getQuestRank(ranks);
        String finalMessage = message.replace("%rank%", rank);
        Quest quest = new Quest(player, finalMessage);
        UUID playerUUID = player.getUniqueId();
        if (onGoingQuest.containsKey(playerUUID)) {
            List<Quest> questList = onGoingQuest.get(playerUUID);
            questList.add(quest);
            onGoingQuest.replace(playerUUID, questList);
        } else {
            List<Quest> questList = new ArrayList<>();
            questList.add(quest);
            onGoingQuest.put(playerUUID, questList);
        }
    }
    public void removeQuest(UUID playerUUID, UUID questUUID) {
        if (!onGoingQuest.containsKey(playerUUID)) return;
        List<Quest> questList = onGoingQuest.get(playerUUID);
        questList.removeIf(quest -> quest.getQuestUUID().equals(questUUID));
    }
    public List<Quest> getQuest(UUID uuid) {
        if (onGoingQuest.containsKey(uuid)) {
            return onGoingQuest.get(uuid);
        }
        return null;
    }
    private String getQuestRank(HashMap<String, Double> rankWeights) {
        // Calculate total weight
        double totalWeight = rankWeights.values().stream().mapToDouble(Double::doubleValue).sum();

        Random random = new Random();
        // Generate a random number between 0 and the total weight
        double randomValue = random.nextDouble() * totalWeight;

        // Find the rank corresponding to the random number
        double cumulativeWeight = 0.0;
        for (Map.Entry<String, Double> entry : rankWeights.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (randomValue <= cumulativeWeight) {
                return entry.getKey();
            }
        }
        // This should not happen, but in case of rounding errors, return the last rank
        return rankWeights.keySet().iterator().next();
    }
    public void saveData() {
        System.out.println("STARTING SAVING...");
        for (UUID uuid : onGoingQuest.keySet()) {
            System.out.println("Saving: " + uuid);
            String startingPath = uuid + ".";

            List<Quest> questList = onGoingQuest.get(uuid);

            for (Quest quest : questList) {
                UUID questUUID = quest.getQuestUUID();
                String questName = quest.getName();

                System.out.println("Saving: " + questName);

                String questDescription = quest.getDescription();
                String questDifficulty = quest.getDifficulty();
                int duration = quest.getDuration();
                ItemStack itemReward = quest.getItemRewards();
                String serializedItemStack = Utils.serialized(itemReward);
                int experienceReward = quest.getExperienceRewards();
                int moneyReward = quest.getMoneyRewards();
                String itemRewardType = quest.getItemType();
                String itemRewardRank = quest.getItemRank();
                String itemRewardName = quest.getItemName();
                HashMap<String, HashMap<String, Object>> questObjectives = quest.getObjectives();
                String serializedObjectives = Utils.serialized(questObjectives);

                String questSettingPath = startingPath + questName + ".";

                String questUUIDPath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_UUID;
                String questTitlePath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_TITLE;
                String questDescriptionPath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_DESCRIPTION;
                String questDifficultyPath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_DIFFICULTY;
                String questDurationPath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_DURATION;
                String questExperienceRewardPath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_REWARDS_EXPERIENCE;
                String questMoneyRewardPath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_REWARDS_MONEY;
                String questItemStackRewardPath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_REWARDS_ITEM_ITEMSTACK;
                String questItemNamePath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_REWARDS_ITEM_NAME;
                String questItemRankPath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_REWARDS_ITEM_RANK;
                String questItemTypePath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_REWARDS_ITEM_TYPE;
                String objectivesPath = questSettingPath + ObjectiveStrings.CONFIG_QUEST_OBJECTIVE;

                questData.writeString(questUUIDPath, String.valueOf(questUUID));
                questData.writeString(questTitlePath, questName);
                questData.writeString(questDescriptionPath, questDescription);
                questData.writeString(questDifficultyPath, questDifficulty);
                questData.writeInt(questDurationPath, duration);
                questData.writeInt(questExperienceRewardPath, experienceReward);
                questData.writeInt(questMoneyRewardPath, moneyReward);
                questData.writeString(questItemStackRewardPath, serializedItemStack);
                questData.writeString(questItemNamePath, itemRewardName);
                questData.writeString(questItemRankPath, itemRewardRank);
                questData.writeString(questItemTypePath, itemRewardType);
                questData.writeString(objectivesPath, serializedObjectives);

                System.out.println("Done Saving...");
            }
        }
    }
}
