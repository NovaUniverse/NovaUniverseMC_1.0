package net.novauniverse.main.gamespecific;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import net.novauniverse.main.modules.NovaGameTimeLimit;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.DelayedGameTrigger;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class UHCv2Handler extends NovaModule implements Listener {
	private Task updateTask;
	// public static final int PLAYERS_LEFT_LINE = 1;
	public static final int COUNTDOWN_LINE = 2;
	public static final int FINAL_HEAL_LINE = 3;

public UHCv2Handler() {
	super("NovaUniverse.UHCv2Handler");
}

	private boolean cleanCountdownLine;

	@Override
	public void onLoad() {
		updateTask = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				if (GameManager.getInstance().hasGame()) {
					DelayedGameTrigger gracePeriodTrigger = (DelayedGameTrigger) GameManager.getInstance().getActiveGame().getTrigger("novauniverse.uhc.endgraceperiod");
					DelayedGameTrigger meetupTrigger = (DelayedGameTrigger) GameManager.getInstance().getActiveGame().getTrigger("novauniverse.uhc.meetup");

					if (gracePeriodTrigger != null && meetupTrigger != null) {
						if (gracePeriodTrigger.isRunning()) {
							cleanCountdownLine = true;
							NetherBoardScoreboard.getInstance().setGlobalLine(COUNTDOWN_LINE, ChatColor.GOLD + "Graceperiod end: " + ChatColor.AQUA + TextUtils.secondsToHoursMinutes(gracePeriodTrigger.getTicksLeft() / 20));
							NovaGameTimeLimit.getInstance().setShowTimer(false);
						} else if (meetupTrigger.isRunning()) {
							cleanCountdownLine = true;
							NetherBoardScoreboard.getInstance().setGlobalLine(COUNTDOWN_LINE, ChatColor.GOLD + "Meetup in: " + ChatColor.AQUA + TextUtils.secondsToHoursMinutes(meetupTrigger.getTicksLeft() / 20));
							NovaGameTimeLimit.getInstance().setShowTimer(false);
						} else {
							if (cleanCountdownLine) {
								cleanCountdownLine = false;
								NetherBoardScoreboard.getInstance().clearGlobalLine(COUNTDOWN_LINE);
							}
							NovaGameTimeLimit.getInstance().setShowTimer(true);
						}
					} else {
						Log.error(getName(), "gracePeriodTrigger or meetupTrigger is null");
					}

					if (GameManager.getInstance().getActiveGame().getTrigger("novauniverse.uhc.finalheal") != null) {
						DelayedGameTrigger finalHealTrigger = (DelayedGameTrigger) GameManager.getInstance().getActiveGame().getTrigger("novauniverse.uhc.finalheal");

						if (finalHealTrigger.isRunning()) {
							NetherBoardScoreboard.getInstance().setGlobalLine(FINAL_HEAL_LINE, ChatColor.GOLD + "Final heal: " + ChatColor.AQUA + TextUtils.secondsToHoursMinutes(finalHealTrigger.getTicksLeft() / 20));
						} else {
							NetherBoardScoreboard.getInstance().clearGlobalLine(FINAL_HEAL_LINE);
						}
					}
				}
			}
		}, 5L);
	}

	@Override
	public void onEnable() throws Exception {
		updateTask.start();

		NovaGameTimeLimit.getInstance().setShowTimer(false);
		NovaGameTimeLimit.getInstance().setTimeLeftLine(COUNTDOWN_LINE);

		cleanCountdownLine = false;
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(updateTask);
	}
}