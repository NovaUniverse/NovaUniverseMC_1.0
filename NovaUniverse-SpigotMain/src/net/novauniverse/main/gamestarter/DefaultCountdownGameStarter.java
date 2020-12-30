package net.novauniverse.main.gamestarter;

import org.bukkit.Bukkit;
import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.modules.game.countdown.DefaultGameCountdown;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class DefaultCountdownGameStarter extends GameStarter {
	private Task checkTask;

	public static final int START_TIME = 180;
	public static final int START_TIME_HALF = 60;

	private boolean passedHalf;

	@Override
	public String getName() {
		return "DefaultCountdownGameStarter";
	}

	@Override
	public void onEnable() {
		if (GameManager.getInstance().getCountdown() instanceof DefaultGameCountdown) {
			((DefaultGameCountdown) GameManager.getInstance().getCountdown()).setStartTime(START_TIME);
		}

		passedHalf = false;

		checkTask = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				if (GameManager.getInstance().hasGame()) {
					if (GameManager.getInstance().getActiveGame().hasStarted()) {
						disable();
						return;
					}
				}

				int c = getGroupCount();

				if (c >= 2) {
					if (GameManager.getInstance().getCountdown().isCountdownRunning()) {
						Log.info(getName(), "Starting countdown since there are 2 or more players / teams online");
						GameManager.getInstance().getCountdown().startCountdown();
					}
				} else {
					if (GameManager.getInstance().getCountdown().isCountdownRunning()) {
						GameManager.getInstance().getCountdown().cancelCountdown();
						passedHalf = false;
					}
				}

				if (GameManager.getInstance().getCountdown().isCountdownRunning()) {
					if (!passedHalf) {
						if (Bukkit.getServer().getOnlinePlayers().size() > (NovaMain.getInstance().getServerType().getTargetPlayerCount() / 2)) {
							passedHalf = true;

							if (GameManager.getInstance().getCountdown().getTimeLeft() > START_TIME) {
								Log.info(getName(), "Decreasing countdown since there is more than falf the target amount of players online");
								GameManager.getInstance().getCountdown().setTimeLeft(START_TIME);
							}
						}
					}
				}
			}
		}, 5L);
		checkTask.start();
	}

	private void disable() {
		Task.tryStopTask(checkTask);
	}
	
	private int getGroupCount() {
		if (GameManager.getInstance().hasGame()) {
			// Special case for missile wars
			if (GameManager.getInstance().getActiveGame().getName().equalsIgnoreCase("missilewars")) {
				return Bukkit.getServer().getOnlinePlayers().size();
			}
		}

		if (GameManager.getInstance().isUseTeams()) {
			int result = 0;
			for (Team team : TeamManager.getTeamManager().getTeams()) {
				if (team.getMembers().size() > 0) {
					result++;
				}
			}

			return result;
		} else {
			return Bukkit.getServer().getOnlinePlayers().size();
		}
	}

	@Override
	public boolean shouldShowCountdown() {
		return GameManager.getInstance().getCountdown().isCountdownRunning();
	}

	@Override
	public long getTimeLeft() {
		return GameManager.getInstance().getCountdown().getTimeLeft();
	}

	/*
	 * private void startCountdown() { if (timer != null) { timer.cancel(); timer =
	 * null; }
	 * 
	 * timer = new BasicTimer(START_TIME_LESS_THAN_HALF);
	 * 
	 * timer.addTickCallback(new TickCallback() {
	 * 
	 * @Override public void execute(long timeLeft) {
	 * //System.out.println(timeLeft); if (timeLeft <= 10) { if (timeLeft > 0) {
	 * Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "Starting in: " +
	 * ChatColor.AQUA + timeLeft); } } } });
	 * 
	 * timer.addFinishCallback(new Callback() {
	 * 
	 * @Override public void execute() { try { disable();
	 * GameManager.getInstance().start(); } catch (IOException e) {
	 * Log.fatal("DefaultCountdownGameStarter", "Failed to start game! " +
	 * e.getClass().getName() + " " + e.getMessage());
	 * Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED +
	 * "Failed to start the game! Caused by: " + e.getClass().getName() + " " +
	 * e.getMessage());
	 * 
	 * NovaMain.getInstance().setInErrorState(true);
	 * 
	 * e.printStackTrace(); } } });
	 * 
	 * timer.start();
	 * 
	 * running = true; }
	 */
}