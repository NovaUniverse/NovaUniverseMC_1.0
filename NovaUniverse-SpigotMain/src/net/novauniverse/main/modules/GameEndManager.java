package net.novauniverse.main.modules;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameEndEvent;

public class GameEndManager extends NovaModule implements Listener {
	public static final int ATTEMPTS_BEFORE_FALLBACK_LOBBY = 5;
	public static final int ATTEMPTS_BEFORE_FAIL = 10;

	private int sendAttempts;

	@Override
	public String getName() {
		return "GameEndManager";
	}

	@Override
	public void onLoad() {
		this.sendAttempts = 0;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameEnd(GameEndEvent e) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(NovaMain.getInstance(), new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					p.sendMessage(LanguageManager.getString(p.getUniqueId(), "novauniverse.game.sending_you_to_lobby_10_seconds"));
				}

				Bukkit.getScheduler().scheduleSyncDelayedTask(NovaMain.getInstance(), new Runnable() {
					@Override
					public void run() {
						attemptSend();
					}
				}, 200L);
			}
		}, 100L);
	}

	public void attemptSend() {
		if (Bukkit.getServer().getOnlinePlayers().size() == 0) {
			Log.info("GameEndManager", "No players left. Closing server");
			Bukkit.getServer().shutdown();
			return;
		}

		if (sendAttempts > GameEndManager.ATTEMPTS_BEFORE_FAIL) {
			Log.warn("GameEndManager", "Failed to send players to the lobby within " + GameEndManager.ATTEMPTS_BEFORE_FAIL + " attempts. Closing server");
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				p.kickPlayer(LanguageManager.getString(p.getUniqueId(), "novauniverse.game.server.restarting", NovaMain.getInstance().getServerType().getDisplayName()));
			}
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