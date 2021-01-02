package net.novauniverse.main;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONException;
import org.json.JSONObject;

import com.connorlinfoot.titleapi.TitleAPI;

import net.novauniverse.commons.NovaUniverseCommons;
import net.novauniverse.commons.network.NovaNetworkManager;
import net.novauniverse.commons.network.server.NovaServerType;
import net.novauniverse.main.commands.Base64DumpItemCommand;
import net.novauniverse.main.commands.JoinServerGroupCommand;
import net.novauniverse.main.commands.ReloadNetworkManagerCommand;
import net.novauniverse.main.commands.ShowServersCommand;
import net.novauniverse.main.gamespecific.MissileWarsHandler;
import net.novauniverse.main.gamespecific.UHCHandler;
import net.novauniverse.main.gamestarter.DefaultCountdownGameStarter;
import net.novauniverse.main.gamestarter.GameStarter;
import net.novauniverse.main.labymod.NovaLabymodAPI;
import net.novauniverse.main.modules.GameEndManager;
import net.novauniverse.main.modules.GameStartScoreboardCountdown;
import net.novauniverse.main.modules.NoEnderPearlDamage;
import net.novauniverse.main.modules.TabList;
import net.novauniverse.main.modules.WinMessage;
import net.novauniverse.main.servericons.ServerIconIndex;
import net.novauniverse.main.team.skywars.solo.SkywarsSoloTeamManager;
import net.novauniverse.main.team.solo.SoloTeamManager;
import net.novauniverse.main.trackers.ClosestPlayerTracker;
import net.zeeraa.novacore.commons.NovaCommons;
import net.zeeraa.novacore.commons.database.DBConnection;
import net.zeeraa.novacore.commons.database.DBCredentials;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.JSONFileType;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.language.LanguageReader;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.compass.CompassTracker;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameEndEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameLoadedEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameStartEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameStartFailureEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.PlayerEliminatedEvent;
import net.zeeraa.novacore.spigot.module.modules.gamelobby.GameLobby;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
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
	private Task updateServersTask;

	private boolean useTeams;

	private boolean inErrorState;

	private boolean disableScoreboard;

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

	public void setInErrorState(boolean inErrorState) {
		this.inErrorState = inErrorState;
	}

	public boolean isInErrorState() {
		return inErrorState;
	}

	public GameStarter getGameStarter() {
		return gameStarter;
	}

	public boolean isDisableScoreboard() {
		return disableScoreboard;
	}

	public boolean isUseTeams() {
		return useTeams;
	}

	@Override
	public void onEnable() {
		/* Set initial variables */
		NovaMain.instance = this;

		inErrorState = false;

		heartbeatTask = null;
		serverId = -1;

		useTeams = false;

		gameStarters = new ArrayList<GameStarter>();
		gameStarter = null;

		disableScoreboard = false;

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
		File configFile = new File("novaserver.json").getAbsoluteFile();

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

		/* Check server icons file */
		File iconsFile = new File(getDataFolder().getPath() + File.separator + "icons.json");

		try {
			if (!iconsFile.exists()) {
				Log.info("NovaMain", "Creating icons.json");
				JSONFileUtils.createEmpty(iconsFile, JSONFileType.JSONObject);
			}

			JSONObject icons = JSONFileUtils.readJSONObjectFromFile(iconsFile);

			ServerIconIndex.load(icons);
		} catch (Exception e) {
			Log.error("NovaMain", "Failed to read / create icons.json. " + e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
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

		/* Check configuration value: mysql */

		JSONObject mysql = null;

		/* Check novaconfig.json */
		boolean useGlobal = true;

		if (config.has("use_global_settings")) {
			if (!config.getBoolean("use_global_settings")) {
				useGlobal = true;
			}
		}

		if (useGlobal) {
			File globalConfigFile = new File(System.getProperty("user.home") + File.separator + "novaconfig.json");

			if (globalConfigFile.exists()) {
				Log.info("NovaMain", "Reading global config file");
				try {
					JSONObject global = JSONFileUtils.readJSONObjectFromFile(globalConfigFile);

					if (global.has("novamain")) {
						JSONObject novaMain = global.getJSONObject("novamain");
						if (novaMain.has("mysql")) {
							Log.info("NovaMain", "Using MySQL connection from novaconfig.json");
							mysql = novaMain.getJSONObject("mysql");
						} else {
							Log.error("NovaMain", "novaconfig.json does not contain field: novamain");
						}
					} else {
						Log.error("NovaMain", "novaconfig.json does not contain field: novamain");
					}
				} catch (Exception e) {
					Log.error("NovaMain", "Failed to read novaconfig.json file " + globalConfigFile.getPath());
					e.printStackTrace();
				}
			}
		}

		if (mysql == null) {
			mysql = config.getJSONObject("mysql");
		}

		/* Connect to the MySQL database */

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

		/* Check configuration value: disable_scoreboard */
		if (config.has("disable_scoreboard")) {
			disableScoreboard = config.getBoolean("disable_scoreboard");
		}

		/* Check configuration value: use_teams */
		if (config.has("use_teams")) {
			if (config.getBoolean("use_teams")) {
				useTeams = true;
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

		/* Check configuration value: server_type */
		serverType = networkManager.getServerTypeByName(config.getString("server_type"));
		if (serverType == null) {
			Log.fatal("NovaMain", "Missing or invalid server type configured in novaconfig.json. Closing server!");
			Bukkit.getServer().shutdown();
			return;
		}

		/* Check configuration value: global_lobby_fallback */
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

		/* Check configuration value: team_manager */
		if (config.has("team_manager")) {
			String teamManagerName = config.getString("team_manager");
			TeamManager teamManager = null;
			switch (teamManagerName.toLowerCase()) {
			case "skywars_solo":
				int skywarsSoloTeamCount = 12;

				if (config.has("skywars_solo_team_count")) {
					skywarsSoloTeamCount = config.getInt("skywars_solo_team_count");
				}

				Log.info("NovaMain", "Using team count of: " + skywarsSoloTeamCount + " for skywars solo teams");
				teamManager = new SkywarsSoloTeamManager(skywarsSoloTeamCount);
				break;

			case "solo":
				int soloTeamCount = getServerType().getSoftPlayerLimit();

				if (config.has("solo_team_count")) {
					skywarsSoloTeamCount = config.getInt("solo_team_count");
				}

				Log.info("NovaMain", "Using team count of: " + soloTeamCount + " for solo teams");
				teamManager = new SoloTeamManager(soloTeamCount);
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

		/* Check configuration value: no_pearl_damage */
		boolean noPearlDamage = false;

		if (config.has("no_pearl_damage")) {
			if (config.getBoolean("no_pearl_damage")) {
				noPearlDamage = true;

				Log.info("NovaMain", "Ender pearl damage disabled");
			}
		}

		/* Check configuration value: game_starter */
		if (config.has("game_starter")) {
			String starterName = config.getString("game_starter");
			for (GameStarter starter : gameStarters) {
				if (starter.getName().equalsIgnoreCase(starterName)) {
					gameStarter = starter;
					Log.info("NovaMain", "Game starter: " + gameStarter);

					break;
				}
			}

			if (gameStarter == null) {
				Log.fatal("NovaMain", "Could not find game starter " + starterName + " (Case insensitive). Closing server!");
				Bukkit.getServer().shutdown();
				return;
			}
		}

		/* Load modules */
		ModuleManager.loadModule(NoEnderPearlDamage.class, noPearlDamage);
		ModuleManager.loadModule(GameEndManager.class, true);
		ModuleManager.loadModule(WinMessage.class, true);
		ModuleManager.loadModule(TabList.class, true);
		ModuleManager.loadModule(GameStartScoreboardCountdown.class);

		/* Game specific */
		ModuleManager.loadModule(MissileWarsHandler.class);
		ModuleManager.loadModule(UHCHandler.class);

		/* Listeners */
		Bukkit.getServer().getPluginManager().registerEvents(this, this);

		/* Check configuration value: name_override and generate name */
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

		updateServersTask = new SimpleTask(this, new Runnable() {
			public void run() {
				NovaCommons.getAbstractAsyncManager().runAsync(new Runnable() {
					@Override
					public void run() {
						try {
							networkManager.update(false);
						} catch (SQLException e) {
							Log.error("NovaMain", "Failed to update server list");
							e.printStackTrace();
						}
					}
				}, 1L);
			}
		}, 40L, 40L);
		updateServersTask.start();

		if (!disableScoreboard) {
			requireModule(NetherBoardScoreboard.class);
		}

		NetherBoardScoreboard.getInstance().setDefaultTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "NovaUniverse");
		NetherBoardScoreboard.getInstance().setGlobalLine(14, ChatColor.YELLOW + "https://novauniverse.net");

		CommandRegistry.registerCommand(new JoinServerGroupCommand());
		CommandRegistry.registerCommand(new ReloadNetworkManagerCommand());
		CommandRegistry.registerCommand(new ShowServersCommand());
		CommandRegistry.registerCommand(new Base64DumpItemCommand());

		if (gameStarter != null) {
			ModuleManager.enable(GameStartScoreboardCountdown.class);
		}
	}

	@Override
	public void onDisable() {
		Task.tryStopTask(heartbeatTask);
		Task.tryStopTask(updateServersTask);

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
		try {
			if (serverType.getReturnToServerType() != null && !useFallback) {
				networkManager.sendPlayerToServer(player.getUniqueId(), serverType.getReturnToServerType());
			} else {
				networkManager.sendPlayerToServer(player.getUniqueId(), fallbackLobbyServerType);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("Failed to fetch target lobby server for player: " + player.getName());
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameLoad(GameLoadedEvent e) {
		if (!disableScoreboard) {
			NetherBoardScoreboard.getInstance().setGlobalLine(0, ChatColor.YELLOW + "" + ChatColor.BOLD + e.getGame().getDisplayName());
		}

		if (gameStarter == null) {
			Log.warn("NovaMain", "No game starter defined. The game will not auto start");
		} else {
			Log.info("NovaMain", "Register events for game starter: " + gameStarter.getClass().getName());
			gameStarter.onEnable();
			Bukkit.getServer().getPluginManager().registerEvents(gameStarter, this);
		}

		switch (e.getGame().getName().toLowerCase()) {
		case "missilewars":
			ModuleManager.enable(MissileWarsHandler.class);
			break;

		case "uch":
			ModuleManager.enable(UHCHandler.class);
			break;

		default:
			break;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameStart(GameStartEvent e) {
		try {
			NovaNetworkManager.flagAsGameStarted(serverId);
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				NovaLabymodAPI.sendCurrentPlayingGamemode(player, true, e.getGame().getDisplayName());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.error("NovaMain", "Failed flag this server as minigame_started");
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (GameManager.getInstance().isEnabled()) {
			if (GameManager.getInstance().hasGame()) {
				NovaLabymodAPI.sendCurrentPlayingGamemode(e.getPlayer(), true, GameManager.getInstance().getActiveGame().getDisplayName());
			}
		}
		// NovaLabymodAPI.sendWatermark(e.getPlayer(), true);

		NovaLabymodAPI.updateGameInfo(e.getPlayer(), true, getServerType().getDisplayName(), 0, 0);
	}

	@EventHandler
	public void onPlayerEliminated(PlayerEliminatedEvent e) {
		if (e.getPlayer().isOnline()) {
			Player player = e.getPlayer().getPlayer();

			new BukkitRunnable() {
				@Override
				public void run() {
					String sub = "";

					if (e.getKiller() != null) {
						sub = ChatColor.RED + "Killed by " + ChatColor.AQUA + e.getKiller().getName();
					}

					player.playSound(player.getLocation(), Sound.WITHER_HURT, 1F, 1);
					TitleAPI.sendTitle(player, 5, 60, 10, ChatColor.RED + "Eliminated", sub);
					NovaLabymodAPI.sendCineScope(player, 10, 20);

					new BukkitRunnable() {

						@Override
						public void run() {
							NovaLabymodAPI.sendCineScope(player, 0, 20);
						}
					}.runTaskLater(NovaMain.getInstance(), 60);
				}
			}.runTaskLater(this, 10L);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (GameManager.getInstance().isEnabled()) {
			if (GameManager.getInstance().hasGame()) {
				NovaLabymodAPI.sendCurrentPlayingGamemode(e.getPlayer(), false, GameManager.getInstance().getActiveGame().getDisplayName());
				NovaLabymodAPI.updateGameInfo(e.getPlayer(), false, "", 0, 0);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameEnd(GameEndEvent e) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			NovaLabymodAPI.sendCurrentPlayingGamemode(player, false, e.getGame().getDisplayName());
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