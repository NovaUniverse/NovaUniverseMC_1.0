package net.novauniverse.commons.network.novaplayer;

import java.util.UUID;

import net.novauniverse.commons.NovaUniverseCommons;
import net.novauniverse.commons.network.NovaNetworkManager;
import net.novauniverse.commons.network.party.NovaParty;
import net.novauniverse.commons.network.server.NovaServer;
import net.zeeraa.novacore.commons.NovaCommons;
import net.zeeraa.novacore.commons.utils.platformindependent.PlatformIndependentPlayerAPI;

public class NovaPlayer {
	private int id;
	
	private UUID uuid;
	private String username;
	
	private int partyId;

	private int serverId;
	private boolean isOnline;

	public NovaPlayer(int id, UUID uuid, String username, int partyId, int serverId, boolean isOnline) {
		this.id = id;
		this.uuid = uuid;
		this.username = username;
		this.partyId = partyId;
		this.serverId = serverId;
		this.isOnline = isOnline;
	}
	
	public int getId() {
		return id;
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getUsername() {
		return username;
	}
	
	public int getPartyId() {
		return partyId;
	}
	
	public void setPartyId(int partyId) {
		this.partyId = partyId;
	}
	
	public NovaParty getParty() {
		if(partyId > 0) {
			return NovaNetworkManager.getInstance().getPartyById(partyId);
		}
		
		return null;
	}
	
	public NovaServer getServer() {
		return NovaUniverseCommons.getNetworkManager().getServerById(serverId);
	}

	public int getServerId() {
		return serverId;
	}
	
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	
	public boolean isOnline() {
		return isOnline;
	}
	
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	
	public void sendMessage(String message) {
		PlatformIndependentPlayerAPI.get().sendMessage(uuid, message);
	}
	
	public void sendToServer(NovaServer server) {
		NovaCommons.getPlatformIndependentBungeecordAPI().sendPlayerToServer(uuid, server.getName());
	}
}