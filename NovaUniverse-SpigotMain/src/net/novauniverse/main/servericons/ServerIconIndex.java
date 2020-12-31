package net.novauniverse.main.servericons;

import java.io.IOException;
import java.util.HashMap;

import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

import net.novauniverse.commons.network.server.NovaServerType;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.utils.BukkitSerailization;

public class ServerIconIndex {
	private static HashMap<String, ItemStack> serverIcons = new HashMap<String, ItemStack>();

	public static HashMap<String, ItemStack> getServerIcons() {
		return serverIcons;
	}

	public static void load(JSONObject json) {
		serverIcons.clear();
		for (String key : json.keySet()) {
			String base64Data = json.getString(key);

			try {
				ItemStack icon = BukkitSerailization.itemStackFromBase64(base64Data);

				serverIcons.put(key.toLowerCase(), icon);
			} catch (IOException e) {
				Log.error("ServerIconIndex", "Failed to decode icon for " + key + ". " + e.getClass().getName() + " " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public static ItemStack getServerIcon(NovaServerType type) {
		return ServerIconIndex.getServerIcon(type.getName());
	}

	public static ItemStack getServerIcon(String type) {
		return serverIcons.get(type.toLowerCase());
	}

	public static boolean hasServerIcon(NovaServerType type) {
		return ServerIconIndex.hasServerIcon(type.getName());
	}

	public static boolean hasServerIcon(String type) {
		return serverIcons.containsKey(type.toLowerCase());
	}
}