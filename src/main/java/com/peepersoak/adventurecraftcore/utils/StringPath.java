package com.peepersoak.adventurecraftcore.utils;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import org.bukkit.NamespacedKey;

public class StringPath {

    public static NamespacedKey MOB_LEVEL_KEY = new NamespacedKey(AdventureCraftCore.getInstance(), "MobLevel");
    public static NamespacedKey CUSTOM_ZOMBIE = new NamespacedKey(AdventureCraftCore.getInstance(), "CustomZombie");
    public static NamespacedKey CUSTOM_SQUID = new NamespacedKey(AdventureCraftCore.getInstance(), "CustomSquid");
    public static NamespacedKey MOB_XP = new NamespacedKey(AdventureCraftCore.getInstance(), "MobXP");
    public static NamespacedKey MOB_XP_GOAL = new NamespacedKey(AdventureCraftCore.getInstance(), "GoalXP");
    public static NamespacedKey CUSTOM_ARROW = new NamespacedKey(AdventureCraftCore.getInstance(), "CustomArrow");
    public static NamespacedKey CUSTOM_SCROLL = new NamespacedKey(AdventureCraftCore.getInstance(), "CustomScroll");
    public static NamespacedKey CUSTOM_WARD = new NamespacedKey(AdventureCraftCore.getInstance(), "CustomWard");
    public static NamespacedKey SCROLL_TP = new NamespacedKey(AdventureCraftCore.getInstance(), "ShouldUseTPScroll");
    public static NamespacedKey SCROLL_MAGNETIC = new NamespacedKey(AdventureCraftCore.getInstance(), "ScrollIsMagnetic");
    public static NamespacedKey WARD_WITCH = new NamespacedKey(AdventureCraftCore.getInstance(), "CustomWitchWard");
    public static NamespacedKey ENCHANT_META = new NamespacedKey(AdventureCraftCore.getInstance(), "EnchantMeta");
    public static NamespacedKey DUNGEON_LIFE = new NamespacedKey(AdventureCraftCore.getInstance(), "DungeonLife");
    public static NamespacedKey ACCEPTED_SCROLL_TP = new NamespacedKey(AdventureCraftCore.getInstance(), "AcceptedScrollTP");
    public static NamespacedKey ACCEPT_TP = new NamespacedKey(AdventureCraftCore.getInstance(), "AcceptTeleport");

    public static String MODERATOR_PERMISSION = "adventurecraft.moderator";
    public static String AFK_BYPASS_PERMISSION = "adventurecraft.afk_bypass";
}
