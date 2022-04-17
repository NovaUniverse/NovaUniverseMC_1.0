package net.novauniverse.main;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.json.JSONException;
import org.json.JSONObject;

import net.novauniverse.commons.NovaUniverseCommons;
import net.novauniverse.commons.network.NovaNetworkManager;
import net.novauniverse.commons.network.server.NovaServerType;
import net.novauniverse.main.commands.Base64DumpItemCommand;
import net.novauniverse.main.commands.DiscordCommand;
import net.novauniverse.main.commands.DumpNetworkState;
import net.novauniverse.main.commands.JoinServerGroupCommand;
import net.novauniverse.main.commands.ReconnectCommand;
import net.novauniverse.main.commands.ReloadNetworkManagerCommand;
import net.novauniverse.main.commands.ShowServersCommand;
import net.novauniverse.main.commands.SpectateGameCommand;
import net.novauniverse.main.commands.WhatIsDogeWorthCommand;
import net.novauniverse.main.commands.WhereAmICommand;
import net.novauniverse.main.commands.account.AccountCommand;
import net.novauniverse.main.gamespecific.DeathSwapHandler;
import net.novauniverse.main.gamespecific.ManhuntHandler;
import net.novauniverse.main.gamespecific.MissileWarsHandler;
import net.novauniverse.main.gamespecific.UHCHandler;
import net.novauniverse.main.gamespecific.UHCv2Handler;
import net.novauniverse.main.gamestarter.DefaultCountdownGameStarter;
import net.novauniverse.main.gamestarter.GameStarter;
import net.novauniverse.main.labymod.NovaLabymodAPI;
import net.novauniverse.main.listener.GameEventListener;
import net.novauniverse.main.modules.GameEndManager;
import net.novauniverse.main.modules.GameStartScoreboardCountdown;
import net.novauniverse.main.modules.NoEnderPearlDamage;
import net.novauniverse.main.modules.NovaGameTimeLimit;
import net.novauniverse.main.modules.NovaScoreboard;
import net.novauniverse.main.modules.NovaSetReconnectServer;
import net.novauniverse.main.modules.PreventingAleksaFromBreakingTheServer;
import net.novauniverse.main.modules.TabList;
import net.novauniverse.main.modules.WinMessage;
import net.novauniverse.main.modules.head.EdibleHeads;
import net.novauniverse.main.modules.head.GoldenHead;
import net.novauniverse.main.modules.head.PlayerHeadDrop;
import net.novauniverse.main.modules.shutdownrequest.CheckShutdownRequest;
import net.novauniverse.main.modules.utils.GameInterface;
import net.novauniverse.main.modules.utils.NovaGameEngineInterface;
import net.novauniverse.main.pluginmessagelistener.NovaPluginMessageListener;
import net.novauniverse.main.serverfinder.ServerFinder;
import net.novauniverse.main.servericons.ServerIconIndex;
import net.novauniverse.main.team.skywars.solo.SkywarsSoloTeamManager;
import net.novauniverse.main.team.solo.SoloTeamManager;
import net.novauniverse.main.trackers.ClosestPlayerTracker;
import net.zeeraa.novacore.commons.NovaCommons;
import net.zeeraa.novacore.commons.async.AsyncManager;
import net.zeeraa.novacore.commons.database.DBConnection;
import net.zeeraa.novacore.commons.database.DBCredentials;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.DiscordWebhook;
import net.zeeraa.novacore.commons.utils.JSONFileType;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.gamelobby.GameLobby;
import net.zeeraa.novacore.spigot.language.LanguageReader;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.compass.CompassTracker;
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
	private int serverId;

	private boolean safeMode;

	private boolean hasLabyMod;

	private List<GameStarter> gameStarters;
	private GameStarter gameStarter;

	private Task heartbeatTask;
	private Task updateServersTask;

	private boolean useTeams;

	private boolean inErrorState;

	private boolean disableScoreboard;

	private String discordWebhookUrl;

	private GameInterface gameInterface;

	public static NovaMain getInstance() {
		return instance;
	}

	public GameInterface getGameInterface() {
		return gameInterface;
	}

	public boolean hasGameInterface() {
		return gameInterface != null;
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

	public void setGameStarter(GameStarter gameStarter) {
		this.gameStarter = gameStarter;
	}

	public boolean isDisableScoreboard() {
		return disableScoreboard;
	}

	public boolean isUseTeams() {
		return useTeams;
	}

	public String getServerHost() {
		return serverHost;
	}

	public int getServerId() {
		return serverId;
	}

	public String getServerName() {
		return serverName;
	}

	public boolean hasLabyMod() {
		return hasLabyMod;
	}

	public boolean isSafeMode() {
		return safeMode;
	}

	public void enableSpectateGameCommand() {
		if (!CommandRegistry.isRegistered(SpectateGameCommand.class)) {
			Log.info("Enabling SpectateGameCommand");
			CommandRegistry.registerCommand(new SpectateGameCommand());
		}
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

		safeMode = false;

		hasLabyMod = Bukkit.getServer().getPluginManager().getPlugin("LabyModAPI") != null;

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

			if (NovaCore.isNovaGameEngineEnabled()) {
				Log.info("NovaMain", "Reading lobby files from " + gameLobbyFolder.getPath());
				GameLobby.getInstance().getMapReader().loadAll(gameLobbyFolder, worldFolder);
			}
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

		discordWebhookUrl = null;
		if (config.has("discord_webhook")) {
			discordWebhookUrl = config.getString("discord_webhook");
		}

		if (useGlobal) {
			File globalConfigFile = new File(System.getProperty("user.home") + File.separator + "novaconfig.json");

			if (globalConfigFile.exists()) {
				Log.info("NovaMain", "Reading global config file");
				try {
					JSONObject global = JSONFileUtils.readJSONObjectFromFile(globalConfigFile);

					if (global.has("novamain")) {
						JSONObject novaMain = global.getJSONObject("novamain");

						if (novaMain.has("discord_webhook")) {
							discordWebhookUrl = novaMain.getString("discord_webhook");
						}

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
		NovaUniverseCommons.setServerFinder(new ServerFinder());

		this.networkManager = new NovaNetworkManager();
		NovaUniverseCommons.setNetworkManager(networkManager);

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
		if (NovaCore.isNovaGameEngineEnabled()) {
			gameInterface = new NovaGameEngineInterface();
			if (config.has("use_teams")) {
				if (config.getBoolean("use_teams")) {
					useTeams = true;
					Log.info("NovaMain", "Using teams");
					GameManager.getInstance().setUseTeams(true);
				}
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

		/* Check configuration value: safe_mode */
		if (config.has("safe_mode")) {
			safeMode = config.getBoolean("safe_mode");
			if (safeMode) {
				Log.warn(getName(), "Safe mode enabled. System commands will be disabled");
			}
		}

		/* Check configuration value for: compass_tracker_strict_mode */
		boolean strictMode = true;
		if (config.has("compass_tracker_strict_mode")) {
			if (!config.getBoolean("compass_tracker_strict_mode")) {
				strictMode = false;
			}
		}

		if (config.has("DefaultCountdownGameStarter_time_override")) {
			int defaultGameStarterTimeOverride = config.getInt("DefaultCountdownGameStarter_time_override");

			for (GameStarter starter : gameStarters) {
				if (starter instanceof DefaultCountdownGameStarter) {
					((DefaultCountdownGameStarter) starter).setStartTimeOverride(defaultGameStarterTimeOverride);
				}

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
		ModuleManager.loadModule(this, NoEnderPearlDamage.class, noPearlDamage);
		ModuleManager.loadModule(this, TabList.class, true);
		ModuleManager.loadModule(this, NovaSetReconnectServer.class);
		ModuleManager.loadModule(this, CheckShutdownRequest.class, true);
		ModuleManager.loadModule(this, NovaScoreboard.class, true);
		ModuleManager.loadModule(this, PreventingAleksaFromBreakingTheServer.class, true);
		ModuleManager.loadModule(this, PlayerHeadDrop.class, false);
		ModuleManager.loadModule(this, GoldenHead.class, false);
		ModuleManager.loadModule(this, EdibleHeads.class, false);

		if (NovaCore.isNovaGameEngineEnabled()) {
			ModuleManager.loadModule(this, GameEndManager.class, true);
			ModuleManager.loadModule(this, WinMessage.class, true);
			ModuleManager.loadModule(this, GameStartScoreboardCountdown.class);

			ModuleManager.loadModule(this, NovaGameTimeLimit.class, true);

			/* Game specific */
			ModuleManager.loadModule(this, MissileWarsHandler.class);
			ModuleManager.loadModule(this, UHCHandler.class);
			ModuleManager.loadModule(this, UHCv2Handler.class);
			ModuleManager.loadModule(this, DeathSwapHandler.class);
			ModuleManager.loadModule(this, ManhuntHandler.class);

			/* Listeners */
			Bukkit.getServer().getPluginManager().registerEvents(new GameEventListener(), this);
		}

		if (config.has("enable_golden_heads")) {
			if (config.getBoolean("enable_golden_heads")) {
				ModuleManager.enable(GoldenHead.class);
			}
		}

		if (config.has("enable_edible_heads")) {
			if (config.getBoolean("enable_edible_heads")) {
				ModuleManager.enable(EdibleHeads.class);
			}
		}

		if (config.has("enable_player_head_drop")) {
			if (config.getBoolean("enable_player_head_drop")) {
				ModuleManager.enable(PlayerHeadDrop.class);
			}
		}

		/* Listeners */
		Bukkit.getServer().getPluginManager().registerEvents(this, this);

		/* Check configuration value: time_limit_countdown_line */
		if (config.has("time_limit_countdown_line")) {
			NovaGameTimeLimit.getInstance().setTimeLeftLine(config.getInt("time_limit_countdown_line"));
		}

		/* Check configuration value: time_limit */
		if (config.has("time_limit")) {
			NovaGameTimeLimit.getInstance().setTimeLeft(config.getLong("time_limit"));
			ModuleManager.enable(NovaGameTimeLimit.class);
			Log.info("NovaMain", "Game time limit enabled");
		}

		/* Check configuration value: set_reconnect_server */
		if (config.has("set_reconnect_server")) {
			if (config.getBoolean("set_reconnect_server")) {
				ModuleManager.enable(NovaSetReconnectServer.class);
				Log.info("NovaMain", "NovaSetReconnectServer enabled");
			}
		}

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
				this.sendWebhookLog("Error", "Attempted to start a server with name override while that name is already in use. Server name: " + serverName + " Server host: " + serverHost + " Server port: " + Bukkit.getServer().getPort() + " Server type: " + serverType);
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
				this.sendWebhookLog("Error", "Server name generation failed. Server name: " + serverName + " Server host: " + serverHost + " Server port: " + Bukkit.getServer().getPort() + " Server type: " + serverType);
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
				this.sendWebhookLog("Error", "Server did not receive an id number. Server name: " + serverName + " Server host: " + serverHost + " Server port: " + Bukkit.getServer().getPort() + " Server type: " + serverType);
				Log.fatal("NovaMain", "Server id returned -1. Closing server!");
				Bukkit.getServer().shutdown();
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			this.sendWebhookLog("Error", "Failed to register server in database. Server name: " + serverName + " Server host: " + serverHost + " Server port: " + Bukkit.getServer().getPort() + " Server type: " + serverType);
			Log.fatal("NovaMain", "Failed to register server in database. Closing server!");
			Bukkit.getServer().shutdown();
			return;
		}

		Log.info("NovaMain", "Server registered! Our server id is: " + serverId);

		/* Register heart beat task */
		heartbeatTask = new SimpleTask(this, new Runnable() {
			public void run() {
				AsyncManager.runAsync(new Runnable() {
					@Override
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
				});
			}
		}, 40L, 40L);
		heartbeatTask.start();

		updateServersTask = new SimpleTask(this, new Runnable() {
			public void run() {
				NovaCommons.getAbstractAsyncManager().runAsync(new Runnable() {
					@Override
					public void run() {
						networkManager.updateAsync();
					}
				}, 1L);
			}
		}, 40L, 40L);
		updateServersTask.start();

		if (!disableScoreboard) {
			requireModule(NetherBoardScoreboard.class);
		}

		NetherBoardScoreboard.getInstance().setDefaultTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "NovaUniverse");
		NetherBoardScoreboard.getInstance().setGlobalLine(14, ChatColor.YELLOW + "novauniverse.net");

		if (gameStarter != null) {
			ModuleManager.enable(GameStartScoreboardCountdown.class);
		}

		CommandRegistry.registerCommand(new JoinServerGroupCommand());
		CommandRegistry.registerCommand(new ReloadNetworkManagerCommand());
		CommandRegistry.registerCommand(new ShowServersCommand());
		CommandRegistry.registerCommand(new Base64DumpItemCommand());
		CommandRegistry.registerCommand(new DumpNetworkState());
		CommandRegistry.registerCommand(new ReconnectCommand());
		CommandRegistry.registerCommand(new WhereAmICommand());
		CommandRegistry.registerCommand(new DiscordCommand());
		CommandRegistry.registerCommand(new AccountCommand());
		CommandRegistry.registerCommand(new WhatIsDogeWorthCommand());

		getServer().getMessenger().registerIncomingPluginChannel(this, "novauniverse:data", new NovaPluginMessageListener());
		getServer().getMessenger().registerOutgoingPluginChannel(this, "novauniverse:data");

		this.sendWebhookLog("Server started", "Server " + serverName + " started on " + serverHost + ":" + Bukkit.getServer().getPort() + ". Type: " + serverType);
	}

	public String getFullServerNameForLogs() {
		return serverName + "@" + serverHost + ":" + Bukkit.getServer().getPort();
	}

	public boolean sendWebhookLog(String title, String message) {
		if (discordWebhookUrl == null) {
			return true;
		}

		DiscordWebhook webhook = new DiscordWebhook(discordWebhookUrl);
		webhook.addEmbed(new DiscordWebhook.EmbedObject().addField(title, message, false));
		try {
			webhook.execute();
			return true;
		} catch (IOException e) {
			Log.error(getName(), "Failed to send discord webhook message. " + e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public void onDisable() {
		this.sendWebhookLog("Server shutting down", getFullServerNameForLogs() + " is shutting down");

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

		if (NovaSetReconnectServer.getInstance() != null) {
			if (NovaSetReconnectServer.getInstance().isEnabled()) {
				try {
					NovaSetReconnectServer.getInstance().clearAll();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

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
			NovaUniverseCommons.getServerFinder().joinServerType(player.getUniqueId(), serverType.getReturnToServerType());
		} else {
			NovaUniverseCommons.getServerFinder().joinServerType(player.getUniqueId(), fallbackLobbyServerType);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		// NovaLabymodAPI.sendWatermark(e.getPlayer(), true);

		if (hasLabyMod) {
			NovaLabymodAPI.updateGameInfo(e.getPlayer(), true, getServerType().getDisplayName(), 0, 0);
		}
	}

}