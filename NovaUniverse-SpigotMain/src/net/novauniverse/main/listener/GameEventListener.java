package net.novauniverse.main.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.connorlinfoot.titleapi.TitleAPI;

import net.novauniverse.commons.network.NovaNetworkManager;
import net.novauniverse.main.NovaMain;
import net.novauniverse.main.gamespecific.DeathSwapHandler;
import net.novauniverse.main.gamespecific.MissileWarsHandler;
import net.novauniverse.main.gamespecific.UHCHandler;
import net.novauniverse.main.labymod.NovaLabymodAPI;
import net.novauniverse.main.modules.GameEndManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameEndEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameLoadedEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameStartEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameStartFailureEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.PlayerEliminatedEvent;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;

public class GameEventListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameStart(GameStartEvent e) {
		try {
			NovaNetworkManager.flagAsGameStarted(NovaMain.getInstance().getServerId());
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				NovaLabymodAPI.sendCurrentPlayingGamemode(player, true, e.getGame().getDisplayName());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.error("NovaMain", "Failed flag this server as minigame_started");
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameLoad(GameLoadedEvent e) {
		if (!NovaMain.getInstance().isDisableScoreboard()) {
			NetherBoardScoreboard.getInstance().setGlobalLine(0, ChatColor.YELLOW + "" + ChatColor.BOLD + e.getGame().getDisplayName());
		}

		if (NovaMain.getInstance().getGameStarter() == null) {
			Log.warn("GameEventListener", "No game starter defined. The game will not auto start");
		} else {
			Log.info("GameEventListener", "Register events for game starter: " + NovaMain.getInstance().getGameStarter().getClass().getName());
			NovaMain.getInstance().getGameStarter().onEnable();
			Bukkit.getServer().getPluginManager().registerEvents(NovaMain.getInstance().getGameStarter(), NovaMain.getInstance());
		}

		switch (e.getGame().getName().toLowerCase()) {
		case "missilewars":
			ModuleManager.enable(MissileWarsHandler.class);
			break;

		case "uhc":
			ModuleManager.enable(UHCHandler.class);
			break;
			
		case "deathswap":
			ModuleManager.enable(DeathSwapHandler.class);
			break;

		default:
			break;
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (GameManager.getInstance().isEnabled()) {
			if (GameManager.getInstance().hasGame()) {
				NovaLabymodAPI.sendCurrentPlayingGamemode(e.getPlayer(), true, GameManager.getInstance().getActiveGame().getDisplayName());
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
		NovaMain.getInstance().setInErrorState(true);

		try {
			NovaNetworkManager.flagServerAsFailed(NovaMain.getInstance().getServerId());
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
		}.runTaskLater(NovaMain.getInstance(), 200L);
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
			}.runTaskLater(NovaMain.getInstance(), 10L);
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
}