package net.novauniverse.main.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.zeeraa.novacore.commons.timers.TickCallback;
import net.zeeraa.novacore.commons.utils.Callback;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameEndEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameStartEvent;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.timers.BasicTimer;

public class NovaGameTimeLimit extends NovaModule implements Listener {
	private static NovaGameTimeLimit instance;
	private static final int DEFAULT_TIME_LEFT_LINE = 3;
	private int timeLeftLine = DEFAULT_TIME_LEFT_LINE;
	private BasicTimer timer;

	private boolean showTimer;

	public NovaGameTimeLimit() {
		super("NovaUniverse.NovaGameTimeLimit");
	}

	public static NovaGameTimeLimit getInstance() {
		return instance;
	}

	@Override
	public void onLoad() {
		NovaGameTimeLimit.instance = this;
		
		showTimer = true;
		
		timer = new BasicTimer(3600);

		timer.addFinishCallback(new Callback() {
			@Override
			public void execute() {
				if (GameManager.getInstance().isEnabled()) {
					if (GameManager.getInstance().hasGame()) {
						if (GameManager.getInstance().getActiveGame().hasStarted()) {
							if (!GameManager.getInstance().getActiveGame().hasEnded()) {
								GameManager.getInstance().getActiveGame().endGame(GameEndReason.TIME);
							}
						}
					}
				}
			}
		});

		timer.addTickCallback(new TickCallback() {
			@Override
			public void execute(long timeLeft) {
				if (timeLeft == 60) {
					for (Player player : Bukkit.getServer().getOnlinePlayers()) {
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
						player.sendMessage(ChatColor.GOLD + "The game will end in 1 minute");
					}
				} else if (timeLeft == 600) {
					for (Player player : Bukkit.getServer().getOnlinePlayers()) {
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
						player.sendMessage(ChatColor.GOLD + "The game will end in 10 minutes");
					}
				}

				if (showTimer) {
					NetherBoardScoreboard.getInstance().setGlobalLine(timeLeftLine, ChatColor.GOLD + "Time left: " + ChatColor.AQUA + TextUtils.secondsToTime(timeLeft));
				}
			}
		});
	}

	public void setTimeLeftLine(int timeLeftLine) {
		this.timeLeftLine = timeLeftLine;
	}

	public void setTimeLeft(long timeLeft) {
		timer.setTimeLeft(timeLeft);
	}

	public boolean isShowTimer() {
		return showTimer;
	}
	
	public void setShowTimer(boolean showTimer) {
		this.showTimer = showTimer;
	}
	
	public BasicTimer getTimer() {
		return timer;
	}

	@Override
	public void onDisable() throws Exception {
		timer.cancel();
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onGameStart(GameStartEvent e) {
		if (!timer.hasStarted()) {
			timer.start();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onGameEnd(GameEndEvent e) {
		if (!timer.hasFinished()) {
			timer.cancel();
		}
	}
}