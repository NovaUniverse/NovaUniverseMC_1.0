package net.novauniverse.bungeecord;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.novauniverse.commons.NovaUniverseCommons;
import net.novauniverse.commons.network.NovaNetworkManager;
import net.novauniverse.commons.network.server.NovaServer;
import net.zeeraa.novacore.bungeecord.novaplugin.NovaPlugin;
import net.zeeraa.novacore.bungeecord.task.AdvancedTask;
import net.zeeraa.novacore.commons.database.DBConnection;
import net.zeeraa.novacore.commons.database.DBCredentials;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;

public class NovaUniverseBungeecord extends NovaPlugin {
	private static NovaUniverseBungeecord instance;

	private List<String> originalServers;

	private Task networkUpdateTask;
	private NovaNetworkManager networkManager;

	public static NovaUniverseBungeecord getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		NovaUniverseBungeecord.instance = this;

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

		networkManager = new NovaNetworkManager();

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
			//Log.trace("Checking server " + server.getName());
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

	@Override
	public void onDisable() {
		Task.tryStopTask(networkUpdateTask);

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
}