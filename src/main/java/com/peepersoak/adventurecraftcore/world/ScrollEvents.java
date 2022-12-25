package com.peepersoak.adventurecraftcore.world;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.items.scrolls.ScrollType;
import com.peepersoak.adventurecraftcore.utils.AllPotionEffect;
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

        if (!query.testState(localPlayer.getLocation(), localPlayer, AdventureCraftCore.PRISON_FLAG)) {
            return;
        }

        switch (type) {
            case TELEPORT -> {
                if (!Utils.checkWGState(player, AdventureCraftCore.ALLOW_SCROLL_TP)) return;
                Location bedSpawn = player.getBedSpawnLocation();
                if (bedSpawn != null) {
                    consumeItem(e.getItem(), player);
                    teleport(player, bedSpawn);
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
                if (!Utils.checkWGState(player, AdventureCraftCore.ALLOW_SCROLL_TP)) return;
                openTeleportGUI(player, false);
                consumeItem(e.getItem(), player);
            }

            case MAGNETIC_FORCE -> {
                if (!Utils.checkWGState(player, AdventureCraftCore.ALLOW_SCROLL_TP)) return;
                openTeleportGUI(player, true);
                consumeItem(e.getItem(), player);
            }

            case ALL_FOR_ONE -> {
                System.out.println(Utils.color("&ePowerrrrrrrrrr"));
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
                teleport(player, target.getLocation());
                player.sendMessage(Utils.color("&eYou consumed a Magnetic Scroll"));
                target.sendMessage(Utils.color("&6" + player.getName() + " used a Magnetic Scroll to your location!"));
                player.closeInventory();
            } else {
                player.sendMessage(Utils.color("&eYou consumed an Accompany Scroll"));
                target.sendMessage(Utils.color("&6" + player.getName() + " used an Accompany Scroll to your location!"));
                player.closeInventory();
                List<Entity> entities = player.getNearbyEntities(5,5,5);
                for (Entity ent : entities) {
                    if (ent instanceof Player t) {
                        teleport(t, target.getLocation());
                    }
                }
                teleport(player, target.getLocation());
            }
        }
    }

    private void openTeleportGUI(Player player, boolean isMagnetic) {
        Inventory inv = Bukkit.createInventory(null, 54, "Select Target");
        int count = 0;
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (player == target) continue;
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(target);
                Utils.getPDC(meta).set(StringPath.SCROLL_TP, PersistentDataType.STRING, target.getName());
                Utils.getPDC(meta).set(StringPath.SCROLL_MAGNETIC, PersistentDataType.INTEGER, isMagnetic ? 1 : 0);
            }
            skull.setItemMeta(meta);
            inv.setItem(count, skull);
            count++;
        }
        player.openInventory(inv);
    }

    private void teleport(Player player, Location location) {
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
                    player.teleport(location);
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
            player.sendMessage(Utils.color( "&eYou got healed you!"));
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

    private boolean isSameLocation(Location init, Location comp) {
        return init.getBlockX() == comp.getBlockX() && init.getBlockY() == comp.getBlockY() && init.getBlockZ() == comp.getBlockZ();
    }
}
