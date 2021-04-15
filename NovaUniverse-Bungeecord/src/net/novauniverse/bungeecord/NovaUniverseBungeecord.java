package net.novauniverse.bungeecord;

import java.net.InetSocketAddress;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.novauniverse.bungeecord.chatlogger.ChatLogger;
import net.novauniverse.bungeecord.chatlogger.serverfinder.ServerFinder;
import net.novauniverse.bungeecord.commands.JoinCommand;
import net.novauniverse.bungeecord.commands.SpectateCommand;
import net.novauniverse.bungeecord.pluginmessagelistener.PluginMessageListener;
import net.novauniverse.commons.NovaUniverseCommons;
import net.novauniverse.commons.network.NovaNetworkManager;
import net.novauniverse.commons.network.server.NovaServer;
import net.novauniverse.commons.network.server.NovaServerType;
import net.zeeraa.novacore.bungeecord.novaplugin.NovaPlugin;
import net.zeeraa.novacore.bungeecord.task.AdvancedTask;
import net.zeeraa.novacore.commons.database.DBConnection;
import net.zeeraa.novacore.commons.database.DBCredentials;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;

public class NovaUniverseBungeecord extends NovaPlugin implements Listener {
	private static NovaUniverseBungeecord instance;

	private List<String> originalServers;

	private Task networkUpdateTask;
	private Task playerHeartbeatTask;
	private Task cleanupTask;
	private NovaNetworkManager networkManager;

	private NovaServerType lobbyType;

	private String defaultServerName;

	public static NovaUniverseBungeecord getInstance() {
		return instance;
	}

	public NovaNetworkManager getNetworkManager() {
		return networkManager;
	}

	@Override
	public void onEnable() {
		NovaUniverseBungeecord.instance = this;

		networkUpdateTask = null;
		playerHeartbeatTask = null;
		cleanupTask = null;

		saveDefaultConfiguration();

		Configuration config = getConfig();

		DBCredentials dbCredentials = new DBCredentials(config.getString("mysql.driver"), config.getString("mysql.host"), config.getString("mysql.username"), config.getString("mysql.password"), config.getString("mysql.database"));
		DBConnection dbc = new DBConnection();
		try {
			dbc.connect(dbCredentials);
		} catch (Exception e) {
			e.printStackTrace();
			Log.fatal("Failed to connect to the database");
			return;
		}

		originalServers = new ArrayList<String>();

		for (String server : getProxy().getServers().keySet()) {
			originalServers.add(server);
		}

		NovaUniverseCommons.setDbConnection(dbc);
		NovaUniverseCommons.setServerFinder(new ServerFinder());

		networkManager = new NovaNetworkManager();
		NovaUniverseCommons.setNetworkManager(networkManager);
		try {
			networkManager.update(true);
		} catch (SQLException e1) {
			e1.printStackTrace();
			Log.fatal("Failed to fetch servers from database");
			return;
		}

		defaultServerName = config.getString("default_server_name");

		lobbyType = networkManager.getServerTypeByName(config.getString("lobby_server_type"));
		if (lobbyType == null) {
			Log.fatal("Could not find lobby server type: " + config.getString("lobby_server_type"));
			return;
		}
		Log.info("Using " + lobbyType.getDisplayName() + "(" + lobbyType.getName() + ") as lobby server type");

		networkUpdateTask = new AdvancedTask(this, new Runnable() {
			@Override
			public void run() {
				try {
					networkManager.update(false);
					updateServerList();
				} catch (SQLException e) {
					e.printStackTrace();
					Log.error("Failed to update server list");
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
		networkUpdateTask.start();

		playerHeartbeatTask = new AdvancedTask(new Runnable() {
			@Override
			public void run() {
				for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
					try {
						String sql = "UPDATE players SET is_online = 1, heartbeat_timestamp = CURRENT_TIMESTAMP WHERE uuid = ?";
						PreparedStatement ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);
						ps.setString(1, player.getUniqueId().toString());
						ps.executeUpdate();
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}, 1L, TimeUnit.SECONDS);
		playerHeartbeatTask.start();

		cleanupTask = new AdvancedTask(new Runnable() {
			@Override
			public void run() {
				try {
					String sql = "{CALL cleanup()}";
					CallableStatement cs = NovaUniverseCommons.getDbConnection().getConnection().prepareCall(sql);
					cs.execute();
					cs.close();
				} catch (Exception ex) {
					ex.printStackTrace();
					Log.error("Failed to execute procedure cleanup");
				}
			}
		}, 10, TimeUnit.SECONDS);
		cleanupTask.start();

		getProxy().getPluginManager().registerListener(this, this);
		getProxy().getPluginManager().registerListener(this, new ChatLogger());
		getProxy().getPluginManager().registerListener(this, new PluginMessageListener());

		getProxy().registerChannel("NovaUniverse:data");

		ProxyServer.getInstance().getPluginManager().registerCommand(this, new SpectateCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new JoinCommand());
	}

	@Override
	public void onDisable() {
		Task.tryStopTask(playerHeartbeatTask);
		Task.tryStopTask(networkUpdateTask);
		Task.tryStopTask(cleanupTask);

		try {
			if (NovaUniverseCommons.getDbConnection() != null) {
				if (NovaUniverseCommons.getDbConnection().isConnected()) {
					NovaUniverseCommons.getDbConnection().close();
					NovaUniverseCommons.setDbConnection(null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		getProxy().getScheduler().cancel(this);
		getProxy().getPluginManager().unregisterListeners((Plugin) this);
		getProxy().getPluginManager().unregisterCommands((Plugin) this);
	}

	private void updateServerList() {
		List<String> old = new ArrayList<String>();
		for (String name : ProxyServer.getInstance().getServers().keySet()) {
			if (originalServers.contains(name)) {
				continue;
			}
			old.add(name);
		}

		for (NovaServer server : networkManager.getServers()) {
			// Log.trace("Checking server " + server.getName());
			old.remove(server.getName());

			if (ProxyServer.getInstance().getServers().containsKey(server.getName())) {
				continue;
			}

			InetSocketAddress address = new InetSocketAddress(server.getHost(), server.getPort());

			ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(server.getName(), address, "NovaUniverse server", false);

			ProxyServer.getInstance().getServers().put(server.getName(), serverInfo);
			Log.trace("NovaBungeecord", "Added server " + server.getName());
		}

		for (String name : old) {
			ProxyServer.getInstance().getServers().remove(name);
			Log.trace("NovaBungeecord", "Removed server " + name);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(ServerConnectEvent e) {
		if (e.isCancelled()) {
			return;
		}

		if (e.getTarget().getName().equalsIgnoreCase(defaultServerName)) {
			try {
				NovaServer targetServer = networkManager.findServer(lobbyType);

				if (targetServer == null) {
					e.getPlayer().disconnect(new TextComponent(ChatColor.DARK_RED + "Could not find a lobby for you"));
					Log.warn("Failed to fetch target server for player: " + e.getPlayer().getDisplayName());
					return;
				}

				e.setTarget(ProxyServer.getInstance().getServerInfo(targetServer.getName()));
			} catch (Exception ex) {
				ex.printStackTrace();
				Log.error("Failed to fetch target server for player " + e.getPlayer().getDisplayName());
				e.getPlayer().disconnect(new TextComponent(ChatColor.DARK_RED + "Could not find a lobby for you\nCaused by: " + ex.getClass().getName()));
			}
		}
	}

	@EventHandler
	public void onPostLogin(PostLoginEvent e) {
		e.getPlayer().sendMessage(new TextComponent(ChatColor.GOLD + "NovaUniverse is still in early alpha!\n " + ChatColor.GOLD + "If you find any bugs please report them on our discord server"));
		try {
			String sql = "{CALL player_join_data(?, ?, ?)}";
			CallableStatement cs = NovaUniverseCommons.getDbConnection().getConnection().prepareCall(sql);

			cs.setString(1, e.getPlayer().getUniqueId().toString());
			cs.setString(2, e.getPlayer().getName());
			cs.setString(3, e.getPlayer().getSocketAddress().toString());

			cs.execute();

			cs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.error("Failed to execute procedure player_join_data");
			e.getPlayer().disconnect(new TextComponent(ChatColor.DARK_RED + ex.getClass().getName()));
		}
	}

	@EventHandler
	public void onServerConnected(ServerConnectedEvent e) {
		NovaServer server = networkManager.getServerByName(e.getServer().getInfo().getName());

		if (server != null) {
			try {
				String sql = "UPDATE players SET server_id = ? WHERE uuid = ?";
				PreparedStatement ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);

				ps.setInt(1, server.getId());
				ps.setString(2, e.getPlayer().getUniqueId().toString());

				ps.executeUpdate();
			} catch (Exception ex) {
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDisconnect(PlayerDisconnectEvent e) {
		try {
			String sql = "UPDATE players SET is_online = 0, server_id = null WHERE uuid = ?";
			PreparedStatement ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);
			ps.setString(1, e.getPlayer().getUniqueId().toString());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}