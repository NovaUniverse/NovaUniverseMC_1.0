package net.novauniverse.bungeecord.serverfinder;

import java.sql.SQLException;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.bungeecord.NovaUniverseBungeecord;
import net.novauniverse.commons.abstraction.AbstractServerFinder;
import net.novauniverse.commons.network.server.NovaServerType;
import net.zeeraa.novacore.commons.NovaCommons;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.platformindependent.PlatformIndependentPlayerAPI;

public class ServerFinder implements AbstractServerFinder {
	@Override
	public void joinServerType(UUID player, NovaServerType type) {
		this.joinServerType(player, type, false);
	}

	@Override
	public void joinServerType(UUID player, NovaServerType type, boolean silent) {
		try {
			boolean result = NovaUniverseBungeecord.getInstance().getNetworkManager().sendPlayerToServer(player, type);
			
			if(!result) {
				Log.trace("ServerFinder", "NovaNetworkManager#sendPlayerToServer(UUID, NovaServerType) returned false");
				if(!silent) {
					PlatformIndependentPlayerAPI.get().sendMessage(player, ChatColor.RED+ "Could not find any available servers. Please try again later");
				}
			}
		} catch (SQLException e) {
			NovaCommons.getAbstractPlayerMessageSender().trySendMessage(player, ChatColor.RED + "Failed to join server. Reason: " + e.getClass().getName());
			e.printStackTrace();
		}
	}
}