package net.novauniverse.main.team.solo;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class SoloTeamManager extends TeamManager implements Listener {
	public SoloTeamManager(int teamCount) {
		for (int i = 0; i < teamCount; i++) {
			SoloTeam team = new SoloTeam();

			teams.add(team);
		}
	}

	@Override
	public boolean requireTeamToJoin(Player player) {
		return true;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		for (Team team : teams) {
			if (team.getMembers().contains(e.getPlayer().getUniqueId())) {
				return;
			}
		}

		for (Team team : teams) {
			if (team.getMembers().size() == 0) {
				team.getMembers().add(e.getPlayer().getUniqueId());
				e.getPlayer().sendMessage(LanguageManager.getString(e.getPlayer().getUniqueId(), "novauniverse.team.solo.added"));
				return;
			}
		}

		e.getPlayer().sendMessage(LanguageManager.getString(e.getPlayer().getUniqueId(), "novauniverse.team.no_team_avaliable"));
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		for (Team team : teams) {
			if (team.getMembers().contains(e.getPlayer().getUniqueId())) {
				team.getMembers().remove(e.getPlayer().getUniqueId());
			}
		}
	}
}