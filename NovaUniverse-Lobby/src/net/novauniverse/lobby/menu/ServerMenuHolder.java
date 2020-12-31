package net.novauniverse.lobby.menu;

import java.util.HashMap;
import java.util.Map;

import net.novauniverse.commons.network.server.NovaServerType;
import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIReadOnlyHolder;

public class ServerMenuHolder extends GUIReadOnlyHolder {
	private Map<NovaServerType, Integer> serverTypeSlots = new HashMap<NovaServerType, Integer>();

	public Map<NovaServerType, Integer> getServerTypeSlots() {
		return serverTypeSlots;
	}
}