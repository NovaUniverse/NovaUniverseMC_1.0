package net.novauniverse.bungeecord.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.zeeraa.novacore.commons.log.Log;

public class Log4JRCEFix implements Listener {
	@EventHandler(priority = EventPriority.HIGH)
	public void onChatEvent(ChatEvent e) {
		if (e.getMessage().toLowerCase().contains("${")) {
			Log.warn("Blocked potential RCE attack. Check chat log to find out if this was a real attack");
			e.setCancelled(true);
			e.setMessage("null");
			if (e.getSender() instanceof ProxiedPlayer) {
				((ProxiedPlayer) e.getSender()).disconnect(new TextComponent(ChatColor.DARK_RED + "Illegal text in chat"));
			}
		}
	}
}