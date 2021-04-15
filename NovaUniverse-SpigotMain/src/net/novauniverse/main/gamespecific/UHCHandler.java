package net.novauniverse.main.gamespecific;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.modules.game.triggers.DelayedGameTrigger;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class UHCHandler extends NovaModule implements Listener {
	private Task updateTask;

	private boolean borderCountdownShown;

	public static final int PLAYERS_LEFT_LINE = 1;
	public static final int BORDER_COUNTDOWN_LINE = 2;

	@Override
	public String getName() {
		return "UHCHandler";
	}

	@Override
	public void onLoad() {
		borderCountdownShown = false;

		updateTask = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				if (GameManager.getInstance().hasGame()) {
					if (GameManager.getInstance().getActiveGame().hasStarted()) {
						NetherBoardScoreboard.getInstance().setGlobalLine(PLAYERS_LEFT_LINE, ChatColor.AQUA + "" + GameManager.getInstance().getActiveGame().getPlayers().size() + ChatColor.GOLD + " Players left");
					}

					DelayedGameTrigger borderTrigger = (DelayedGameTrigger) GameManager.getInstance().getActiveGame().getTrigger("novacore.worldborder.start");

					if (borderTrigger != null) {
						if (borderTrigger.isRunning()) {
							borderCountdownShown = true;
							NetherBoardScoreboard.getInstance().setGlobalLine(BORDER_COUNTDOWN_LINE, ChatColor.GOLD + "Border in: " + ChatColor.AQUA + TextUtils.secondsToHoursMinutes(borderTrigger.getTicksLeft() / 20));
						} else {
							if (borderCountdownShown) {
								NetherBoardScoreboard.getInstance().clearGlobalLine(BORDER_COUNTDOWN_LINE);
								borderCountdownShown = false;
							}
						}
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