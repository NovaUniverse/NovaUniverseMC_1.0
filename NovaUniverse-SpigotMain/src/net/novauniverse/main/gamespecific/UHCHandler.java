package net.novauniverse.main.gamespecific;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class UHCHandler extends NovaModule implements Listener {
	private Task updateTask;

	public static final int PLAYERS_LEFT_LINE = 1;

	@Override
	public String getName() {
		return "MissileWarsHandler";
	}

	@Override
	public void onLoad() {
		updateTask = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				if (GameManager.getInstance().hasGame()) {
					if (GameManager.getInstance().getActiveGame().hasStarted()) {
						NetherBoardScoreboard.getInstance().setGlobalLine(PLAYERS_LEFT_LINE, ChatColor.AQUA + "" + GameManager.getInstance().getActiveGame().getPlayers().size() + ChatColor.GOLD + " Players left");
					}
				}
			}
		}, 5L);
	}

	@Override
	public void onEnable() throws Exception {
		updateTask.start();

		// GameStartScoreboardCountdown.getInstance().setCountdownLine(2);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(updateTask);
	}
}
