package net.novauniverse.main.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.novauniverse.commons.utils.TextUtils;
import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class NovaScoreboard extends NovaModule {
	private static NovaScoreboard instance;
	
	public static NovaScoreboard getInstance() {
		return instance;
	}
	
	private Task task;

	private int playersLeftLine = -1;

	@Override
	public String getName() {
		return "NovaScoreboard";
	}

	public void setPlayersLeftLine(int playersLeftLine) {
		this.playersLeftLine = playersLeftLine;
	}

	public int getPlayersLeftLine() {
		return playersLeftLine;
	}

	@Override
	public void onLoad() {
		NovaScoreboard.instance = this;
		task = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				if(playersLeftLine != -1 && NovaMain.getInstance().hasGameInterface()) {
					if(NovaMain.getInstance().getGameInterface().hasStarted()) {
					NetherBoardScoreboard.getInstance().setGlobalLine(playersLeftLine, ChatColor.GOLD + "Players left: " + ChatColor.AQUA + NovaMain.getInstance().getGameInterface().getInGamePlayers());
					}
				}
				
				double tps = -1;
				try {
					tps = NovaCore.getInstance().getVersionIndependentUtils().getRecentTps()[0];
				} catch (Exception e) {
					Log.trace("TabList", "Failed to fetch server ping " + e.getClass().getName() + " " + e.getMessage());
				}

				if (tps != -1) {
					NetherBoardScoreboard.getInstance().setGlobalLine(13, ChatColor.GOLD + "TPS: " + TextUtils.formatTps(tps));
				} else {
					NetherBoardScoreboard.getInstance().setGlobalLine(13, ChatColor.GOLD + "TPS: " + ChatColor.AQUA + "--");
				}

				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					int ping = NovaCore.getInstance().getVersionIndependentUtils().getPlayerPing(player);
					NetherBoardScoreboard.getInstance().setPlayerLine(12, player, ChatColor.GOLD + "Ping: " + TextUtils.formatPing(ping));
				}
			}
		}, 20L);
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