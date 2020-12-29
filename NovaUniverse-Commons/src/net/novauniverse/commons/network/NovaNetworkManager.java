package net.novauniverse.commons.network;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import net.novauniverse.commons.NovaUniverseCommons;
import net.novauniverse.commons.network.server.NovaServer;
import net.novauniverse.commons.network.server.NovaServerType;
import net.zeeraa.novacore.commons.NovaCommons;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.RandomGenerator;

public class NovaNetworkManager {
	public static final int SERVER_NAME_GENERATION_ATTEMPTS = 1000;
	public static final int SERVER_NAME_NUMBER_LENGTH = 4;

	private List<NovaServerType> serverTypes = new ArrayList<NovaServerType>();
	private List<NovaServer> servers = new ArrayList<NovaServer>();

	public NovaNetworkManager() {

	}

	public void updateTypes(boolean clean) throws SQLException {
		if (clean) {
			serverTypes.clear();
		}

		PreparedStatement ps;
		ResultSet rs;

		ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement("SELECT * FROM server_type");
		rs = ps.executeQuery();

		List<NovaServerType> serverTypesNew = new ArrayList<NovaServerType>();
		Map<NovaServerType, Integer> returnServerType = new HashMap<NovaServerType, Integer>();

		while (rs.next()) {
			NovaServerType newServerType = new NovaServerType(rs.getInt("id"), rs.getString("name"), rs.getString("display_name"), rs.getInt("soft_player_limit"), rs.getInt("hard_player_limit"), rs.getInt("target_player_count"), rs.getBoolean("is_minigame"), null, rs.getString("server_naming_scheme"), rs.getString("lore"), rs.getBoolean("show_in_server_list"));
			serverTypesNew.add(newServerType);
			int returnType = rs.getInt("return_to_server_type_id");

			if (returnType > 0) {
				returnServerType.put(newServerType, returnType);
			}
		}

		for (NovaServerType st : returnServerType.keySet()) {
			int t = returnServerType.get(st);

			for (NovaServerType target : serverTypesNew) {
				if (target.getId() == t) {
					st.setReturnToServerType(target);
					continue;
				}
			}
		}

		for (int i = serverTypes.size() - 1; i >= 0; i--) {
			NovaServerType old = serverTypes.get(i);

			if (!serverTypesNew.contains(old)) {
				serverTypes.remove(i);
				Log.trace("NetworkManager", "Adding server type: " + old.getName() + " (" + old.getDisplayName() + ")");
			} else {
				serverTypesNew.remove(old);
			}
		}

		for (NovaServerType newType : serverTypesNew) {
			serverTypes.add(newType);
			Log.trace("NetworkManager", "Adding server type: " + newType.getName() + " (" + newType.getDisplayName() + ")");
		}

		rs.close();
		ps.close();
	}

	public void updateServers(boolean clean) throws SQLException {
		if (clean) {
			servers.clear();
		}

		PreparedStatement ps;
		ResultSet rs;

		ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement("SELECT * FROM servers WHERE TIMESTAMPDIFF(MINUTE, heartbeat, CURRENT_TIMESTAMP) < 3");
		rs = ps.executeQuery();

		List<NovaServer> newServers = new ArrayList<NovaServer>();

		while (rs.next()) {
			int typeId = rs.getInt("type_id");

			NovaServerType type = this.getServerTypeByID(typeId);

			if (type == null) {
				System.err.println("Missing server type id: " + typeId);
				continue;
			}

			NovaServer server = new NovaServer(rs.getInt("id"), rs.getString("name"), rs.getString("host"), rs.getInt("port"), type, rs.getBoolean("minigame_started"), rs.getBoolean("has_failed"));

			newServers.add(server);
		}

		for (int i = servers.size() - 1; i >= 0; i--) {
			NovaServer old = servers.get(i);

			if (!newServers.contains(old)) {
				servers.remove(i);
				Log.trace("NetworkManager", "Removing server: " + old.getName() + " " + old.getHost() + ":" + old.getPort());
			} else {
				newServers.remove(old);
			}
		}

		for (NovaServer newServer : newServers) {
			servers.add(newServer);
			Log.trace("NetworkManager", "Adding server: " + newServer.getName() + " " + newServer.getHost() + ":" + newServer.getPort());
		}

		rs.close();
		ps.close();
	}

	public void clearServers() {
		servers.clear();
	}

	public void clearServerTypes() {
		serverTypes.clear();
	}

	public void update(boolean clean) throws SQLException {
		updateTypes(clean);
		updateServers(clean);
	}

	public List<NovaServer> getServers() {
		return servers;
	}

	public NovaServer getServerById(int id) {
		for (NovaServer server : servers) {
			Log.trace(server.getName() + " " + server.getId() + " = " + id);
			if (server.getId() == id) {
				return server;
			}
		}
		return null;
	}

	public List<NovaServerType> getServerTypes() {
		return serverTypes;
	}

	public NovaServerType getServerTypeByID(int id) {
		for (NovaServerType type : serverTypes) {
			if (type.getId() == id) {
				return type;
			}
		}
		return null;
	}

	public NovaServerType getServerTypeByName(String name) {
		for (NovaServerType serverType : serverTypes) {
			if (serverType.getName().equalsIgnoreCase(name)) {
				return serverType;
			}
		}
		return null;
	}

	public NovaServer getServerByName(String name) {
		for (NovaServer server : servers) {
			if (server.getName().equalsIgnoreCase(name)) {
				return server;
			}
		}
		return null;
	}

	public List<NovaServer> getServerByType(NovaServerType type) {
		List<NovaServer> result = new ArrayList<NovaServer>();

		for (NovaServer server : servers) {
			if (server.getServerType().equals(type)) {
				result.add(server);
			}
		}

		return result;
	}

	public static String generateServerName(NovaServerType serverType) throws SQLException {
		return NovaNetworkManager.generateServerName(serverType, 1000);
	}

	public static String generateServerName(NovaServerType serverType, int maxTries) throws SQLException {
		Random random = new Random();

		for (int i = 0; i < maxTries; i++) {
			String number = "";
			for (int j = 0; j < SERVER_NAME_NUMBER_LENGTH; j++) {
				number += "" + RandomGenerator.generate(0, 9, random);
			}

			String name = serverType.getServerNamingScheme() + number;

			Log.trace("NetworkManager", "Checking if server name " + name + " is avaliable");

			PreparedStatement ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement("SELECT id FROM servers WHERE name = ? LIMIT 1");
			ps.setString(1, name);

			ResultSet rs = ps.executeQuery();

			boolean invalid = rs.next();

			ps.close();
			rs.close();

			if (invalid) {
				continue;
			}

			return name;
		}
		return null;
	}

	public static int registerServer(String serverName, String serverHost, int port, NovaServerType serverType) throws SQLException {
		int serverId = -1;

		String sql = "INSERT INTO servers (name, host, port, type_id) VALUES (?, ?, ?, ?)";
		PreparedStatement ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

		ps.setString(1, serverName);
		ps.setString(2, serverHost);
		ps.setInt(3, port);
		ps.setInt(4, serverType.getId());

		ps.executeUpdate();

		ResultSet rs = ps.getGeneratedKeys();

		if (rs.next()) {
			serverId = rs.getInt(1);
		}

		rs.close();
		ps.close();

		return serverId;
	}

	public static boolean updateHeartbeat(int serverId) throws SQLException {
		String sql = "UPDATE servers SET heartbeat = CURRENT_TIMESTAMP WHERE id = ?";
		PreparedStatement ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);

		ps.setInt(1, serverId);

		int rows = ps.executeUpdate();

		ps.close();

		return rows > 0;
	}

	public static void unregisterServer(int serverId) throws SQLException {
		String sql = "DELETE FROM servers WHERE id = ?";
		PreparedStatement ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);

		ps.setInt(1, serverId);
		ps.executeUpdate();
		ps.close();
	}

	public boolean sendPlayerToServer(UUID player, NovaServerType serverType) throws SQLException {
		NovaServer server = this.findServer(serverType);

		//Log.trace("NetworkManager", "NovaNetworkManager.sendPlayerToServer() " + server);

		if (server != null) {
			return NovaCommons.getPlatformIndependentBungeecordAPI().sendPlayerToServer(player, server.getName());
		}

		return false;
	}

	public NovaServer findServer(NovaServerType type) throws SQLException {
		NovaServer server = null;

		String sql = "CALL find_server(?)";
		PreparedStatement ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);

		ps.setInt(1, type.getId());

		ResultSet rs = ps.executeQuery();

		if (rs.next()) {
			server = this.getServerById(rs.getInt("id"));
			//Log.trace("NetworkMagaer", "Found server " + rs.getString("name"));
			//Log.trace("NetworkManager", "" + server);
		}

		rs.close();
		ps.close();

		return server;
	}

	public static boolean flagAsGameStarted(int serverId) throws SQLException {
		String sql = "UPDATE servers SET minigame_started = 1 WHERE id = ?";
		PreparedStatement ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);

		ps.setInt(1, serverId);

		int rows = ps.executeUpdate();

		ps.close();

		return rows > 0;
	}

	public static boolean flagServerAsFailed(int serverId) throws SQLException {
		String sql = "UPDATE servers SET has_failed = 1 WHERE id = ?";
		PreparedStatement ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);

		ps.setInt(1, serverId);

		int rows = ps.executeUpdate();

		ps.close();

		return rows > 0;
	}
}