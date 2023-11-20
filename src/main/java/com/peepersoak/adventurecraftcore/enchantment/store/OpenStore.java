package com.peepersoak.adventurecraftcore.enchantment.store;

import com.peepersoak.adventurecraftcore.AdventureCraftCore;
import com.peepersoak.adventurecraftcore.enchantment.Enchantments;
import com.peepersoak.adventurecraftcore.enchantment.ItemFactory;
import com.peepersoak.adventurecraftcore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class OpenStore implements CommandExecutor, Listener {
    private final Enchantments enchantments;
    private final ItemFactory itemFactory = new ItemFactory();

    private final HashMap<UUID, Enchantment> enchantment = new HashMap<>();
    private final HashMap<UUID, Integer> enchantLevel = new HashMap<>();
    private final HashMap<UUID, Double> dealCost = new HashMap<>();
    private final HashMap<UUID, Location> playerLocation = new HashMap<>();

    private final NamespacedKey enchentKey = new NamespacedKey(AdventureCraftCore.getInstance(), "EnchantKey");

    public OpenStore() {
        enchantments = new Enchantments();
        enchantments.setAllEnchantment();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!player.isOp()) return false;
        }

        if (args.length <= 0) return false;

        String name = args[0];
        Player player = Bukkit.getPlayer(name);
        if (player == null) return false;
        clearDeal(player);

        playerLocation.put(player.getUniqueId(), player.getLocation());

        player.sendMessage(Utils.color("&6Select the enchantment you want."));

        Inventory inv = Bukkit.createInventory(null, 54, "Select Enchantment");
        for (String enchant : enchantments.getNormalEnchant()) {
            ItemStack mat = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta meta = mat.getItemMeta();
            if (meta == null) return false;
            meta.setDisplayName(Utils.color("&6" + enchant.toUpperCase().replace("_", " ")));
            meta.getPersistentDataContainer().set(enchentKey, PersistentDataType.STRING, enchant);
            mat.setItemMeta(meta);
            inv.addItem(mat);
        }

        player.openInventory(inv);

        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (!e.getView().getTitle().equalsIgnoreCase("Select Enchantment")) return;
        if (e.getCurrentItem() == null) return;
        if (!(e.getWhoClicked() instanceof Player player)) return;
        e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        String enchant = meta.getPersistentDataContainer().get(enchentKey, PersistentDataType.STRING);
        if (enchant != null) {
            enchantment.put(player.getUniqueId(), Enchantment.getByKey(NamespacedKey.minecraft(enchant.toLowerCase().replace(" ", "_"))));
            player.sendMessage(Utils.color("&6Enter the Enchantment Level you want"));
            player.closeInventory();
        }
    }

    @EventHandler
    public void onBuy(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (!e.getView().getTitle().equalsIgnoreCase("Enchantment Deal")) return;
        if (e.getSlot() != 13) return;
        if (!(e.getWhoClicked() instanceof Player player)) return;
        e.setCancelled(true);

        double balance = AdventureCraftCore.getEconomy().getBalance(player);
        double cost = dealCost.get(player.getUniqueId());
        if (balance < cost) {
            player.sendMessage(Utils.color("&cYou don't have enough money! &4Don't come back again if you don't have money!!"));
            player.closeInventory();
            clearDeal(player);
        } else {
            AdventureCraftCore.getEconomy().withdrawPlayer(player, cost);
            player.sendMessage(Utils.color("&b$" + cost + " &6has been deducted from your balance."));

            String enchantName = enchantment.get(player.getUniqueId()).getKey().getKey().toUpperCase();
            int level = enchantLevel.get(player.getUniqueId());

            ItemStack book = itemFactory.createBook(level, "normal", enchantName);
            HashMap<Integer, ItemStack> returnItem = player.getInventory().addItem(book);

            player.closeInventory();

            if (returnItem.isEmpty()) return;

            for (ItemStack item : returnItem.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (!enchantment.containsKey(player.getUniqueId())) return;
        e.setCancelled(true);

        if (e.getMessage().equalsIgnoreCase("cancel")) {
            player.sendMessage(Utils.color("&bMargrave &cdeal was cancelled!"));
            clearDeal(player);
            return;
        }

        try {
            int level = Integer.parseInt(e.getMessage());
            if (level > 200) {
                player.sendMessage(Utils.color("&cEnter a number less than or equal to &b200"));
                return;
            }
            int maxLevel = enchantment.get(player.getUniqueId()).getMaxLevel();
            if (level <= maxLevel) {
                player.sendMessage(Utils.color("&cEnter a number greater than &b" + maxLevel));
                return;
            }

            player.sendMessage(Utils.color("&eHere's my deal for you"));

            enchantLevel.put(player.getUniqueId(), level);
            openConfirmation(player);
        } catch (NumberFormatException ex) {
            player.sendMessage(Utils.color("&cEnter a valid number!"));
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (!playerLocation.containsKey(player.getUniqueId())) return;

        Location lastLocation = playerLocation.get(player.getUniqueId());
        if (player.getWorld() != lastLocation.getWorld()) {
            player.sendMessage(Utils.color("&bMargrave &cdeal was cancelled!"));
            clearDeal(player);
        } else if (player.getLocation().distance(lastLocation) > 5) {
            player.sendMessage(Utils.color("&bMargrave &cdeal was cancelled!"));
            clearDeal(player);
        }
    }

    private void clearDeal(Player player) {
        enchantment.remove(player.getUniqueId());
        enchantLevel.remove(player.getUniqueId());
        dealCost.remove(player.getUniqueId());
        playerLocation.remove(player.getUniqueId());
    }

    private void openConfirmation(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "Enchantment Deal");
        ItemStack display = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = display.getItemMeta();
        if (meta == null) return;
        meta.setDisplayName(Utils.color("&6" + enchantment.get(player.getUniqueId()).getKey().getKey().replace("_", " ").toUpperCase()));

        int maxLevel = enchantment.get(player.getUniqueId()).getMaxLevel() + 1;
        double cost = 500000 + (100000 * (enchantLevel.get(player.getUniqueId()) - maxLevel));

        dealCost.put(player.getUniqueId(), cost);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(Utils.color("&eCost: &b$" + cost));
        lore.add("");
        meta.setLore(lore);
        display.setItemMeta(meta);

        for (int i = 0; i < 27; i++) {
            if (i == 13) {
                inv.setItem(i, display);
            } else {
                ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                ItemMeta paneItemMeta= pane.getItemMeta();
                if (paneItemMeta == null)return;
                paneItemMeta.setDisplayName(Utils.color("&6"));
                pane.setItemMeta(meta);
                inv.setItem(i, pane);
            }
        }

        Bukkit.getScheduler().runTask(AdventureCraftCore.getInstance(), () -> player.openInventory(inv));
    }
}
