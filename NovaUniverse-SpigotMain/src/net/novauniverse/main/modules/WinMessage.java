package net.novauniverse.main.modules;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.md_5.bungee.api.ChatColor;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.PlayerWinEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.TeamWinEvent;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.teams.Team;

public class WinMessage extends NovaModule implements Listener {
	public WinMessage() {
		super("NovaUniverse.WinMessage");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerWin(PlayerWinEvent e) {
		ChatColor color = ChatColor.AQUA;
		if (NovaCore.getInstance().getTeamManager() != null) {
			Team team = NovaCore.getInstance().getTeamManager().getPlayerTeam(e.getPlayer());

			if (team != null) {
				color = team.getTeamColor();
			}
		}

		// Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD +
		// "GAME OVER> " + ChatColor.GOLD + ChatColor.BOLD + "Winning player: " + color
		// + ChatColor.BOLD + e.getPlayer().getName());
		LanguageManager.broadcast("novauniverse.game.gameover.winner.player", color.toString(), e.getPlayer().getName());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onTeamWin(TeamWinEvent e) {
		ChatColor color = e.getTeam().getTeamColor();

		// Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD +
		// "GAME OVER> " + ChatColor.GOLD + ChatColor.BOLD + "Winning team: " + color +
		// ChatColor.BOLD + "Team " + ((MCFTeam) e.getTeam()).getTeamNumber());
		LanguageManager.broadcast("novauniverse.game.gameover.winner.team", color.toString(), e.getTeam().getDisplayName());
	}
}