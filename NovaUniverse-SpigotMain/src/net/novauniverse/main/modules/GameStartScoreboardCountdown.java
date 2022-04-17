package net.novauniverse.main.modules;

import org.bukkit.ChatColor;

import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class GameStartScoreboardCountdown extends NovaModule {
	private static GameStartScoreboardCountdown instance;

	private boolean countdownVisible;

	public static final int DEFAULT_COUNTDOWN_LINE = 1;

	private int countdownLine;

	public static GameStartScoreboardCountdown getInstance() {
		return instance;
	}

	private Task task;

	public GameStartScoreboardCountdown() {
		super("NovaUniverse.GameStartScoreboardCountdown");
	}

	@Override
	public void onLoad() {
		GameStartScoreboardCountdown.instance = this;
		countdownLine = DEFAULT_COUNTDOWN_LINE;
		countdownVisible = false;

		task = new SimpleTask(NovaMain.getInstance(), new Runnable() {
			@Override
			public void run() {
				// System.out.println("NovaMain.getInstance().getGameStarter().shouldShowCountdown():
				// " + NovaMain.getInstance().getGameStarter().shouldShowCountdown());
				if (NovaMain.getInstance().getGameStarter().shouldShowCountdown()) {
					countdownVisible = true;
					NetherBoardScoreboard.getInstance().setGlobalLine(countdownLine, ChatColor.GOLD + "Starting in: " + ChatColor.AQUA + TextUtils.secondsToHoursMinutes(NovaMain.getInstance().getGameStarter().getTimeLeft()));
				} else {
					if (countdownVisible) {
						NetherBoardScoreboard.getInstance().clearGlobalLine(countdownLine);
						countdownVisible = false;
					}
				}
			}
		}, 10L);
	}

	public int getCountdownLine() {
		return countdownLine;
	}

	public void setCountdownLine(int countdownLine) {
		if (countdownVisible) {
			NetherBoardScoreboard.getInstance().clearGlobalLine(countdownLine);
		}
		this.countdownLine = countdownLine;
	}

	@Override
	public void onEnable() throws Exception {
		task.start();
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}
}