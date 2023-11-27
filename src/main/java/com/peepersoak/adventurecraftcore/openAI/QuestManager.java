package com.peepersoak.adventurecraftcore.openAI;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.utils.Data;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class QuestManager {
    private final HashMap<UUID, QuestData> allQuest = new HashMap<>();
    private final HashMap<UUID, QuestData> todaysAvailableQuest = new HashMap<>();
    private final Random random = new Random();

    public QuestManager() {
        restoreAllQuest();
        startAddingQuest();
    }

    // This will restore all board quest
    public void restoreAllQuest() {
        Data allQuestData = AdventureCraftCore.getInstance().getAllQuestData();
        List<String> allQuestUUID = new ArrayList<>(allQuestData.getConfig().getKeys(false));
        List<QuestData> questDataList = AdventureCraftCore.getInstance().getOnGoingQuest().restoreQuestData(allQuestUUID, "", null);
        if (questDataList.isEmpty()) return;
        for (QuestData data : questDataList) {
            data.shouldStopUpdating();
            UUID questUUID = data.getQuestUUID();
            if (random.nextBoolean()) continue;
            allQuest.put(questUUID, data);
        }
    }

    // This will be trigger by the quest board only
    public void getQuest(Player player, UUID questID) {
        UUID playerUUID = player.getUniqueId();
        List<QuestData> questDataList = AdventureCraftCore.getInstance().getOnGoingQuest().getAllQuest(playerUUID);
        if (questDataList != null && questDataList.size() >= 5) {
            player.sendMessage(Utils.color("&cYou can't take anymore quest right now! Try again later."));
            return;
        }
        if (!allQuest.containsKey(questID)) {
            player.sendMessage(Utils.color("&cThat quest is not available right now!"));
            return;
        }
        assignANewQuest(player, questID, playerUUID, true, true);
    }
    // For now, this should be trigger by the auto quest generation
    public QuestData getRandomQuest(Player player, String rank, boolean announce) {
        UUID playerUUID = player.getUniqueId();
        if (allQuest.isEmpty()) return null;
        List<QuestData> rankedQuestData = new ArrayList<>(allQuest.values().stream().filter(data -> data != null && rank.equalsIgnoreCase(Utils.cleanString(data.getQuestRank())) && !data.isDoingQuestAlready(playerUUID)).toList());
        if (rankedQuestData.isEmpty()) {
            if (announce) {
                player.sendMessage(Utils.color("&cNo quest is available right now!"));
            }
            return null;
        }
        Collections.shuffle(rankedQuestData);
        QuestData questData = rankedQuestData.get(0);
        assignANewQuest(player, questData.getQuestUUID(), playerUUID, false, false);
        return questData;
    }
    public void removeQuestFromTodays(UUID uuid) {
        todaysAvailableQuest.remove(uuid);
    }
    public HashMap<UUID, QuestData> getTodaysAvailableQuest() {
        return todaysAvailableQuest;
    }
    private void assignANewQuest(Player player, UUID questID, UUID playerUUID, boolean autoAssign, boolean announce) {
        QuestData questData = allQuest.get(questID);
        if (questData.isDoingQuestAlready(playerUUID)) {
            if (announce) {
                player.sendMessage(Utils.color("&cYou are doing that quest already!"));
            }
            return;
        }

        long duration = Utils.getSessionDuration(player);
        if (duration <= 0) return;

        if (!Utils.isElligibleForThisQuestRank(questData.getQuestRank(), duration)) {
            if (announce) {
                player.sendMessage(Utils.color("&cThis quest is too high for you!"));
            }
            return;
        }
        questData.addPlayerToListOfPlayer(player);

        QuestData newQuestData = new QuestData(
                questData.getQuestRank(),
                questData.getQuestName(),
                questData.getQuestLore(),
                questData.getDuration(),
                questData.getTotalDuration(),
                questData.getRewardMoney(),
                questData.getRewardExperience(),
                questData.getListOfObjectives(),
                null,
                new ArrayList<>(),
                false,
                questID,
                playerUUID);

        AdventureCraftCore.getInstance().getOnGoingQuest().addNewQuest(player, newQuestData);
        if (announce) {
            player.sendMessage(Utils.color("&6You have a new quest! " + newQuestData.getQuestName()));
        }
        if (autoAssign) {
            newQuestData.activateQuest(player);
        }
    }
    private void startAddingQuest() {
        long duration = 20 * 1800;
        new BukkitRunnable() {
            @Override
            public void run() {
                List<QuestData> questDataList = new ArrayList<>(allQuest.values());
                if (questDataList.isEmpty()) return;
                Collections.shuffle(questDataList);
                QuestData questData = questDataList.get(0);
                if (questData == null) return;
                if (todaysAvailableQuest.values().size() >= 54) return;
                for (QuestData data : questDataList) {
                    if (todaysAvailableQuest.containsKey(data.getQuestUUID())) continue;
                    todaysAvailableQuest.put(data.getQuestUUID(), data);
                    data.setAsBoardQuest();
                    break;
                }
            }
        }.runTaskTimer(AdventureCraftCore.getInstance(), 0, duration);
    }
}
