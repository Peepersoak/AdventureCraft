package com.peepersoak.adventurecraftcore.openAI;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import org.bukkit.NamespacedKey;

public class ObjectiveStrings {

    public final static NamespacedKey PDC_QUEST_UUID = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.UUID");
    public final static NamespacedKey PDC_CUSTOM_ITEM = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.CustomItem");

    public final static NamespacedKey kEY_PLACING = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.Place");
    public final static NamespacedKey KEY_BREAKING = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.Break");
    public final static NamespacedKey KEY_SEND_DAMAGE = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.SendDamage");
    public final static NamespacedKey KEY_KILLING = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.Killed");
    public final static NamespacedKey KEY_GET_DAMAGE = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.GetDamage");
    public final static NamespacedKey KEY_DYING = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.Dying");
    public final static NamespacedKey KEY_CONSUMING = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.Consuming");
    public final static NamespacedKey KEY_CRAFTING = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.Crafting");
    public final static NamespacedKey KEY_SUMMONING = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.Summoning");
    public final static NamespacedKey KEY_ENCHANTING = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.Enchanting");
    public final static NamespacedKey KEY_ANVIL = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.Anvil");
    public final static NamespacedKey KEY_SMITHING = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.Smithing");
    public final static NamespacedKey KEY_CARTOGRAPHY = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.Cartography");
    public final static NamespacedKey KEY_STONECUTTER = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.StoneCutter");
    public final static NamespacedKey KEY_GRINDSTONE = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.GrindStone");
    public final static NamespacedKey KEY_LOOM = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.Loom");
    public final static NamespacedKey KEY_SMOKER = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.Smoker");
    public final static NamespacedKey KEY_FURNACE = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.Furnace");
    public final static NamespacedKey KEY_BLASTFURNACE = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.BlastFurnace");
    public final static NamespacedKey KEY_TRADING = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.Trading");
    public final static NamespacedKey KEY_SESSION_DURATION = new NamespacedKey(AdventureCraftCore.getInstance(), "AC.Quest.Tracking.SessionDuration");

    public final static String QUEST_INVENTORY_NAME = "My Quests";
    public final static String QUEST_ADVENTURECRAFT_BOARD = "AdventureCraft Quests Board";

    public final static String BREAK_QUEST_PERMISSION = "AC.Objectives.Break";
    public final static String PLACE_QUEST_PERMISSION = "AC.Objectives.Place";
    public final static String WALK_QUEST_PERMISSION = "AC.Objectives.Walk";
    public final static String FLY_QUEST_PERMISSION = "AC.Objectives.Fly";
    public final static String KILL_QUEST_PERMISSION = "AC.Objectives.Kill";
    public final static String HARVEST_QUEST_PERMISSION = "AC.Objectives.Harvest";
    public final static String PLANT_QUEST_PERMISSION = "AC.Objectives.Plant";
    public final static String CRAFT_QUEST_PERMISSION = "AC.Objectives.Craft";
    public final static String ENCHANT_QUEST_PERMISSION = "AC.Objectives.Enchant";
    public final static String FISHIN_QUEST_PERMISSION = "AC.Objectives.Fishing";

    public final static String COMMON = "Common";
    public final static String UNCOMMON = "Uncommon";
    public final static String RARE = "Rare";
    public final static String EPIC = "Epic";
    public final static String LEGENDARY = "Legendary";
    public final static String MYTHICAL = "Mythical";
    public final static String FABLED = "Fabled";
    public final static String GODLIKE = "Godlike";
    public final static String ASCENDED = "Ascended";

    public final static String QUEST_RANK = "Rank";
    public final static String QUEST_NAME = "Quest Name";
    public final static String QUEST_LORE = "Quest Lore";
    public final static String QUEST_DURATION = "Duration";
    public final static String QUEST_TOTAL_DURATION = "Total Duration";
    public final static String QUEST_ACTIVE = "Active";
    public final static String QUEST_OBJECTIVES = "Objectives";
    public final static String QUEST_OBJECTIVE = "Objective";
    public final static String QUEST_OBJECTIVE_TITLE = "Title";
    public final static String QUEST_OBJECTIVE_MATERIAL = "Material";
    public final static String QUEST_OBJECTIVE_ENTITY_TYPE = "EntityType";
    public final static String QUEST_OBJECTIVE_ENCHANTMENT = "Enchantment";
    public final static String QUEST_OBJECTIVE_LEVEL = "Level";
    public final static String QUEST_OBJECTIVE_COUNT = "Count";
    public final static String QUEST_OBJECTIVE_TOTAL_COUNT = "Total Count";
    public final static String QUEST_OBJECTIVE_OPTIONS = "Optional";
    public final static String QUEST_OBJECTIVE_OPTION_WORLD = "World";
    public final static String QUEST_OBJECTIVE_OPTION_BIOME = "Biome";
    public final static String QUEST_OBJECTIVE_OPTION_START_Y = "StartY";
    public final static String QUEST_OBJECTIVE_OPTION_END_Y = "EndY";
    public final static String QUEST_OBJECTIVE_OPTION_TIME_START = "TimeStart";
    public final static String QUEST_OBJECTIVE_OPTION_TIME_END = "TimeEnd";
    public final static String QUEST_REWARDS = "Rewards";
    public final static String QUEST_REWARDS_MONEY = "Money";
    public final static String QUEST_REWARDS_EXPERIENCE = "Experience";
    public final static String QUEST_REWARDS_REGULAR_ITEMS = "Regular Items";
    public final static String QUEST_REWARDS_REGULAR_ITEMS_MATERIAL = "Material";
    public final static String QUEST_REWARDS_REGULAR_ITEMS_AMOUNT = "Amount";
    public final static String QUEST_REWARDS_CUSTOM_ITEM = "Custom Item";
    public final static String QUEST_REWARDS_CUSTOM_ITEM_RANK = "Rank";
    public final static String QUEST_REWARDS_CUSTOM_ITEM_TYPE = "Type";
    public final static String QUEST_REWARDS_CUSTOM_ITEM_NAME = "Name";
    public final static String QUEST_REWARDS_CUSTOM_ITEM_LORE = "Lore";
    public final static String QUEST_REWRADS_CUSTOM_ITEM_OPTIONS = "Optional";
    public final static String QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_ENCHANTMENTS = "Enchantments";
    public final static String QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_ENCHANTMENT = "Enchantment";
    public final static String QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_TRIM_PATTERN = "Trim Pattern";
    public final static String QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_TRIM_MATERIAL = "Trim Material";
    public final static String QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_LEVEL = "Level";
    public final static String QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_ATTRIBUTES = "Attributes";
    public final static String QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_ATTRIBUTE = "Attribute";
    public final static String QUEST_REWARDS_CUSTOM_ITEM_OPTIONS_MODIFIER = "Modifier";
    public final static String QUEST_REWARDS_CUSTOM_ITEM_EXTRA_LORE = "Extra Lore";

    public final static String TYPE_BREAK = "Break";
    public final static String TYPE_PLACE = "Place";
    public final static String TYPE_WALK = "Walk";
    public final static String TYPE_FLY = "Fly";
    public final static String TYPE_KILL = "Kill";
    public final static String TYPE_HARVEST = "Harvest";
    public final static String TYPE_PLANT = "Plant";
    public final static String TYPE_CRAFT = "Craft";
    public final static String TYPE_ENCHANT = "Enchant";
    public final static String TYPE_FISHING = "Fishing";
}

