package net.novauniverse.bungeecord.chatlogger;

import java.sql.SQLException;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.novauniverse.commons.network.NovaNetworkManager;
import net.zeeraa.novacore.commons.log.Log;

public class ChatLogger implements Listener {
	@EventHandler(priority = EventPriority.HIGH)
	public void onChatEvent(ChatEvent e) {
		if (e.getSender() instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) e.getSender();
			try {
				NovaNetworkManager.storeChatMessage(player.getUniqueId(), e.getMessage(), e.isCommand(), e.isCancelled());
			} catch (SQLException ex) {
				Log.error("ChatLogger", "Failed to log chat message: " + ex.getClass().getName() + " " + ex.getMessage());
			}
		}
	}
}