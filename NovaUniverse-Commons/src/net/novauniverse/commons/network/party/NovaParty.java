package net.novauniverse.commons.network.party;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.commons.NovaUniverseCommons;
import net.novauniverse.commons.network.novaplayer.NovaPlayer;
import net.novauniverse.commons.network.server.NovaServer;

public class NovaParty {
	private int id;
	private List<UUID> members;
	private int ownerId;

	public NovaParty(int id, List<UUID> members, int ownerId) {
		this.id = id;
		this.members = members;
		this.ownerId = ownerId;
	}

	public int getId() {
		return id;
	}

	public List<UUID> getMembers() {
		return members;
	}
	
	public List<NovaPlayer> getMembersAsNovaPlayerList() {
		List<NovaPlayer> players = new ArrayList<NovaPlayer>();
		for (UUID uuid : members) {
			NovaPlayer player = NovaUniverseCommons.getNetworkManager().getNovaPlayer(uuid, true);
			if (player != null) {
				players.add(player);
			}
		}
		
		return players;
	}
	
	public NovaPlayer getOwner() {
		return NovaUniverseCommons.getNetworkManager().getNovaPlayerById(ownerId);
	}

	public int getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public void broadcast(String message) {
		for (UUID uuid : members) {
			NovaPlayer player = NovaUniverseCommons.getNetworkManager().getNovaPlayer(uuid, true);
			if (player != null) {
				player.sendMessage(message);
			}
		}
	}

	public void warpToServer(NovaServer server) {
		for (UUID uuid : members) {
			NovaPlayer player = NovaUniverseCommons.getNetworkManager().getNovaPlayer(uuid, true);
			if (player != null) {
				if (player.isOnline()) {
					if (player.getServer() != null) {
						if (player.getServer().getId() != server.getId()) {
							player.sendMessage(ChatColor.AQUA + "Trying to send you to " + server.getName() + "...");
							player.sendToServer(server);
						}
					}
				}
			}
		}
	}
}