package com.peepersoak.adventurecraftcore.world;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.items.scrolls.ScrollFactory;
import com.peepersoak.adventurecraftcore.items.scrolls.ScrollType;
import com.peepersoak.adventurecraftcore.utils.AllPotionEffect;
import com.peepersoak.adventurecraftcore.utils.Flags;
import com.peepersoak.adventurecraftcore.utils.StringPath;
import com.peepersoak.adventurecraftcore.utils.Utils;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ScrollEvents implements Listener {
    AllPotionEffect potionEffect = new AllPotionEffect();
    List<UUID> teleporting = new ArrayList<>();
    List<UUID> requestingPlayer = new ArrayList<>();

    @EventHandler
    public void useScroll(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!e.hasItem()) return;
        if (Objects.requireNonNull(e.getItem()).getType() != Material.PAPER) return;

        Player player = e.getPlayer();
        ItemMeta meta = e.getItem().getItemMeta();
        if (meta == null) return;
        if (!Utils.getPDC(meta).has(StringPath.CUSTOM_SCROLL, PersistentDataType.STRING)) return;
        String scroll = Utils.getPDC(meta).get(StringPath.CUSTOM_SCROLL, PersistentDataType.STRING);
        if (scroll == null) return;

        ScrollType type = ScrollType.valueOf(scroll);

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        if (!query.testState(localPlayer.getLocation(), localPlayer, Flags.ALLOW_SCROLL)) {
            return;
        }

        switch (type) {
            case TELEPORT -> {
                if (!Utils.checkWGState(player, Flags.ALLOW_SCROLL_TP)) return;
                Location bedSpawn = player.getBedSpawnLocation();
                if (bedSpawn != null) {
                    consumeItem(e.getItem(), player);
                    teleport(player, null,bedSpawn);
                } else {
                    player.sendMessage(Utils.color("&cCan't locate your bed spawn!"));
                }
            }

            case ANGELS_BREATH -> {
                List<Entity> entities = player.getNearbyEntities(20,5,20);
                consumeItem(e.getItem(), player);
                for (Entity ent : entities) {
                    if (ent instanceof Player target) {
                        healTarget(player,target);
                    }
                }
                healTarget(player, null);
            }

            case ACCOMPANY -> {
                if (!Utils.checkWGState(player, Flags.ALLOW_SCROLL_TP)) return;
                if (requestingPlayer.contains(player.getUniqueId())) {
                    player.sendMessage(Utils.color("&cYou already have an existing teleport request!"));
                    return;
                }
                requestingPlayer.add(player.getUniqueId());
                openTeleportGUI(player, false);
                consumeItem(e.getItem(), player);
                runTask(player, scroll);
            }

            case MAGNETIC_FORCE -> {
                if (!Utils.checkWGState(player, Flags.ALLOW_SCROLL_TP)) return;
                if (requestingPlayer.contains(player.getUniqueId())) {
                    player.sendMessage(Utils.color("&cYou already have an existing teleport request!"));
                    return;
                }
                requestingPlayer.add(player.getUniqueId());
                openTeleportGUI(player, true);
                consumeItem(e.getItem(), player);
                runTask(player, scroll);
            }

            case ALL_FOR_ONE -> {
                player.addPotionEffects(potionEffect.getPotionList());
                consumeItem(e.getItem(), player);
            }

            case ONE_FOR_ALL -> {
                consumeItem(e.getItem(), player);
                Random rand = new Random();
                int getRandomNumber = rand.nextInt(potionEffect.getPotionList().size());
                PotionEffect effect = potionEffect.getPotionList().get(getRandomNumber);
                List<Entity> entities = player.getNearbyEntities(20,5,20);
                for (Entity ent : entities) {
                    if (ent instanceof Player t) {
                        t.addPotionEffect(effect);
                        t.sendMessage(Utils.color("&6" + player.getName() + " &eused the One for All Scroll!"));
                    }
                }
                player.addPotionEffect(effect);
                player.sendMessage(Utils.color("&eYou used the One for All Scroll!"));
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equalsIgnoreCase("Select Target")) return;
        e.setCancelled(true);
        if (e.getCurrentItem() != null) {
            Player player = (Player) e.getWhoClicked();
            ItemStack item = e.getCurrentItem();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;
            if (!Utils.getPDC(meta).has(StringPath.SCROLL_TP, PersistentDataType.STRING)) return;
            String targetName = Utils.getPDC(meta).get(StringPath.SCROLL_TP, PersistentDataType.STRING);
            if (targetName == null) return;
            Player target = Bukkit.getPlayer(targetName);
            if (target == null) return;
            Integer rawMagnetic = Utils.getPDC(meta).get(StringPath.SCROLL_MAGNETIC, PersistentDataType.INTEGER);
            if (rawMagnetic == null) return;
            if (rawMagnetic == 1) {
                player.sendMessage(Utils.color("&eYou consumed a Magnetic Scroll"));
                target.sendMessage(Utils.color("&6" + player.getName() + " used a Magnetic Scroll to your location!"));

                openTPAcceptGUI(player, target, true);
                player.closeInventory();
            } else {
                player.sendMessage(Utils.color("&eYou consumed an Accompany Scroll"));
                target.sendMessage(Utils.color("&6" + player.getName() + " used an Accompany Scroll to your location!"));

                openTPAcceptGUI(player, target, false);
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onTPAccept(InventoryClickEvent e) {
        if (!e.getView().getTitle().equalsIgnoreCase("Teleport Request")) return;
        e.setCancelled(true);
        if (e.getCurrentItem() != null) {
            Player player = (Player) e.getWhoClicked();
            ItemStack item = e.getCurrentItem();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;

            if (!Utils.getPDC(meta).has(StringPath.ACCEPTED_SCROLL_TP, PersistentDataType.STRING)) return;
            String requesterName = Utils.getPDC(meta).get(StringPath.ACCEPTED_SCROLL_TP, PersistentDataType.STRING);

            if (requesterName == null) return;
            Player requester = Bukkit.getPlayer(requesterName);
            if (requester == null) return;

            Integer rawMagnetic = Utils.getPDC(meta).get(StringPath.SCROLL_MAGNETIC, PersistentDataType.INTEGER);
            if (rawMagnetic == null) return;

            Integer accepted = Utils.getPDC(meta).get(StringPath.ACCEPT_TP, PersistentDataType.INTEGER);
            if (accepted == null) return;

            if (accepted == 1) {
                requester.sendMessage(Utils.color("&6Your teleport request has been accepted!"));
                requestingPlayer.remove(requester.getUniqueId());

                if (rawMagnetic == 1) {
                    teleport(requester, player, null);
                } else {
                    List<Entity> entities = requester.getNearbyEntities(5,5,5);
                    for (Entity ent : entities) {
                        if (ent instanceof Player t) {
                            teleport(t, player, null);
                        }
                    }
                    teleport(requester, player, null);
                }
            } else {
                requester.sendMessage(Utils.color("&cYour teleport request has been denied!"));
                requestingPlayer.remove(requester.getUniqueId());
            }

            player.closeInventory();
        }
    }

    private void openTeleportGUI(Player player, boolean isMagnetic) {
        Inventory inv = Bukkit.createInventory(null, 54, "Select Target");
        int count = 0;
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (player == target) continue;
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();

            int isMag = 0;
            if (isMagnetic) isMag = 1;

            if (meta != null) {
                meta.setOwningPlayer(target);
                Utils.getPDC(meta).set(StringPath.SCROLL_TP, PersistentDataType.STRING, target.getName());
                Utils.getPDC(meta).set(StringPath.SCROLL_MAGNETIC, PersistentDataType.INTEGER, isMag);
            }

            skull.setItemMeta(meta);
            inv.setItem(count, skull);
            count++;
        }
        player.openInventory(inv);
    }

    public void openTPAcceptGUI(Player player, Player target, boolean isMagnetic) {
        Inventory inv = Bukkit.createInventory(null, 27, "Teleport Request");

        int isMag = 0;
        if (isMagnetic) isMag = 1;

        int[] slots = {11, 13, 15};
        for (int slot : slots) {
            ItemStack item;

            ItemMeta meta = null;
            SkullMeta skullMeta;

            switch (slot) {
                case 11 -> {
                    item = new ItemStack(Material.LIME_WOOL);
                    meta = item.getItemMeta();
                    if (meta != null) {
                        Utils.getPDC(meta).set(StringPath.ACCEPTED_SCROLL_TP, PersistentDataType.STRING, player.getName());
                        Utils.getPDC(meta).set(StringPath.SCROLL_MAGNETIC, PersistentDataType.INTEGER, isMag);
                        Utils.getPDC(meta).set(StringPath.ACCEPT_TP, PersistentDataType.INTEGER, 1);
                        meta.setDisplayName(Utils.color("&3Accept"));
                    }
                }
                case 13 -> {
                    item = new ItemStack(Material.PLAYER_HEAD);
                    skullMeta = (SkullMeta) item.getItemMeta();
                    if (skullMeta != null) {
                        skullMeta.setOwningPlayer(player);
                        skullMeta.setDisplayName(Utils.color("&6" + player.getName()));
                    }
                }
                default -> {
                    item = new ItemStack(Material.RED_WOOL);
                    meta = item.getItemMeta();
                    if (meta != null) {
                        Utils.getPDC(meta).set(StringPath.ACCEPTED_SCROLL_TP, PersistentDataType.STRING, player.getName());
                        Utils.getPDC(meta).set(StringPath.SCROLL_MAGNETIC, PersistentDataType.INTEGER, isMag);
                        Utils.getPDC(meta).set(StringPath.ACCEPT_TP, PersistentDataType.INTEGER, 0);
                        meta.setDisplayName(Utils.color("&cDeny"));
                    }
                }
            }

            item.setItemMeta(meta);
            inv.setItem(slot, item);
        }

        target.openInventory(inv);
    }

    private void teleport(Player player, Player target, Location bed) {
        if (teleporting.contains(player.getUniqueId())) {
            player.sendMessage(Utils.color("&cYou are currently teleporting!"));
            return;
        }
        teleporting.add(player.getUniqueId());
        new BukkitRunnable() {
            int count = 5;
            @Override
            public void run() {
                if (count <= 0) {
                    if (bed == null) {
                        if (target != null) {
                            player.teleport(target.getLocation());
                        } else {
                            player.sendMessage(Utils.color("&cPlayer location is missing!"));
                        }
                    } else {
                        player.teleport(bed);
                    }
                    teleporting.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }
                if (count == 5) {
                    player.sendMessage(Utils.color("&6Teleporting in 5 seconds"));
                } else {
                    player.sendMessage(Utils.color("&c" + count));
                }
                count--;
            }
        }.runTaskTimer(AdventureCraftCore.getInstance(), 0, 20);
    }

    private void healTarget(Player player, Player target) {
        if (target == null) {
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
            player.sendMessage(Utils.color( "&eYou got healed!"));
        } else {
            target.setHealth(Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
            target.sendMessage(Utils.color("&6" + player.getName() + " &ehealed you!"));
        }
    }

    private void consumeItem(ItemStack item, Player player) {
        int amount = item.getAmount();
        if (amount == 1) {
            player.getInventory().setItemInMainHand(null);
        } else {
            item.setAmount(amount - 1);
        }
    }

    private void runTask(Player player, String scrollType) {
        new BukkitRunnable() {
            int count = 60;
            @Override
            public void run() {
                if (!requestingPlayer.contains(player.getUniqueId())) {
                    this.cancel();
                    return;
                }
                if (count <= 0 && requestingPlayer.contains(player.getUniqueId())) {
                    ScrollFactory factory = new ScrollFactory();
                    ItemStack itemScroll = factory.createScroll(scrollType);
                    requestingPlayer.remove(player.getUniqueId());
                    Utils.giveItemToPlayer(itemScroll, player);
                    player.sendMessage(Utils.color("&6Scroll has been refunded!"));
                    this.cancel();
                    return;
                }
                if (count == 60 || count == 30 || count == 15) {
                    player.sendMessage(Utils.color("&6TP Request will expire in &b" + count + " &6seconds"));
                }
                if (count == 5) {
                    player.sendMessage(Utils.color("&6TP Request will expire in &b" + count + " &6seconds"));
                }
                if (count < 5) {
                    player.sendMessage(Utils.color("&c" + count));
                }
                count--;
            }
        }.runTaskTimer(AdventureCraftCore.getInstance(), 0, 20);
    }
}
