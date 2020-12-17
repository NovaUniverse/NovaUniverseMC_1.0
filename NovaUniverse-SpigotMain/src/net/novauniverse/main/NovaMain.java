package net.novauniverse.main;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONException;
import org.json.JSONObject;

import net.novauniverse.commons.NovaUniverseCommons;
import net.novauniverse.commons.network.NovaNetworkManager;
import net.novauniverse.commons.network.server.NovaServerType;
import net.novauniverse.main.gamestarter.DefaultCountdownGameStarter;
import net.novauniverse.main.gamestarter.GameStarter;
import net.novauniverse.main.modules.GameEndManager;
import net.novauniverse.main.modules.NoEnderPearlDamage;
import net.novauniverse.main.modules.WinMessage;
import net.novauniverse.main.team.skywars.solo.SkywarsSoloTeamManager;
import net.novauniverse.main.trackers.ClosestPlayerTracker;
import net.zeeraa.novacore.commons.database.DBConnection;
import net.zeeraa.novacore.commons.database.DBCredentials;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.language.LanguageReader;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.compass.CompassTracker;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameLoadedEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameStartEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameStartFailureEvent;
import net.zeeraa.novacore.spigot.module.modules.gamelobby.GameLobby;
import net.zeeraa.novacore.spigot.novaplugin.NovaPlugin;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class NovaMain extends NovaPlugin implements Listener {
	private static NovaMain instance;

	private NovaNetworkManager networkManager;

	private NovaServerType serverType;
	private NovaServerType fallbackLobbyServerType;
	private String serverName;
	private String serverHost;

	private List<GameStarter> gameStarters;
	private GameStarter gameStarter;

	private int serverId;

	private Task heartbeatTask;

	private boolean inErrorState;

	public static NovaMain getInstance() {
		return instance;
	}

	public NovaNetworkManager getNetworkManager() {
		return networkManager;
	}

	public NovaServerType getFallbackLobbyServerType() {
		return fallbackLobbyServerType;
	}

	public NovaServerType getServerType() {
		return serverType;
	}

	public boolean isInErrorState() {
		return inErrorState;
	}

	public GameStarter getGameStarter() {
		return gameStarter;
	}

	@Override
	public void onEnable() {
		/* Set initial variables */
		NovaMain.instance = this;

		inErrorState = false;

		heartbeatTask = null;
		serverId = -1;

		gameStarters = new ArrayList<GameStarter>();
		gameStarter = null;

		/* Create config.yml */
		saveDefaultConfig();

		/* Load game starters */
		gameStarters.add(new DefaultCountdownGameStarter());

		/* Language files */
		Log.info("NovaMain", "Loading language files...");
		try {
			LanguageReader.readFromJar(this.getClass(), "/lang/en-us.json");
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("NovaMain", "Failed to read lanaguge files");
			inErrorState = true;
		}

		/* Check configuration file */
		File configFile = new File("novaconfig.json").getAbsoluteFile();

		if (!configFile.exists()) {
			Log.fatal("NovaMain", "Config file at " + configFile.getPath() + " does not exist. Closing server!");
			Bukkit.getServer().shutdown();
			return;
		}

		JSONObject config;

		try {
			config = JSONFileUtils.readJSONObjectFromFile(configFile);
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			Log.fatal("NovaMain", "Could not read config file at " + configFile.getPath() + ". Closing server!");
			Bukkit.getServer().shutdown();
			return;
		}

		/* Load game lobby maps */
		File gameLobbyFolder = new File(this.getDataFolder().getPath() + File.separator + "GameLobby");
		File worldFolder = new File(this.getDataFolder().getPath() + File.separator + "Worlds");

		try {
			FileUtils.forceMkdir(getDataFolder());
			FileUtils.forceMkdir(gameLobbyFolder);
			FileUtils.forceMkdir(worldFolder);

			Log.info("NovaMain", "Reading lobby files from " + gameLobbyFolder.getPath());
			GameLobby.getInstance().getMapReader().loadAll(gameLobbyFolder, worldFolder);
		} catch (IOException e1) {
			e1.printStackTrace();
			Log.fatal("NovaMain", "Failed to setup data directory");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		/* Check configuration value : mysql */

		JSONObject mysql = config.getJSONObject("mysql");

		DBCredentials dbCredentials = new DBCredentials(mysql.getString("driver"), mysql.getString("host"), mysql.getString("username"), mysql.getString("password"), mysql.getString("database"));

		DBConnection dbc = new DBConnection();
		try {
			dbc.connect(dbCredentials);
		} catch (Exception e) {
			e.printStackTrace();
			Log.fatal("NovaMain", "Failed to connect to the database. Closing server!");
			Bukkit.getServer().shutdown();
			return;
		}

		Log.info("NovaMain", "DBConnection started");
		NovaUniverseCommons.setDbConnection(dbc);

		this.networkManager = new NovaNetworkManager();

		Log.info("NovaMain", "Updating server types in NetworkManager");
		try {
			networkManager.updateTypes(false);
		} catch (SQLException e) {
			e.printStackTrace();
			Log.fatal("NovaMain", "Failed to fetch server types. Closing server!");
			Bukkit.getServer().shutdown();
			return;
		}

		/* Check configuration value: use_teams */
		if (config.has("use_teams")) {
			if (config.getBoolean("use_teams")) {
				Log.info("NovaMain", "Using teams");
				GameManager.getInstance().setUseTeams(true);
			}
		}

		/* Check configuration value : host */
		serverHost = config.getString("host");
		if (serverHost == null) {
			Log.fatal("NovaMain", "No host specified in novaconfig.json. Closing server!");
			Bukkit.getServer().shutdown();
			return;
		}

		/* Check configuration value : server_type */
		serverType = networkManager.getServerTypeByName(config.getString("server_type"));
		if (serverType == null) {
			Log.fatal("NovaMain", "Missing or invalid server type configured in novaconfig.json. Closing server!");
			Bukkit.getServer().shutdown();
			return;
		}

		/* Check configuration value : global_lobby_fallback */
		if (!config.has("global_lobby_fallback")) {
			Log.fatal("NovaMain", "Missing global_lobby_fallback in novaconfig.json. Closing server!");
			Bukkit.getServer().shutdown();
			return;
		}

		fallbackLobbyServerType = networkManager.getServerTypeByName(config.getString("global_lobby_fallback"));

		if (fallbackLobbyServerType == null) {
			Log.fatal("NovaMain", "Invalid global_lobby_fallback in novaconfig.json. Closing server!");
			Bukkit.getServer().shutdown();
			return;
		}

		/* Check configuration value: compass_tracker_mode */
		if (config.has("compass_tracker_mode")) {
			switch (config.getString("compass_tracker_mode").toLowerCase()) {
			case "closest_player":
				Log.info("NovaMain", "Using compass tracker: " + ClosestPlayerTracker.class.getName());
				CompassTracker.getInstance().setCompassTrackerTarget(new ClosestPlayerTracker());
				break;

			default:
				Log.fatal("NovaMain", "Invalid compass_tracker_mode in novaconfig.json. Closing server!");
				Bukkit.getServer().shutdown();
				return;
			}
		}

		/* Check configuration value for: compass_tracker_strict_mode */
		boolean strictMode = true;
		if (config.has("compass_tracker_strict_mode")) {
			if (!config.getBoolean("compass_tracker_strict_mode")) {
				strictMode = false;
			}
		}

		/* Set strict mode */
		Log.info("NovaMain", strictMode ? "Using strict mode for compass tracker" : "Strict mode disabled for compass trackers");
		CompassTracker.getInstance().setStrictMode(strictMode);

		/* Check configuration value : team_manager */
		if (config.has("team_manager")) {
			String teamManagerName = config.getString("team_manager");
			TeamManager teamManager = null;
			switch (teamManagerName.toLowerCase()) {
			case "skywars_solo":
				teamManager = new SkywarsSoloTeamManager();
				break;

			default:
				Log.error("NovaMain", "Invalid team manager: " + teamManager);
				break;
			}

			if (teamManager != null) {
				if (teamManager instanceof Listener) {
					Log.info("NovaMain", "Register listener for team manager");
					Bukkit.getServer().getPluginManager().registerEvents((Listener) teamManager, this);
				}

				Log.info("NovaMain", "Using team manager: " + teamManager.getClass().getName());
				NovaCore.getInstance().setTeamManager(teamManager);
			} else {
				Log.info("NovaMain", "No team manager defined");
			}
		} else {
			Log.info("NovaMain", "No team manager defined");
		}

		/* Check configuration value : no_pearl_damage */
		boolean noPearlDamage = false;

		if (config.has("no_pearl_damage")) {
			if (config.getBoolean("no_pearl_damage")) {
				noPearlDamage = true;

				Log.info("NovaMain", "Ender pearl damage disabled");
			}
		}

		/* Load modules */
		ModuleManager.loadModule(NoEnderPearlDamage.class, noPearlDamage);
		ModuleManager.loadModule(GameEndManager.class, true);
		ModuleManager.loadModule(WinMessage.class, true);

		/* Listeners */
		Bukkit.getServer().getPluginManager().registerEvents(this, this);

		/* Check configuration value : name_override and generate name */
		if (config.has("name_override")) {
			String name = config.getString("name_override");
			Log.warn("NovaMain", "Overriding naming scheme with: " + name);

			try {
				networkManager.updateServers(false);
			} catch (SQLException e) {
				e.printStackTrace();
				Log.fatal("NovaMain", "Failed to fetch servers. Closing server!");
				Bukkit.getServer().shutdown();
				return;
			}

			if (networkManager.getServerByName(name) != null) {
				Log.fatal("NovaMain", "Cant override name since the name is already in use. Closing server!");
				Bukkit.getServer().shutdown();
				return;
			}

			serverName = name;
		} else {
			try {
				serverName = NovaNetworkManager.generateServerName(serverType);
				if (serverName == null) {
					Log.fatal("NovaMain", "Failed to generate server name. Closing server!");
					Bukkit.getServer().shutdown();
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.fatal("NovaMain", "An error occurred while generating the server name. Closing server!");
				Bukkit.getServer().shutdown();
				return;
			}
		}
		Log.info("NovaMain", "Using server name: " + serverName);

		/* Register server */
		try {
			serverId = NovaNetworkManager.registerServer(serverName, serverHost, Bukkit.getServer().getPort(), serverType);

			if (serverId == -1) {
				Log.fatal("NovaMain", "Server id returned -1. Closing server!");
				Bukkit.getServer().shutdown();
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Log.fatal("NovaMain", "Failed to register server in database. Closing server!");
			Bukkit.getServer().shutdown();
			return;
		}

		Log.info("NovaMain", "Server registered! Our server id is: " + serverId);

		/* Register heart beat task */
		heartbeatTask = new SimpleTask(this, new Runnable() {
			public void run() {
				try {
					if (!NovaNetworkManager.updateHeartbeat(serverId)) {
						Log.warn("NovaMain", "Warning heartbeat returned a row count of 0");
					}
				} catch (SQLException e) {
					Log.error("NovaMain", "Failed to update heartbeat");
					e.printStackTrace();
				}
			}
		}, 40L, 40L);
		heartbeatTask.start();
	}

	@Override
	public void onDisable() {
		Task.tryStopTask(heartbeatTask);

		if (serverId > 0) {
			try {
				Log.info("NovaMain", "Unregistering server from database");
				NovaNetworkManager.unregisterServer(serverId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll((Plugin) this);

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
	}

	public void sendToLobby(Player player, boolean useFallback) {
		if (serverType.getReturnToServerType() != null && !useFallback) {
			NovaNetworkManager.sendPlayerToServer(player.getUniqueId(), serverType.getReturnToServerType());
		} else {
			NovaNetworkManager.sendPlayerToServer(player.getUniqueId(), fallbackLobbyServerType);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameLoad(GameLoadedEvent e) {
		if (gameStarter == null) {
			Log.warn("NovaMain", "No game starter defined. The game will not auto start");
			return;
		}

		Log.info("NovaMain", "Register events for game starter: " + gameStarter.getClass().getName());
		gameStarter.onEnable();
		Bukkit.getServer().getPluginManager().registerEvents(gameStarter, this);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameStart(GameStartEvent e) {
		try {
			NovaNetworkManager.flagAsGameStarted(serverId);
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.error("NovaMain", "Failed flag this server as minigame_started");
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameStartFailure(GameStartFailureEvent e) {
		inErrorState = true;

		try {
			NovaNetworkManager.flagServerAsFailed(serverId);
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.error("NovaMain", "Failed flag this server as has_failed");
		}

		LanguageManager.broadcast("novauniverse.game_load_error");

		Log.fatal("NovaMain", "Failed to start game. Sending player to the lobby in 10 seconds");

		new BukkitRunnable() {
			@Override
			public void run() {
				if (!ModuleManager.isEnabled(GameEndManager.class)) {
					ModuleManager.enable(GameEndManager.class);
				}
				GameEndManager gameEndManager = (GameEndManager) ModuleManager.getModule(GameEndManager.class);

				gameEndManager.attemptSend();
			}
		}.runTaskLater(this, 200L);
	}
}