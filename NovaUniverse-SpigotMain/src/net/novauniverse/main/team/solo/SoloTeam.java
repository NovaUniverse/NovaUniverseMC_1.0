package net.novauniverse.main.team.solo;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import net.zeeraa.novacore.spigot.teams.Team;

public class SoloTeam extends Team {
	@Override
	public ChatColor getTeamColor() {
		return ChatColor.AQUA;
	}

	@Override
	public String getDisplayName() {
		if (members.size() == 0) {
			return "Empty team";
		}
		
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(members.get(0));
		
		if(player == null) {
			return "Unknown team";
		}
		
		return player.getName();
	}
}