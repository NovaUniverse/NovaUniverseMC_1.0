package net.novauniverse.bungeecord.chatlogger.serverfinder;

import java.sql.SQLException;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.bungeecord.NovaUniverseBungeecord;
import net.novauniverse.commons.abstraction.AbstractServerFinder;
import net.novauniverse.commons.network.server.NovaServerType;
import net.zeeraa.novacore.commons.NovaCommons;

public class ServerFinder implements AbstractServerFinder {
	@Override
	public void joinServerType(UUID player, NovaServerType type) {
		try {
			NovaUniverseBungeecord.getInstance().getNetworkManager().sendPlayerToServer(player, type);
		} catch (SQLException e) {
			NovaCommons.getAbstractPlayerMessageSender().trySendMessage(player, ChatColor.RED + "Failed to join server. Reason: " + e.getClass().getName());
			e.printStackTrace();
		}
	}
}