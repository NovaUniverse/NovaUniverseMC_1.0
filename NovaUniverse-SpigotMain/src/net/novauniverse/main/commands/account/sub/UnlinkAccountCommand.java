package net.novauniverse.main.commands.account.sub;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.commons.NovaUniverseCommons;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class UnlinkAccountCommand extends NovaSubCommand {
	public UnlinkAccountCommand() {
		super("unlink");

		setAllowedSenders(AllowedSenders.PLAYERS);
		setPermission("novauniverse.command.account");
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setDescription("Unlink your minecraft account from your novauniverse account");

		setFilterAutocomplete(true);
		setEmptyTabMode(true);
		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		try {
			Player player = (Player) sender;

			String sql;
			PreparedStatement ps;
			ResultSet rs;

			sql = "SELECT id FROM players WHERE uuid = ?";
			ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);
			ps.setString(1, player.getUniqueId().toString());

			rs = ps.executeQuery();

			int playerId = -1;

			if (rs.next()) {
				playerId = rs.getInt("id");
			}

			rs.close();
			ps.close();

			if (playerId == -1) {
				sender.sendMessage(ChatColor.DARK_RED + "Failed to fetch player id");
				return true;
			}

			int accountId = -1;

			sql = "SELECT id FROM accounts WHERE player_id = ?";
			ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);
			ps.setInt(1, playerId);
			
			rs = ps.executeQuery();

			if (rs.next()) {
				accountId = rs.getInt("id");
			}

			rs.close();
			ps.close();

			if (accountId == -1) {
				player.sendMessage(ChatColor.GREEN + "You are not linked to any novauniverse account");
				return true;
			}

			sql = "UPDATE accounts SET minecraft_link_code = null, player_id = null WHERE id = ?";
			ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);
			ps.setInt(1, accountId);
			if (ps.executeUpdate() > 0) {
				sender.sendMessage(ChatColor.GREEN + "Your minecraft account has been unlinked from your novauniverse account");
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Error: no change");
			}

			ps.close();
		} catch (SQLException e) {
			sender.sendMessage(ChatColor.DARK_RED + "Failed to unlink account. " + e.getClass().getName());
			e.printStackTrace();
		}

		return true;
	}
}