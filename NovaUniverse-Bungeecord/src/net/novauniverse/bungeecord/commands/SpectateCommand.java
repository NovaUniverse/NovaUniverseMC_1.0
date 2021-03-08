package net.novauniverse.bungeecord.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.novauniverse.commons.NovaUniverseCommons;
import net.zeeraa.novacore.commons.NovaCommons;

public class SpectateCommand extends Command {
	public SpectateCommand() {
		super("spectate");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			if (args.length == 0) {
				sender.sendMessage(new TextComponent(ChatColor.RED + "Please provide a player to spectate"));
			} else {
				String sql = "CALL join_or_spectate(?)";

				try {
					PreparedStatement ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);

					ps.setString(1, args[0].toLowerCase());

					ResultSet rs = ps.executeQuery();

					if (rs.next()) {
						boolean isOnline = rs.getBoolean("is_online");
						String username = rs.getString("username");

						if (isOnline) {
							String server = rs.getString("server_name");

							if (server != null) {
								if (rs.getBoolean("allow_spectate")) {
									sender.sendMessage(new TextComponent(ChatColor.GOLD + "Joining " + server + "..."));
									NovaCommons.getPlatformIndependentBungeecordAPI().sendPlayerToServer(((ProxiedPlayer) sender).getUniqueId(), server);
								} else {
									sender.sendMessage(new TextComponent(ChatColor.RED + username + " is on a server that does not allow spectators"));
								}
							} else {
								sender.sendMessage(new TextComponent(ChatColor.RED + username + " is not on any server right now"));
							}
						} else {
							sender.sendMessage(new TextComponent(ChatColor.RED + username + " is not online"));
						}
					} else {
						sender.sendMessage(new TextComponent(ChatColor.RED + "Could not find player: " + args[0]));
					}

					ps.close();
					rs.close();
				} catch (Exception e) {
					sender.sendMessage(new TextComponent(ChatColor.DARK_RED + "Error: " + e.getClass().getName()));
					e.printStackTrace();
				}
			}
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "Only players can use this command"));
		}
	}
}