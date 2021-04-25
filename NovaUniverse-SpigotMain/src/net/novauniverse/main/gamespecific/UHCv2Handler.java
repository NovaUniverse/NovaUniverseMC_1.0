package net.novauniverse.main.gamespecific;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import net.novauniverse.main.modules.NovaGameTimeLimit;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.modules.game.triggers.DelayedGameTrigger;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class UHCv2Handler extends NovaModule implements Listener {
	private Task updateTask;
	// public static final int PLAYERS_LEFT_LINE = 1;
	public static final int COUNTDOWN_LINE = 2;

	@Override
	public String getName() {
		return "UHCv2Handler";
	}

	@Override
	public void onLoad() {
		updateTask = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				if (GameManager.getInstance().hasGame()) {
					// if (GameManager.getInstance().getActiveGame().hasStarted()) {
					// NetherBoardScoreboard.getInstance().setGlobalLine(PLAYERS_LEFT_LINE,
					// ChatColor.AQUA + "" +
					// GameManager.getInstance().getActiveGame().getPlayers().size() +
					// ChatColor.GOLD + " Players left");
					// }

					DelayedGameTrigger gracePeriodTrigger = (DelayedGameTrigger) GameManager.getInstance().getActiveGame().getTrigger("novauniverse.uhc.endgraceperiod");
					DelayedGameTrigger meetupTrigger = (DelayedGameTrigger) GameManager.getInstance().getActiveGame().getTrigger("novauniverse.uhc.meetup");

					if (gracePeriodTrigger != null && meetupTrigger != null) {
						if (gracePeriodTrigger.isRunning()) {
							NetherBoardScoreboard.getInstance().setGlobalLine(COUNTDOWN_LINE, ChatColor.GOLD + "Graceperiod end: " + ChatColor.AQUA + TextUtils.secondsToHoursMinutes(gracePeriodTrigger.getTicksLeft() / 20));
						} else if (meetupTrigger.isRunning()) {
							NetherBoardScoreboard.getInstance().setGlobalLine(COUNTDOWN_LINE, ChatColor.GOLD + "Meetup in: " + ChatColor.AQUA + TextUtils.secondsToHoursMinutes(meetupTrigger.getTicksLeft() / 20));
						} else {
							NetherBoardScoreboard.getInstance().clearGlobalLine(COUNTDOWN_LINE);
							NovaGameTimeLimit.getInstance().setShowTimer(true);
						}
					} else {
						Log.error(getName(), "gracePeriodTrigger or meetupTrigger is null");
					}
				}
			}
		}, 5L);
	}

	@Override
	public void onEnable() throws Exception {
		updateTask.start();

		NovaGameTimeLimit.getInstance().setTimeLeftLine(COUNTDOWN_LINE);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(updateTask);
	}
}