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

public class LinkAccountCommand extends NovaSubCommand {
	public LinkAccountCommand() {
		super("link");

		setAllowedSenders(AllowedSenders.PLAYERS);
		setPermission("novauniverse.command.account");
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setDescription("Link your minecraft account to your novauniverse account");

		setFilterAutocomplete(true);
		setEmptyTabMode(true);
		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Missing link code. Visit novauniverse.net to get your link code");
		} else {
			if (args[0].length() != 64) {
				sender.sendMessage(ChatColor.RED + "Invalid link code. Visit novauniverse.net to get your link code");
			} else {
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

					int existingAccountId = -1;

					sql = "SELECT id FROM accounts WHERE player_id = ?";
					ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);
					ps.setInt(1, playerId);
					
					rs = ps.executeQuery();
					
					if (rs.next()) {
						existingAccountId = rs.getInt("id");
					}

					rs.close();
					ps.close();

					if (existingAccountId != -1) {
						player.sendMessage(ChatColor.GREEN + "You are already linked to an account. Use /account unlink to unlink");
						return true;
					}

					int targetAccountId = -1;

					sql = "SELECT id FROM accounts WHERE minecraft_link_code = ? AND player_id = null";
					ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);
					ps.setString(1, args[0]);
					
					rs = ps.executeQuery();

					if (rs.next()) {
						targetAccountId = rs.getInt("id");
					}

					rs.close();
					ps.close();

					if (targetAccountId != -1) {
						player.sendMessage(ChatColor.GREEN + "The link code is either invalid or and account has already been linked to that novauniverse account. Visit novauniverse.net to manage your account");
						return true;
					}

					sql = "UPDATE accounts SET player_id = ? WHERE id = ?";
					ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);
					ps.setInt(1, playerId);
					ps.setInt(2, targetAccountId);

					if (ps.executeUpdate() > 0) {
						sender.sendMessage(ChatColor.GREEN + "Your minecraft account has been linked to your novauniverse account");
					} else {
						sender.sendMessage(ChatColor.DARK_RED + "Error: no change");
					}

					ps.close();
				} catch (SQLException e) {
					sender.sendMessage(ChatColor.DARK_RED + "Failed to link account. " + e.getClass().getName());
					e.printStackTrace();
				}
			}
		}
		return true;
	}
}