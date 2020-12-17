package net.novauniverse.lobby.misc;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerMessages {
	public static String getJoinMessage(Player player) {
		return ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + ChatColor.BOLD + "+" + ChatColor.RESET + ChatColor.DARK_GRAY + "] " + ChatColor.RESET + ChatColor.YELLOW + player.getName() + ChatColor.RESET + ChatColor.GRAY + " Joined";
	}

	public static String getLeaveMessage(Player player) {
		return ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + ChatColor.BOLD + "-" + ChatColor.RESET + ChatColor.DARK_GRAY + "] " + ChatColor.RESET + ChatColor.YELLOW + player.getName() + ChatColor.RESET + ChatColor.GRAY + " Left";
	}
}