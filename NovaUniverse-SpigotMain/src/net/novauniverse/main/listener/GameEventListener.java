package net.novauniverse.main.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.connorlinfoot.titleapi.TitleAPI;

import net.novauniverse.commons.network.NovaNetworkManager;
import net.novauniverse.main.NovaMain;
import net.novauniverse.main.gamespecific.DeathSwapHandler;
import net.novauniverse.main.gamespecific.ManhuntHandler;
import net.novauniverse.main.gamespecific.MissileWarsHandler;
import net.novauniverse.main.gamespecific.UHCHandler;
import net.novauniverse.main.gamespecific.UHCv2Handler;
import net.novauniverse.main.modules.GameEndManager;
import net.novauniverse.main.modules.NovaScoreboard;
import net.novauniverse.main.modules.NovaSetReconnectServer;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentSound;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameEndEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameLoadedEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameStartEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameStartFailureEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.PlayerEliminatedEvent;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;

public class GameEventListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameStart(GameStartEvent e) {
		try {
			NovaMain.getInstance().sendWebhookLog("Game started", "Started game session with " + e.getGame().getDisplayName() + " on " + NovaMain.getInstance().getFullServerNameForLogs());

			NovaNetworkManager.flagAsGameStarted(NovaMain.getInstance().getServerId());
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.error("NovaMain", "Failed flag this server as minigame_started");
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameLoad(GameLoadedEvent e) {
		ModuleManager.require(NovaSetReconnectServer.class);

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
			NovaScoreboard.getInstance().setPlayersLeftLine(1);
			ModuleManager.enable(UHCHandler.class);
			NovaMain.getInstance().enableSpectateGameCommand();
			break;

		case "uhcv2":
			NovaScoreboard.getInstance().setPlayersLeftLine(1);
			ModuleManager.enable(UHCv2Handler.class);
			NovaMain.getInstance().enableSpectateGameCommand();
			break;

		case "deathswap":
			NovaScoreboard.getInstance().setPlayersLeftLine(1);
			ModuleManager.enable(DeathSwapHandler.class);
			NovaMain.getInstance().enableSpectateGameCommand();
			break;

		case "manhunt":
			ModuleManager.enable(ManhuntHandler.class);
			NovaMain.getInstance().enableSpectateGameCommand();
			break;

		case "survivalgames":
			NovaScoreboard.getInstance().setPlayersLeftLine(1);
			NovaMain.getInstance().enableSpectateGameCommand();
			break;

		case "skywars":
			NovaScoreboard.getInstance().setPlayersLeftLine(1);
			NovaMain.getInstance().enableSpectateGameCommand();
			break;

		default:
			break;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameEnd(GameEndEvent e) {
		NovaMain.getInstance().sendWebhookLog("Game ended", "Ended game session with " + e.getGame().getDisplayName() + " on " + NovaMain.getInstance().getFullServerNameForLogs());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameStartFailure(GameStartFailureEvent e) {
		NovaMain.getInstance().sendWebhookLog("Error", "GameStartFailureEvent received on game " + e.getGame().getDisplayName() + " running on " + NovaMain.getInstance().getFullServerNameForLogs() + ". Cause: " + e.getException().getClass().getName() + " " + e.getException().getMessage());
		
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
			final Player player = e.getPlayer().getPlayer();

			new BukkitRunnable() {
				@Override
				public void run() {
					String sub = "";

					if (e.getKiller() != null) {
						sub = ChatColor.RED + "Killed by " + ChatColor.AQUA + e.getKiller().getName();
					}

					VersionIndependentUtils.get().playSound(player, player.getLocation(), VersionIndependentSound.WITHER_HURT, 1F, 1F);
					TitleAPI.sendTitle(player, 5, 60, 10, ChatColor.RED + "Eliminated", sub);
				}
			}.runTaskLater(NovaMain.getInstance(), 10L);
		}
	}
}