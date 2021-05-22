package net.novauniverse.bungeecord.domainbind;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.novauniverse.bungeecord.NovaUniverseBungeecord;
import net.novauniverse.commons.network.server.NovaServerType;
import net.zeeraa.novacore.commons.log.Log;

public class DomainBindListener implements Listener {
	private Map<String, NovaServerType> redirect = new HashMap<String, NovaServerType>();

	@EventHandler
	public void onPreLogin(PreLoginEvent e) {
		if (redirect.containsKey(e.getConnection().getName())) {
			redirect.remove(e.getConnection().getName());
		}

		if (e.getConnection().isLegacy()) {
			return;
		}

		InetSocketAddress address = e.getConnection().getVirtualHost();

		if (address == null) {
			return;
		}

		String hostname = address.getHostName().toLowerCase();
		Log.info(e.getConnection().getName() + " is connecting with " + hostname);

		for (String key : NovaUniverseBungeecord.getInstance().getDomainBinds().keySet()) {
			if (key.equalsIgnoreCase(hostname)) {
				redirect.put(e.getConnection().getName(), NovaUniverseBungeecord.getInstance().getDomainBinds().get(key));
			}
		}
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent e) {
		if (redirect.containsKey(e.getPlayer().getName())) {
			redirect.remove(e.getPlayer().getName());
		}
	}

	public Map<String, NovaServerType> getRedirect() {
		return redirect;
	}
}