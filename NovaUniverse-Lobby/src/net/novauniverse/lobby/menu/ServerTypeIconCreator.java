package net.novauniverse.lobby.menu;

import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.novauniverse.commons.network.server.NovaServerType;
import net.novauniverse.main.servericons.ServerIconIndex;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class ServerTypeIconCreator {
	public static ItemStack createIcon(NovaServerType serverType) {
		ItemBuilder builder;

		if (ServerIconIndex.hasServerIcon(serverType)) {
			builder = new ItemBuilder(ServerIconIndex.getServerIcon(serverType).clone());
		} else {
			builder = new ItemBuilder(Material.DIRT);
		}

		builder.setName(ChatColor.GOLD + serverType.getDisplayName());

		builder.addLore(ChatColor.AQUA + "" + serverType.getPlayerCount() + ChatColor.GOLD + " Players online");

		if (serverType.getLore() != null) {
			String[] loreLines = serverType.getLore().split(Pattern.quote("\\n"));
			for (String line : loreLines) {
				builder.addLore(ChatColor.translateAlternateColorCodes('§', line));
			}
			builder.addLore(" ");
		}

		builder.addLore(ChatColor.AQUA + "Click to join");

		return builder.build();
	}
}