package net.novauniverse.commons.utils;

import net.md_5.bungee.api.ChatColor;

public class TextUtils {
	public static String formatPing(int ping) {
		ChatColor color = ChatColor.DARK_RED;

		if (ping < 100) {
			color = ChatColor.GREEN;
		} else if (ping < 150) {
			color = ChatColor.DARK_GREEN;
		} else if (ping < 200) {
			color = ChatColor.YELLOW;
		} else if (ping < 300) {
			color = ChatColor.RED;
		}

		return color + "" + ping;
	}

	public static String formatTps(double tps) {
		return ((tps > 18.0) ? ChatColor.GREEN : (tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED).toString() + ((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
	}
}