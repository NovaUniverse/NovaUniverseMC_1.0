package net.novauniverse.main.labymod;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import net.labymod.serverapi.bukkit.LabyModPlugin;

public class NovaLabymodAPI {
	public static boolean isLabyModAPIEnabled() {
		// Log.trace("Bukkit.getServer().getPluginManager().getPlugin(\"LabyModAPI\") !=
		// null : " + (Bukkit.getServer().getPluginManager().getPlugin("LabyModAPI") !=
		// null));
		return Bukkit.getServer().getPluginManager().getPlugin("LabyModAPI") != null;
	}

	public static void sendCurrentPlayingGamemode(Player player, boolean visible, String gamemodeName) {
		if (!isLabyModAPIEnabled()) {
			return;
		}

		JsonObject object = new JsonObject();

		object.addProperty("show_gamemode", visible); // Gamemode visible for everyone
		object.addProperty("gamemode_name", gamemodeName); // Name of the current playing gamemode

		// Send to LabyMod using the API
		LabyModPlugin.getInstance().sendServerMessage(player, "server_gamemode", object);

		// Log.trace("Sending laby mod server_gamemode");
	}

	/**
	 * Warning: This seems to mess up the tab list
	 * 
	 * @param player  The {@link Player} to send the update to
	 * @param visible <code>true</code> to show the watermark for
	 */
	public static void sendWatermark(Player player, boolean visible) {
		JsonObject object = new JsonObject();

		// Visibility
		object.addProperty("visible", visible);

		// Send to LabyMod using the API
		LabyModPlugin.getInstance().sendServerMessage(player, "watermark", object);
	}

	public static void updateGameInfo(Player player, boolean hasGame, String gamemode, long startTime, long endTime) {
		if (!isLabyModAPIEnabled()) {
			return;
		}

		// Create game json object
		JsonObject obj = new JsonObject();
		obj.addProperty("hasGame", hasGame);

		if (hasGame) {
			obj.addProperty("game_mode", gamemode);
			obj.addProperty("game_startTime", startTime); // Set to 0 for countdown
			obj.addProperty("game_endTime", endTime); // Set to 0 for timer
		}

		// Send to user
		LabyModPlugin.getInstance().sendServerMessage(player, "discord_rpc", obj);
	}

	public static void sendCineScope(Player player, int coveragePercent, long duration) {
		if (!isLabyModAPIEnabled()) {
			return;
		}

		JsonObject object = new JsonObject();

		// Cinescope height (0% - 50%)
		object.addProperty("coverage", coveragePercent);

		// Duration
		object.addProperty("duration", duration);

		// Send to LabyMod using the API
		LabyModPlugin.getInstance().sendServerMessage(player, "cinescopes", object);
	}
}