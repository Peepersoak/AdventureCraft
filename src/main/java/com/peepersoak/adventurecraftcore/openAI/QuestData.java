package com.peepersoak.adventurecraftcore.openAI;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class QuestData {
    private final String questRank;
    private final String questName;
    private final String questLore;
    private final HashMap<UUID, Objective> listOfObjectives;
    private final int rewardMoney;
    private final int rewardExperience;
    private final int totalDuration;
    private final List<String> rewardRegularItem;
    private final CustomItem rewardCustomItem;
    private final UUID questUUID;
    private final UUID playerUUID;

    public QuestData(String questRank,
                     String questName,
                     String questLore,
                     int duration,
                     int totalDuration,
                     int rewardMoney,
                     int rewardExperience,
                     HashMap<UUID, Objective> listOfObjectives,
                     CustomItem rewardCustomItem,
                     List<String> rewardRegularItem,
                     boolean isActive,
                     UUID questUUID,
                     UUID playerUUID) {

        this.questRank = questRank;
        this.questName = questName;
        this.questLore = questLore;
        this.duration = duration;
        this.totalDuration = totalDuration;
        this.rewardMoney = rewardMoney;
        this.rewardExperience = rewardExperience;
        this.listOfObjectives = listOfObjectives;
        this.rewardCustomItem = rewardCustomItem;
        this.rewardRegularItem = rewardRegularItem;
        this.isActive = isActive;
        this.questUUID = questUUID;
        this.playerUUID = playerUUID;

        assignAnActiveObjective(null);
        updateDuration();
    }
    private int duration = 0;

    // This means the player is actively doing the quest, if this is false, which is by default, the plugin will not track the progress
    private boolean isActive;

    // This is the status for when the quest is done
    private boolean isDone = false;
    // If the quest duration is zero the quest will expire and should not be able to activate
    private boolean isExpired = false;

    // This is the active objective
    private Objective activeObjective = null;

    public ItemStack createPaperQuest() {
        ItemStack paper = new ItemStack(Material.PAPER);
        paper.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        ItemMeta meta = paper.getItemMeta();
        if (meta == null) return null;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(ObjectiveStrings.PDC_QUEST_UUID, PersistentDataType.STRING, questUUID.toString());

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        meta.setDisplayName(Utils.color(questName));
        List<String> lore = new ArrayList<>(Utils.getLore(questLore));
        lore.add("");

        // Like is it the first objective, second or third
        int objectiveCount = 0;
        int totalObjectives = listOfObjectives.size();

        boolean hasFound = false;

        // List the objective number
        for (Objective objective : listOfObjectives.values()) {
            objectiveCount++;
            if (objective.getCount() > 0) {
                lore.add(Utils.color("&3Objective Details ("+ objectiveCount + "/" + totalObjectives + ")" ));
                hasFound = true;
                break;
            }
        }

        if (!hasFound) {
            lore.add(Utils.color("&3Objective Details ("+ objectiveCount + "/" + totalObjectives + ")" ));
        }

        Objective objective = activeObjective;

        String title = objective.getTitle();
        String objectiveType = objective.getObjective();

        lore.add(Utils.color("&d◆ " + title));
        lore.add(Utils.color("&b➤ &7Action: &3" + Utils.capitalizeFirstLetter(Utils.cleanString(objectiveType))));

        if (objectiveType.equalsIgnoreCase(ObjectiveStrings.TYPE_BREAK) || objectiveType.equalsIgnoreCase(ObjectiveStrings.TYPE_PLACE)) {
            String material = objective.getMaterial();
            lore.add(Utils.color("&b➤ &7Block: &3" + Utils.capitalizeFirstLetter(Utils.cleanString(material))));
        } else if (objectiveType.equalsIgnoreCase(ObjectiveStrings.TYPE_CRAFT)) {
            String material = objective.getMaterial();
            lore.add(Utils.color("&b➤ &7Item: &3" + Utils.capitalizeFirstLetter(Utils.cleanString(material))));
        } else if (objectiveType.equalsIgnoreCase(ObjectiveStrings.TYPE_WALK) || objectiveType.equalsIgnoreCase(ObjectiveStrings.TYPE_FLY)) {
            String sampleData = "";
        } else if (objectiveType.equalsIgnoreCase(ObjectiveStrings.TYPE_KILL)) {
            String entityType = objective.getEntityType();
            String materialToUse = objective.getMaterial();
            lore.add(Utils.color("&b➤ &7Entity: &3" + Utils.capitalizeFirstLetter(Utils.cleanString(entityType))));
            lore.add(Utils.color("&b➤ &7Using: &3" + Utils.capitalizeFirstLetter(Utils.cleanString(materialToUse))));
        } else if (objectiveType.equalsIgnoreCase(ObjectiveStrings.TYPE_HARVEST) || objectiveType.equalsIgnoreCase(ObjectiveStrings.TYPE_PLANT)) {
            String material = objective.getMaterial();
            lore.add(Utils.color("&b➤ &7Crops: &3" + Utils.capitalizeFirstLetter(Utils.cleanString(material))));
        } else if (objectiveType.equalsIgnoreCase(ObjectiveStrings.TYPE_ENCHANT)) {
            int level = objective.getLevel();
            String material = objective.getMaterial();
            String enchantment = objective.getEnchantment();
            lore.add(Utils.color("&b➤ &7Item: &3" + Utils.capitalizeFirstLetter(Utils.cleanString(material))));
            lore.add(Utils.color("&b➤ &7Enchantment: &3" + Utils.capitalizeFirstLetter(Utils.cleanString(enchantment))));
            lore.add(Utils.color("&b➤ &7Enchantment Level: &3" + level));
        }

        String biome = objective.getBiome();
        if (biome != null) {
            lore.add(Utils.color("&b➤ &7Biome: &3" + Utils.capitalizeFirstLetter(Utils.cleanString(biome))));
        }

        String world = objective.getWorld();
        if (world != null) {
            lore.add(Utils.color("&b➤ &7World: &3" + Utils.capitalizeFirstLetter(Utils.cleanString(world))));
        }

        int minY = objective.getStartY();
        int maxY = objective.getEndY();
        if (minY != -300 && maxY != -300) {
            // Check Y Coordinate
            lore.add(Utils.color("&b➤ &7Between &3" + minY + "y&7 and &3" + maxY + "y"));
        }

        // Check the time
        int minTime = objective.getTimeStart();
        int maxTime = objective.getTimeEnd();
        if (minTime != -1 && maxTime != -1) {
            lore.add(Utils.color("&b➤ &7Between the time of &3" + minTime + "&7 and &3" + maxTime + "&7"));
        }

        // Assign the current active objective to the meta
        int count = objective.getCount();
        if (objectiveType.equalsIgnoreCase(ObjectiveStrings.TYPE_BREAK)) {
            lore.add(Utils.color("&b➤ &7Goal: &6" + count + " &3Blocks Remaining"));
        } else if (objectiveType.equalsIgnoreCase(ObjectiveStrings.TYPE_CRAFT)) {
            lore.add(Utils.color("&b➤ &7Goal: &6" + count + " &3Item Remaining"));
            lore.add(Utils.color("&b➤ &7Using: &6Crafting Table"));
        } else if (objectiveType.equalsIgnoreCase(ObjectiveStrings.TYPE_WALK)) {
            lore.add(Utils.color("&b➤ &7Goal: &6" + count + " &3Seconds Remaining"));
        } else if (objectiveType.equalsIgnoreCase(ObjectiveStrings.TYPE_FLY)) {
            lore.add(Utils.color("&b➤ &7Goal: &6" + count + " &3Seconds Remaining"));
        } else if (objectiveType.equalsIgnoreCase(ObjectiveStrings.TYPE_KILL)) {
            lore.add(Utils.color("&b➤ &7Goal: &6" + count + " &3Kills Remaining"));
        } else if (objectiveType.equalsIgnoreCase(ObjectiveStrings.TYPE_HARVEST) || objectiveType.equalsIgnoreCase(ObjectiveStrings.TYPE_PLANT)) {
            lore.add(Utils.color("&b➤ &7Goal: &6" + count + " &3Crops Remaining"));
        } else if (objectiveType.equalsIgnoreCase(ObjectiveStrings.TYPE_ENCHANT)) {
            lore.add(Utils.color("&b➤ &7Goal: &6" + count + " &3Enchants Remaining"));
        }
        lore.add("");

        lore.add(Utils.color("&3Quest Rank: " + questRank));
        lore.add("");
        lore.add(Utils.color("&bRewards"));
        lore.add(Utils.color("&b➤ &7Money: &6" + rewardMoney));
        lore.add(Utils.color("&b➤ &7Experience: &6" + rewardExperience + " &3exp."));
        for (String itemData : rewardRegularItem) {
            String[] split =  itemData.split(":");
            lore.add(Utils.color("&b➤ &7" + Utils.capitalizeFirstLetter(Utils.cleanString(split[0])) + " &6" + split[1] + "x"));
        }
        if (rewardCustomItem != null && rewardCustomItem.getRewards() != null) {
            String itemName = rewardCustomItem.getItemName();
            String itemRank = rewardCustomItem.getItemRank();
            lore.add("");
            lore.add(Utils.color("&6Item Rank: &b" + itemRank));
            lore.add(Utils.color("&4✦ " + itemName));
        }
        lore.add("");
        if (isActive && !isDone && !isExpired) {
            lore.add(Utils.color("&6Status: &eActive"));
            lore.add("");
        }
        if (duration > 0) {
            if (isDone) {
                lore.add(Utils.color("&6Status: &3Complete"));
                lore.add(Utils.color("&7Click to collect your rewards"));
            } else {
                lore.add(Utils.color("&cTime Remaining: &7" + Utils.convertSecondsToTime(duration)));
                if (!isActive) {
                    lore.add(Utils.color("&7Click to activate"));
                }
            }
        } else {
            lore.add(Utils.color("&6Status: &cEXPIRED"));
        }

        meta.setLore(lore);
        paper.setItemMeta(meta);
        return paper;
    }

    // Update Harvest
    // Update Plant
    // Update Break
    public void updateQuest(Block block) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || !player.isOnline()) return;
        if (!passAllOptionalRequirement(player, activeObjective)) return;
        String requiredMaterialRaw = activeObjective.getMaterial();
        String material = block.getType().getKey().getKey();
        if (!material.equalsIgnoreCase(Utils.cleanString(requiredMaterialRaw))) return;
        if (activeObjective.getObjective().equalsIgnoreCase(ObjectiveStrings.TYPE_HARVEST)) {
            if (block.getBlockData() instanceof Ageable data) {
                if (data.getAge() != data.getMaximumAge()) return;
            }
        }
        boolean isDone = activeObjective.updateProgress(player);
        if (isDone) {
            System.out.println("DONE");
            assignAnActiveObjective(player);
        }
    }
    // Update Walk
    public void updateQuest() {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || !player.isOnline()) return;
        if (!passAllOptionalRequirement(player, activeObjective)) return;
        boolean isDone = activeObjective.updateProgress(player);
        if (isDone) {
            assignAnActiveObjective(player);
        }
    }
    // Update Fly
    public void updateQuest(int duration) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || !player.isOnline()) return;
        if (!passAllOptionalRequirement(player, activeObjective)) return;
        boolean isDone = activeObjective.updateProgress(player, duration);
        if (isDone) {
            assignAnActiveObjective(player);
        }
    }
    // Update Kill
    public void updateQuest(Entity entity, ItemStack item) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || !player.isOnline()) return;
        if (!passAllOptionalRequirement(player, activeObjective)) return;
        String entityType = Utils.cleanString(entity.getType().getKey().getKey());
        String entityTypeRequirement = activeObjective.getEntityType();

        String material = Utils.cleanString(item.getType().getKey().getKey());
        String materialRequirement = activeObjective.getMaterial();

        if (!entityType.equalsIgnoreCase(entityTypeRequirement) || !material.equalsIgnoreCase(materialRequirement)) return;
        boolean isDone = activeObjective.updateProgress(player);
        if (isDone) {
            assignAnActiveObjective(player);
        }
    }
    // Update Craft
    public void updateQuest(ItemStack material, int amount) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || !player.isOnline()) return;
        if (!passAllOptionalRequirement(player, activeObjective)) return;
        String requiredMaterialRaw = activeObjective.getMaterial();
        String craftedMaterial = material.getType().getKey().getKey();
        if (!craftedMaterial.equalsIgnoreCase(Utils.cleanString(requiredMaterialRaw))) return;
        boolean isDone = activeObjective.updateProgress(player, amount);
        if (isDone) {
            assignAnActiveObjective(player);
        }
    }
    // Update Enchantment
    public void updateQuest(ItemStack material, Map<Enchantment, Integer> allEnchantments) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || !player.isOnline()) return;
        if (!passAllOptionalRequirement(player, activeObjective)) return;
        String requiredMaterialRaw = activeObjective.getMaterial();
        String craftedMaterial = material.getType().getKey().getKey();
        if (!craftedMaterial.equalsIgnoreCase(Utils.cleanString(requiredMaterialRaw))) return;
        for (Enchantment enchantment : allEnchantments.keySet()) {
            String enchantmentRequirement = activeObjective.getEnchantment();
            String enchantmentRaw = enchantment.getKey().getKey();
            if (!enchantmentRaw.equalsIgnoreCase(Utils.cleanString(enchantmentRequirement))) continue;
            int levelRequirement = activeObjective.getLevel();
            if (levelRequirement != allEnchantments.get(enchantment)) continue;

            boolean isDone = activeObjective.updateProgress(player);
            if (isDone) {
                assignAnActiveObjective(player);
            }
            return;
        }
    }
    // Fishing
    public void updateQuest(Material material) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || !player.isOnline()) return;
        if (!passAllOptionalRequirement(player, activeObjective)) return;
        String requiredMaterial = activeObjective.getMaterial();
        String caught = material.getKey().getKey();
        if (!caught.equalsIgnoreCase(Utils.cleanString(requiredMaterial))) return;
        boolean isDone = activeObjective.updateProgress(player);
        if (isDone) {
            assignAnActiveObjective(player);
        }
    }

    public void updateDuration() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isDone) {
                    this.cancel();
                    return;
                }
                if (duration <= 0) {
                    duration = 0;
                    isActive = false;
                    isExpired = true;
                    Bukkit.getScheduler().runTask(AdventureCraftCore.getInstance(), () -> {
                        Player player = Bukkit.getPlayer(playerUUID);
                        if (player != null && player.isOnline()) {
                            assignPermission(player, false);
                            player.sendMessage(Utils.color("&b" + questName + " &chas expired!"));
                        }
                    });
                    AdventureCraftCore.getInstance().getOnGoingQuest().removeQuest(playerUUID, questUUID, false);
                    this.cancel();
                    return;
                }
                duration--;
            }
        }.runTaskTimerAsynchronously(AdventureCraftCore.getInstance(), 20, 20);
    }

    public void activateQuest(Player player) {
        if (isActive) {
            isActive = false;
            player.sendMessage(Utils.color("&b" + questName + " &chas been de-activated!"));
            return;
        }
        isActive = true;
        player.sendMessage(Utils.color("&6" + questName + " &7has been activated!"));
        player.getPersistentDataContainer().set(ObjectiveStrings.PDC_QUEST_UUID, PersistentDataType.STRING, questUUID.toString());
        assignPermission(player, true);
    }
    public void deactiveQuest(Player player, boolean announce) {
        if (!isActive) {
            if (announce) {
                player.sendMessage(Utils.color("&b" + questName + " &cis not active!"));
            }
            return;
        }
        isActive = false;
        if (announce) {
            player.sendMessage(Utils.color("&6" + questName + " &7has been de-activated!"));
        }
        player.getPersistentDataContainer().remove(ObjectiveStrings.PDC_QUEST_UUID);
        assignPermission(player, false);
    }
    public boolean isActive() {
        return isActive;
    }
    public UUID getQuestUUID() {
        return questUUID;
    }
    public String getQuestRank() {
        return questRank;
    }
    public String getQuestName() {
        return questName;
    }
    public String getQuestLore() {
        return questLore;
    }
    public HashMap<UUID, Objective> getListOfObjectives() {
        return listOfObjectives;
    }
    public int getRewardMoney() {
        return rewardMoney;
    }
    public int getRewardExperience() {
        return rewardExperience;
    }
    public List<String> getRewardRegularItem() {
        return rewardRegularItem;
    }
    public CustomItem getRewardCustomItem() {
        return rewardCustomItem;
    }
    public int getDuration() {
        return duration;
    }
    public int getTotalDuration() {
        return totalDuration;
    }
    public boolean isExpired() {
        return isExpired;
    }
    public boolean isDone() {
        return isDone;
    }

    public void assignPermission(Player player, boolean assign) {
        String type = activeObjective.getObjective();
        String permission = null;

        if (type.equalsIgnoreCase(ObjectiveStrings.TYPE_BREAK)) {
            permission = ObjectiveStrings.BREAK_QUEST_PERMISSION;
        } else if (type.equalsIgnoreCase(ObjectiveStrings.TYPE_PLACE)) {
            permission = ObjectiveStrings.PLACE_QUEST_PERMISSION;
        } else if (type.equalsIgnoreCase(ObjectiveStrings.TYPE_WALK)) {
            permission = ObjectiveStrings.WALK_QUEST_PERMISSION;
        } else if (type.equalsIgnoreCase(ObjectiveStrings.TYPE_FLY)) {
            permission = ObjectiveStrings.FLY_QUEST_PERMISSION;
        } else if (type.equalsIgnoreCase(ObjectiveStrings.TYPE_KILL)) {
            permission = ObjectiveStrings.KILL_QUEST_PERMISSION;
        } else if (type.equalsIgnoreCase(ObjectiveStrings.TYPE_HARVEST)) {
            permission = ObjectiveStrings.HARVEST_QUEST_PERMISSION;
        } else if (type.equalsIgnoreCase(ObjectiveStrings.TYPE_PLANT)) {
            permission = ObjectiveStrings.PLANT_QUEST_PERMISSION;
        } else if (type.equalsIgnoreCase(ObjectiveStrings.TYPE_CRAFT)) {
            permission = ObjectiveStrings.CRAFT_QUEST_PERMISSION;
        } else if (type.equalsIgnoreCase(ObjectiveStrings.TYPE_ENCHANT)) {
            permission = ObjectiveStrings.ENCHANT_QUEST_PERMISSION;
        } else if (type.equalsIgnoreCase(ObjectiveStrings.TYPE_FISHING)) {
            permission = ObjectiveStrings.FISHIN_QUEST_PERMISSION;
        }

        if (permission != null) player.addAttachment(AdventureCraftCore.getInstance(), permission, assign);
    }
    private void assignAnActiveObjective(Player player) {
        Objective oldObjective = null;
        for (String objectiveType : AdventureCraftCore.getInstance().getQuestListChecker().getObjectiveList()) {
            System.out.println(objectiveType);

            for (Objective objective : listOfObjectives.values()) {
                System.out.println(objective.getTitle() + " " + objective.getObjective());
                if (!objectiveType.equalsIgnoreCase(objective.getObjective())) continue;
                if (objective.getCount() <= 0) {
                    oldObjective = objective;
                    continue;
                }
                System.out.println("ASIGNING " + objective.getTitle() + " "  + objective.getObjective());
                activeObjective = objective;
                if (isActive && player != null && player.isOnline()) {
                    assignPermission(player, true);
                }
                return;
            }
        }
        // Code will only reach this if all the objectives are done
        activeObjective = oldObjective;
        isDone = true;
    }
    public void collectRewards(Player player) {
        if (!isDone) return;
        // Give the reward

        if( rewardCustomItem != null) {
            ItemStack customItem = rewardCustomItem.getRewards();
            if (customItem != null) {
                Utils.giveItemToPlayer(customItem, player);
                String itemName = rewardCustomItem.getItemName();
                player.sendMessage(Utils.color("&b" + itemName + " &ehas been received"));
            }
        }

        if (rewardRegularItem != null && !rewardRegularItem.isEmpty()) {
            for (String str : rewardRegularItem) {
                String[] sp = str.split(":");
                int amount = 1;
                try {
                    amount = Integer.parseInt(sp[1]);
                } catch (NumberFormatException e) {
                    //
                }
                ItemStack item = new ItemStack(Material.valueOf(sp[0]), amount);
                Utils.giveItemToPlayer(item, player);
                player.sendMessage(Utils.color("&6" + amount + "x &b" + Utils.capitalizeFirstLetter(Utils.cleanString(item.getType().toString())) + " &ehas been received"));
            }
        }

        if (rewardExperience > 0) {
            player.giveExp(rewardExperience);
            player.sendMessage(Utils.color("&b" + rewardExperience + " exp &ehas been received"));
        }

        if (rewardMoney > 0) {
            AdventureCraftCore.getEconomy().depositPlayer(player, rewardMoney);
            player.sendMessage(Utils.color("&b" + rewardMoney + " money &ehas been received"));
        }

        // Remove the quest
        AdventureCraftCore.getInstance().getOnGoingQuest().removeQuest(player.getUniqueId(), questUUID, true);

        // Announce it
        String rank = Utils.cleanString(questRank);
        if (rank.equalsIgnoreCase("Godlike") || rank.equalsIgnoreCase("Ascended")) {
            player.getServer().broadcastMessage(Utils.color("&6" + player.getName() + " &ehas completed a " + questRank + " &equest: " + questName));
        } else {
            player.sendMessage(Utils.color("&b" + questName + " quest &ehas been completed"));
        }
    }

    private boolean passAllOptionalRequirement(Player player, Objective objective) {
        // Check if quest is not active, is done, has expired, or if the last objective has been completed
        if (!isActive || isDone || isExpired || objective.getCount() <= 0) return false;

        // Check the world
        String worldRequirement = objective.getWorld();
        if (worldRequirement != null) {
            // This either OVERWORLD, THE_NETHER, THE_END
            World.Environment environment = player.getWorld().getEnvironment();
            if (worldRequirement.equalsIgnoreCase("OVERWORLD") &&
                    !environment.equals(World.Environment.NORMAL)) return false;

            if (worldRequirement.equalsIgnoreCase("THE_NETHER") &&
                    !environment.equals(World.Environment.NETHER)) return false;

            if (worldRequirement.equalsIgnoreCase("THE_END") &&
                    !environment.equals(World.Environment.THE_END)) return false;
        }

        // Check the Biome
        String biomeRequirement = objective.getBiome();
        if (biomeRequirement != null) {
            String biome = player.getLocation().getBlock().getBiome().getKey().getKey();
            if (!biomeRequirement.equalsIgnoreCase(Utils.cleanString(biome))) return false;
        }

        // Check the Start Y
        int yCoordinateMin = objective.getStartY();
        if (yCoordinateMin != -300) {
            if (player.getLocation().getBlockY() < yCoordinateMin) return false;
        }

        // Check the End Y
        int yCoordinateMax = objective.getEndY();
        if (yCoordinateMax != -300) {
            if (player.getLocation().getBlockY() > yCoordinateMax) return false;
        }

        // Check the Time Start
        int minTime = objective.getTimeStart();
        if (minTime != -1) {
            int currentTime = (int) player.getWorld().getTime();
            if (currentTime < minTime) return false;
        }

        // Check the Time End
        int maxTime = objective.getTimeEnd();
        if (maxTime != -1) {
            int currentTime = (int) player.getWorld().getTime();
            return currentTime <= maxTime;
        }

        return true;
    }
}
