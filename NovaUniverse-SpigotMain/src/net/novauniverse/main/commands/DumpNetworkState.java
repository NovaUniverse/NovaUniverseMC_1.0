package net.novauniverse.main.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.commons.hastebin.ZeeraasHastebin;
import net.novauniverse.commons.network.server.NovaServer;
import net.novauniverse.commons.network.server.NovaServerType;
import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.commons.async.AsyncManager;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class DumpNetworkState extends NovaCommand {

	public DumpNetworkState() {
		super("dumpnetworkstate", NovaMain.getInstance());

		setAllowedSenders(AllowedSenders.PLAYERS);
		setEmptyTabMode(true);

		setPermission("novauniverse.command.dumpnetworkstate");
		setPermissionDefaultValue(PermissionDefault.OP);

	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Player player = (Player) sender;

		String data = "-=-=-= NovaUniverse Network dump =-=-=-\n";

		for (NovaServerType types : NovaMain.getInstance().getNetworkManager().getServerTypes()) {
			data += "--- Server type ---\n";
			data += "Name: " + types.getName() + "\n";
			data += "Display name: " + types.getDisplayName() + "\n";
			data += "Lore: " + types.getLore() + "\n";
			data += "Naming scheme: " + types.getServerNamingScheme() + "\n";
			data += "Target player count: " + types.getTargetPlayerCount() + "\n";
			data += "Soft player limit: " + types.getSoftPlayerLimit() + "\n";
			data += "Hard player limit: " + types.getHardPlayerLimit() + "\n";
			data += "Return type: " + types.getReturnToServerType().getName() + "\n";
			data += "Player count: " + types.getPlayerCount() + "\n";
			data += "\n";
		}

		for (NovaServer server : NovaMain.getInstance().getNetworkManager().getServers()) {
			data += "--- Server ---\n";
			data += "Type: " + server.getServerType() + "\n";
			data += "Host: " + server.getHost() + "\n";
			data += "Port: " + server.getPort() + "\n";
			data += "Id: " + server.getId() + "\n";
			data += "Name: " + server.getName() + "\n";
			data += "\n";
		}

		final String finalData = data;

		ZeeraasHastebin hastebin = new ZeeraasHastebin();

		AsyncManager.runAsync(new Runnable() {
			@Override
			public void run() {
				String url = null;
				String error = null;
				try {
					url = hastebin.post(finalData, true);
				} catch (Exception e) {
					error = e.getMessage();
					e.printStackTrace();
				}

				final String finalUrl = url;
				final String finalError = error;

				AsyncManager.runSync(new Runnable() {
					@Override
					public void run() {
						if (finalUrl != null) {
							player.sendMessage(ChatColor.GREEN + finalUrl);
						} else {
							player.sendMessage(ChatColor.RED + "Failed to post data. " + finalError);
						}
					}
				});
			}
		});

		return true;
	}
}