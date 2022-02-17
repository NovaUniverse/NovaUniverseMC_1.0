package net.novauniverse.main.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.commons.network.server.NovaServer;
import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class ShowServersCommand extends NovaCommand {
	public ShowServersCommand() {
		super("showservers", NovaMain.getInstance());

		setPermission("novauniverse.command.showservers");
		setPermissionDefaultValue(PermissionDefault.OP);

		setAllowedSenders(AllowedSenders.PLAYERS);
		setDescription("Show loaded servers");
		setEmptyTabMode(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if(NovaMain.getInstance().isSafeMode() && !(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(ChatColor.RED + "This server is running in safe mode. All administrative commands are disabled for players");
			return false;
		}
		
		String result = ChatColor.AQUA + "" + NovaMain.getInstance().getNetworkManager().getServers().size() + ChatColor.GOLD + " servers loaded:\n";

		for (NovaServer server : NovaMain.getInstance().getNetworkManager().getServers()) {
			result += ChatColor.WHITE + "(" + ChatColor.AQUA + "" + server.getId() + ChatColor.GOLD + " : " + ChatColor.AQUA + server.getName() + ChatColor.GOLD + " | cat: " + ChatColor.AQUA + server.getServerType().getName() + ChatColor.WHITE + ") ";
		}

		sender.sendMessage(result);
		return true;
	}
}