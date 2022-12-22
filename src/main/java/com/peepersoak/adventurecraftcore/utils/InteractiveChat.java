package com.peepersoak.adventurecraftcore.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventHandler;

public class InteractiveChat implements Listener {

    @Deprecated
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        String str = e.getMessage();

        Player player = e.getPlayer();

        if (str.contains("[i]")) {
            String message = sendItem(player, e.getMessage());
            if (message != null) {
                e.setMessage(message);
            }
        }

        else if (str.equalsIgnoreCase("[inv]")) {
            openInventory(e.getPlayer());
            e.setCancelled(true);
        }

        e.setMessage(Utils.color(e.getMessage()));
    }

    @Deprecated
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        boolean isCustom = e.getView().getTitle().contains("[Inventory]");
        if (isCustom) {
            e.setCancelled(true);
        }
    }

    @Deprecated
    private String sendItem(Player player, String msg) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) return null;

        String type = item.getItemMeta().getDisplayName().equalsIgnoreCase("") ?
                item.getType().toString().replace("_", " ").toLowerCase() :
                item.getItemMeta().getDisplayName();
        int amount = item.getAmount();

        String str;
        if (msg.equalsIgnoreCase("[i]")) {
            str = Utils.color("&eIs holding &6[" + type + " " + amount +"x" + "&6]");
        } else {
            str = Utils.color("&6[" + type + " " + amount +"x" + "&6]");
        }

        return msg.replace("[i]", str);
    }

    @Deprecated
    private void openInventory(Player player) {
        TextComponent textComponent = new TextComponent(Utils.color("&d[" + player.getName() + " Inventory]"));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/open inv " + player.getName()));
        Bukkit.spigot().broadcast(textComponent);
    }
}
