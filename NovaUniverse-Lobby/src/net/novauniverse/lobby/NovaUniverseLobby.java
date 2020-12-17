package net.novauniverse.lobby;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONException;

import net.novauniverse.lobby.messages.LobbyMessages;
import net.novauniverse.lobby.misc.DoubleJump;
import net.novauniverse.lobby.spawn.NovaUniverseSpawn;
import net.novauniverse.lobby.spawn.NovaUniverseSpawnProtection;
import net.novauniverse.lobby.spawn.kotl.KingOfTheLadderManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.ASCIIArtGenerator;
import net.zeeraa.novacore.commons.utils.JSONFileType;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.abstraction.events.VersionIndependantPlayerAchievementAwardedEvent;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.gui.GUIManager;
import net.zeeraa.novacore.spigot.module.modules.jumppad.JumpPadManager;
import net.zeeraa.novacore.spigot.module.modules.jumppad.command.JumpPadCommand;
import net.zeeraa.novacore.spigot.module.modules.multiverse.MultiverseManager;
import net.zeeraa.novacore.spigot.module.modules.multiverse.MultiverseWorld;
import net.zeeraa.novacore.spigot.module.modules.multiverse.PlayerUnloadOption;
import net.zeeraa.novacore.spigot.module.modules.multiverse.WorldUnloadOption;
import net.zeeraa.novacore.spigot.novaplugin.NovaPlugin;

public class NovaUniverseLobby extends NovaPlugin {
	private static NovaUniverseLobby instance;

	public static NovaUniverseLobby getInstance() {
		return instance;
	}

	private File jumpPadFile;
	private File kotlFile;
	private File worldFile;

	private Location spawnLocation;

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	@Override
	public void onEnable() {
		NovaUniverseLobby.instance = this;

		this.jumpPadFile = new File(this.getDataFolder().getPath() + File.separator + "jump_pads.json");
		this.kotlFile = new File(this.getDataFolder().getPath() + File.separator + "king_of_the_ladder.json");
		this.worldFile = new File(this.getDataFolder().getPath() + File.separator + "lobby_world");

		try {
			// Folders
			FileUtils.forceMkdir(this.getDataFolder());
			if (!jumpPadFile.exists()) {
				JSONFileUtils.createEmpty(jumpPadFile, JSONFileType.JSONArray);
			}

			if(!kotlFile.exists()) {
				JSONFileUtils.createEmpty(kotlFile, JSONFileType.JSONArray);
			}
			
			// Configuration
			this.saveDefaultConfig();

			// Multiverse
			this.requireModule(MultiverseManager.class);

			MultiverseWorld world = MultiverseManager.getInstance().createFromFile(worldFile, WorldUnloadOption.DELETE);

			world.getWorld().setStorm(false);
			world.setLockWeather(true);

			world.setPlayerUnloadOptions(PlayerUnloadOption.SEND_TO_FIRST);

			// Require modules
			this.requireModule(GUIManager.class);

			// Register modules
			this.loadModule(DoubleJump.class, true);
			this.loadModule(LobbyMessages.class, true);

			// Lobby spawn
			ConfigurationSection spawnSection = this.getConfig().getConfigurationSection("spawn_location");

			World spawnWorld = Bukkit.getServer().getWorld(spawnSection.getString("world"));

			if (spawnWorld == null) {
				throw new NullPointerException("World named: " + spawnSection.getString("world") + " was not found. Configured in config.yml for " + this.getName() + " was not found");
			}

			double sX = spawnSection.getDouble("x");
			double sY = spawnSection.getDouble("y");
			double sZ = spawnSection.getDouble("z");

			float sYaw = (float) spawnSection.getDouble("yaw");
			float sPitch = (float) spawnSection.getDouble("pitch");

			this.spawnLocation = new Location(spawnWorld, sX, sY, sZ, sYaw, sPitch);

			this.loadModule(NovaUniverseSpawn.class, true);
			this.loadModule(NovaUniverseSpawnProtection.class, true);

			// Jump pads

			this.requireModule(JumpPadManager.class);

			CommandRegistry.registerCommand(new JumpPadCommand());

			// Run after all plugins has been loaded
			new BukkitRunnable() {
				@Override
				public void run() {
					Log.info(getName(), "-=-= Beging final load =-=-");
					// Jump pads
					try {
						JumpPadManager.getInstance().loadJumpPads(jumpPadFile, NovaUniverseLobby.getInstance());
					} catch (JSONException | IOException e) {
						e.printStackTrace();
					}
				}
			}.runTaskLater(this, 1L);
			
			
			this.loadModule(KingOfTheLadderManager.class, true);
			KingOfTheLadderManager.getInstance().loadArenas(kotlFile);
		} catch (Exception e) {
			e.printStackTrace();
			NovaUniverseLobby.showCrashMessage(e);
			this.disableSelf();
			return;
		}
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll((Plugin) this);
		Bukkit.getScheduler().cancelTasks(this);

		if (ModuleManager.moduleExists(JumpPadManager.class)) {
			try {
				JumpPadManager.getInstance().saveJumpPads(jumpPadFile, this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onVersionIndependantPlayerAchievementAwarded(VersionIndependantPlayerAchievementAwardedEvent e) {
		e.setCancelled(true);
	}

	/**
	 * Display a error message as a blue screen of death. This is just a easter egg
	 * do not use for exception logging
	 * 
	 * @param e The {@link Exception}
	 */
	public static final void showCrashMessage(Exception e) {
		try {
			List<String> art = ASCIIArtGenerator.generateTextArt(":(", 10);

			String artLine = "";

			for (String s : art) {
				artLine += s + "\n";
			}

			Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "\n\n\n" + artLine + "\n\nNovaUniverse ran into a problem and needs to restart. We're\njust collecting some error info, and then we'll restart for\nyou.\n\n0% complete\n\nStop code: " + e.getClass().getName() + "\n\n\nFor real tho the plugin crashed. Sorry\n\n");
		} catch (Exception e1) {
			System.err.println("an error occured while diplaying the error");
		}
	}
}