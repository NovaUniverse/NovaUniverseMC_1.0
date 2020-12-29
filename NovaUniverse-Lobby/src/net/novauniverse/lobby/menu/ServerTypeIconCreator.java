package net.novauniverse.lobby.menu;

import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.novauniverse.commons.network.server.NovaServerType;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class ServerTypeIconCreator {
	public static ItemStack createIcon(NovaServerType serverType) {
		Material material = Material.DIRT;
		ItemBuilder builder = new ItemBuilder(material);

		builder.setName(ChatColor.GOLD + serverType.getDisplayName());

		builder.addLore(ChatColor.AQUA + "Click to join");

		
		if (serverType.getLore() != null) {
			String[] loreLines = serverType.getLore().split(Pattern.quote("\\n"));
			for(String line : loreLines) {
				builder.addLore(ChatColor.translateAlternateColorCodes('§', line));
			}
		}

		return builder.build();
	}
}