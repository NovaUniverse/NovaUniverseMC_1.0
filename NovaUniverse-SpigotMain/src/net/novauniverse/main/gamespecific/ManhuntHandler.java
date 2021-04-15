package net.novauniverse.main.gamespecific;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import net.novauniverse.games.manhunt.v1.game.team.ManhuntTeam;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class ManhuntHandler extends NovaModule implements Listener {
	private Task updateTask;

	public static final int TEAM_LINE = 1;

	@Override
	public String getName() {
		return "ManhuntHandler";
	}

	@Override
	public void onLoad() {
		updateTask = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				if (GameManager.getInstance().hasGame()) {
					if (GameManager.getInstance().getActiveGame().hasStarted()) {
						for(Player player : Bukkit.getServer().getOnlinePlayers()) {
							String team = "";
							
							ManhuntTeam manhuntTeam = (ManhuntTeam)TeamManager.getTeamManager().getPlayerTeam(player);
							
							if(manhuntTeam != null) {
								team = manhuntTeam.getTeamColor() + "" + ChatColor.BOLD + manhuntTeam.getRole().getDisplayName();
							}
							
							NetherBoardScoreboard.getInstance().setPlayerLine(TEAM_LINE, player, team);
						}
					}
				}
			}
		}, 5L);
	}

	@Override
	public void onEnable() throws Exception {
		updateTask.start();
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(updateTask);
	}
}