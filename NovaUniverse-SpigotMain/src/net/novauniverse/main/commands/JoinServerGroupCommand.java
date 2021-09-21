package net.novauniverse.main.commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.commons.network.server.NovaServerType;
import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class JoinServerGroupCommand extends NovaCommand {
	public JoinServerGroupCommand() {
		super("joinservergroup", NovaMain.getInstance());

		setPermission("novauniverse.command.joinservergroup");
		setPermissionDefaultValue(PermissionDefault.OP);

		setAllowedSenders(AllowedSenders.PLAYERS);
		setDescription("Join a server group");
		setFilterAutocomplete(true);
		setAliases(generateAliasList("jsg"));
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Please provide a server type");
			return false;
		}

		NovaServerType type = NovaMain.getInstance().getNetworkManager().getServerTypeByName(args[0].toLowerCase());

		if (type == null) {
			sender.sendMessage(ChatColor.RED + "Could not find server type " + args[0]);
			return false;
		}

		try {
			if (!NovaMain.getInstance().getNetworkManager().sendPlayerToServer(((Player) sender).getUniqueId(), type)) {
				sender.sendMessage(ChatColor.RED + "Could not find an available server of that type");
				return false;
			}
		} catch (SQLException e) {
			sender.sendMessage(ChatColor.DARK_RED + e.getClass().getName() + " " + e.getMessage());
			return false;
		}

		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		List<String> result = new ArrayList<String>();

		for (NovaServerType serverType : NovaMain.getInstance().getNetworkManager().getServerTypes()) {
			result.add(serverType.getName());
		}

		return result;
	}
}