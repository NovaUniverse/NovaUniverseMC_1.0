package net.novauniverse.bungeecord.listeners;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.novauniverse.bungeecord.NovaUniverseBungeecord;
import net.zeeraa.novacore.commons.async.AsyncManager;

public class WebhookLogListener implements Listener {
	@EventHandler
	public void onPostLogin(PostLoginEvent e) {
		AsyncManager.runAsync(new Runnable() {
			@Override
			public void run() {
				NovaUniverseBungeecord.getInstance().sendWebhookLog("Proxy", "Player " + e.getPlayer().getName() + " (" + e.getPlayer().getUniqueId().toString() + ") joined the server");
			}
		});
	}

	@EventHandler
	public void onPostLogin(PlayerDisconnectEvent e) {
		AsyncManager.runAsync(new Runnable() {
			@Override
			public void run() {
				NovaUniverseBungeecord.getInstance().sendWebhookLog("Proxy", "Player " + e.getPlayer().getName() + " (" + e.getPlayer().getUniqueId().toString() + ") left the server");
			}
		});
	}
}