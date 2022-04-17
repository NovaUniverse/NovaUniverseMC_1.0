package net.novauniverse.main.gamestarter;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.timers.TickCallback;
import net.zeeraa.novacore.commons.utils.Callback;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.timers.BasicTimer;

@Deprecated
public class DefaultCountdownGameStarterOld extends GameStarter {
	private BasicTimer timer;
	private Task checkTask;

	public static final long START_TIME_LESS_THAN_HALF = 180;
	public static final long START_TIME_HALF = 60;

	private boolean running;
	private boolean passedHalf;

	@Override
	public String getName() {
		return "DefaultCountdownGameStarter";
	}

	@Override
	public void onEnable() {
		timer = null;

		running = false;
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
					if (!running) {
						Log.info(getName(), "Starting countdown since there are 2 or more players / teams online");
						startCountdown();
					}
				} else {
					if (timer != null) {
						timer.cancel();
						timer = null;
						running = false;
						passedHalf = false;
					}
				}

				if (running && timer != null) {
					if (!passedHalf) {
						if (Bukkit.getServer().getOnlinePlayers().size() > (NovaMain.getInstance().getServerType().getTargetPlayerCount() / 2)) {
							passedHalf = true;

							if (timer.getTimeLeft() > START_TIME_LESS_THAN_HALF) {
								Log.info(getName(), "Decreasing countdown since there is more than falf the target amount of players online");
								timer.setTimeLeft(START_TIME_LESS_THAN_HALF);
							}
						}
					}
				}
			}
		}, 5L);
		checkTask.start();
	}

	private void disable() {
		timer = null;
		Task.tryStopTask(checkTask);
		running = false;
	}

	private void startCountdown() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		timer = new BasicTimer(START_TIME_LESS_THAN_HALF);

		timer.addTickCallback(new TickCallback() {
			@Override
			public void execute(long timeLeft) {
				//System.out.println(timeLeft);
				if (timeLeft <= 10) {
					if (timeLeft > 0) {
						Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "Starting in: " + ChatColor.AQUA + timeLeft);
					}
				}
			}
		});

		timer.addFinishCallback(new Callback() {
			@Override
			public void execute() {
				try {
					disable();
					GameManager.getInstance().start();
				} catch (IOException e) {
					Log.fatal("DefaultCountdownGameStarter", "Failed to start game! " + e.getClass().getName() + " " + e.getMessage());
					Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "Failed to start the game! Caused by: " + e.getClass().getName() + " " + e.getMessage());

					NovaMain.getInstance().setInErrorState(true);

					e.printStackTrace();
				}
			}
		});

		timer.start();

		running = true;
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
		if (timer != null) {
			return timer.isRunning();
		}
		return false;
	}

	@Override
	public long getTimeLeft() {
		if (timer != null) {
			return timer.getTimeLeft();
		}

		return 0;
	}
}