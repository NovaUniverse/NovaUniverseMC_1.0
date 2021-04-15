package net.novauniverse.commons.abstraction;

import java.util.UUID;

import net.novauniverse.commons.network.server.NovaServerType;

public interface AbstractServerFinder {
	public void joinServerType(UUID player, NovaServerType type);
	
	public void joinServerType(UUID player, NovaServerType type, boolean silent);
}