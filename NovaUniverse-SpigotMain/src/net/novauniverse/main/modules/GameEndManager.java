package net.novauniverse.main.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameEndEvent;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class GameEndManager extends NovaModule implements Listener {
	public static final int ATTEMPTS_BEFORE_FALLBACK_LOBBY = 5;
	public static final int ATTEMPTS_BEFORE_FAIL = 10;

	private static GameEndManager instance;

	private int sendAttempts;

	private boolean preventShutdown;

	public static GameEndManager getInstance() {
		return instance;
	}

	public void setPreventShutdown(boolean preventShutdown) {
		this.preventShutdown = preventShutdown;
	}

	public boolean isPreventShutdown() {
		return preventShutdown;
	}

	public GameEndManager() {
		super("NovaUniverse.GameEndManager");
	}

	@Override
	public void onLoad() {
		GameEndManager.instance = this;
		this.sendAttempts = 0;
		this.preventShutdown = false;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameEnd(GameEndEvent e) {
		switch (e.getReason()) {
		case OPERATOR_ENDED_GAME:
			Bukkit.getServer().broadcastMessage(ChatColor.RED + "Game was ended by a staff member");
			break;

		case TIME:
			Bukkit.getServer().broadcastMessage(ChatColor.RED + "Game was ended due to time limit reached");
			break;

		case SERVER_ENDED_GAME:
			Bukkit.getServer().broadcastMessage(ChatColor.RED + "Game was ended by the server");
			break;

		default:
			break;
		}

		if (!preventShutdown) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(NovaMain.getInstance(), new Runnable() {
				@Override
				public void run() {
					Bukkit.getServer().getOnlinePlayers().forEach(p -> LanguageManager.getString(p.getUniqueId(), "novauniverse.game.sending_you_to_lobby_10_seconds"));

					Bukkit.getScheduler().scheduleSyncDelayedTask(NovaMain.getInstance(), new Runnable() {
						@Override
						public void run() {
							attemptSend();
						}
					}, 200L);
				}
			}, 100L);
		}
	}

	public void attemptSend() {
		if (Bukkit.getServer().getOnlinePlayers().size() == 0) {
			Log.info("GameEndManager", "No players left. Closing server");
			Bukkit.getServer().shutdown();
			return;
		}

		if (sendAttempts > GameEndManager.ATTEMPTS_BEFORE_FAIL) {
			Log.warn("GameEndManager", "Failed to send players to the lobby within " + GameEndManager.ATTEMPTS_BEFORE_FAIL + " attempts. Closing server");

			Bukkit.getServer().getOnlinePlayers().forEach(p -> p.kickPlayer(LanguageManager.getString(p.getUniqueId(), "novauniverse.game.server.restarting", NovaMain.getInstance().getServerType().getDisplayName())));

			Bukkit.getServer().shutdown();
			return;
		}

		sendAttempts++;

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			boolean fallback = sendAttempts > GameEndManager.ATTEMPTS_BEFORE_FALLBACK_LOBBY;
			if (fallback) {
				Log.info("GameEndManager", "Using fallback lobby");
			}
			NovaMain.getInstance().sendToLobby(player, fallback);
		}

		Bukkit.getScheduler().runTaskLater(NovaMain.getInstance(), new Runnable() {
			@Override
			public void run() {
				attemptSend();
			}
		}, 100L);
	}
}