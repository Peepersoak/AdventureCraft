package com.peepersoak.adventurecraftcore.achievement;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;

public class Achievement {

	public void sendAchievement(String achievement, String playerName) {

		BaseComponent component = null;

		if (achievement.equalsIgnoreCase("Marshmallow")) {
			TextComponent tc = new TextComponent(ChatColor.AQUA + playerName + ChatColor.GREEN + " has made the advancement "
					+ ChatColor.RED + "[Marshmallow!]");
			tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new Text(ChatColor.RED + "Hit a Ghast using trident!")));
			component = tc;
		} 
		
		else if (achievement.equalsIgnoreCase("Forbidden_Land")) {
			
			TextComponent tc = new TextComponent(ChatColor.AQUA + playerName + ChatColor.GREEN + " has made the advancement "
					+ ChatColor.RED + "[Forbidden Land!]");
			tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new Text(ChatColor.RED + "kill a level 50 Mob")));
			component = tc;
		} 
		
		else if (achievement.equalsIgnoreCase("Exceeding_Limit")) {
			
			TextComponent tc = new TextComponent(ChatColor.AQUA + playerName + ChatColor.GREEN + " has made the advancement "
					+ ChatColor.RED + "[Exceeding The Limit!]");
			tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new Text(ChatColor.RED + "Enchant your tools past their limit")));
			component = tc;
			
		} else if (achievement.equalsIgnoreCase("Touching_The_Unkown")) {

			TextComponent tc = new TextComponent(ChatColor.AQUA + playerName + ChatColor.GREEN + " has made the advancement "
					+ ChatColor.RED + "[Touching The Unknown!]");
			tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new Text(ChatColor.RED + "Enchant your item with skills")));
			component = tc;
			
		} else if (achievement.equalsIgnoreCase("God_Among_Men")) {

			TextComponent tc = new TextComponent(ChatColor.AQUA + playerName + ChatColor.GREEN + " has made the advancement "
					+ ChatColor.RED + "[God Among Men!]");
			tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new Text(ChatColor.RED + "Increase your health past their limits")));
			component = tc;
			
		} else if (achievement.equalsIgnoreCase("The_Ruler")) {
			TextComponent tc = new TextComponent(ChatColor.AQUA + playerName + ChatColor.GREEN + " has made the advancement "
					+ ChatColor.RED + "[The Ruler!]");
			tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new Text(ChatColor.RED + "Use arise for the first time")));
			component = tc;
		}
		
		else if (achievement.equalsIgnoreCase("The_Legendary")) {
			TextComponent tc = new TextComponent(ChatColor.AQUA + playerName + ChatColor.GREEN + " has made the advancement "
					+ ChatColor.RED + "[The Legendary]");
			tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new Text(ChatColor.RED + "Kill a level 100 mob")));
			component = tc;
		}

		if (component != null) {
			Bukkit.spigot().broadcast(component);
		}
	}
}
