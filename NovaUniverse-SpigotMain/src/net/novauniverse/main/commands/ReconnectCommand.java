package net.novauniverse.main.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.scheduler.BukkitRunnable;

import net.novauniverse.commons.NovaUniverseCommons;
import net.novauniverse.commons.network.server.NovaServer;
import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.commons.NovaCommons;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import net.zeeraa.novacore.spigot.utils.BungeecordUtils;

public class ReconnectCommand extends NovaCommand {
	public ReconnectCommand() {
		super("reconnect", NovaMain.getInstance());

		setAllowedSenders(AllowedSenders.PLAYERS);
		setPermission("novamain.command.reconnect");
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setDescription("Reconnect to the last game");
		setEmptyTabMode(true);
		setUsage("/reconnect");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Player player = (Player) sender;

		String sql = "SELECT reconnect_server FROM players WHERE uuid = ?";
		UUID uuid = player.getUniqueId();

		// Start async code execution
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					PreparedStatement ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);

					ps.setString(1, uuid.toString());

					ResultSet rs = ps.executeQuery();

					if (rs.next()) {
						int serverId = rs.getInt("reconnect_server");

						if (serverId == 0) {
							sendSyncMessage(uuid, ChatColor.RED + "No server to reconnect to");
						} else {
							// Start sync code execution
							new BukkitRunnable() {
								@Override
								public void run() {
									NovaServer server = NovaMain.getInstance().getNetworkManager().getServerById(serverId);

									if (server == null) {
										sendMessage(uuid, ChatColor.RED + "No server to reconnect to");
										return;
									}

									sendMessage(uuid, ChatColor.GOLD + "Sending you to " + ChatColor.AQUA + server.getName());

									BungeecordUtils.sendToServer(uuid, server.getName());
								}
							}.runTask(NovaMain.getInstance());
						}
					} else {
						sendSyncMessage(uuid, ChatColor.RED + "No server to reconnect to");
					}

				} catch (Exception e) {
					sendSyncMessage(uuid, ChatColor.DARK_RED + "Failed to fetch last server! Caused by: " + e.getClass().getName());
				}
			}
		}.runTaskAsynchronously(NovaMain.getInstance());

		return true;
	}

	private void sendSyncMessage(UUID target, String message) {
		new BukkitRunnable() {
			@Override
			public void run() {
				NovaCommons.getAbstractPlayerMessageSender().trySendMessage(target, message);
			}
		}.runTask(NovaMain.getInstance());
	}

	public void sendMessage(UUID target, String message) {
		Player player = Bukkit.getServer().getPlayer(target);

		if (player != null) {
			if (player.isOnline()) {
				player.sendMessage(message);
			}
		}
	}
}