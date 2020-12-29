package net.novauniverse.main.commands;

import java.sql.SQLException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class ReloadNetworkManagerCommand extends NovaCommand {
	public ReloadNetworkManagerCommand() {
		super("reloadnetworkmanager", NovaMain.getInstance());

		setDescription("Reload the servers in network manager");
		setAllowedSenders(AllowedSenders.ALL);
		setPermission("novauniverse.command.reloadnetworkmanager");
		setPermissionDefaultValue(PermissionDefault.OP);
		addHelpSubCommand();
		setEmptyTabMode(true);
		setAliases(generateAliasList("nmrl"));
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		try {
			NovaMain.getInstance().getNetworkManager().update(true);
			sender.sendMessage(ChatColor.GREEN + "ok");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage(ChatColor.DARK_RED + e.getClass().getName() + " " + e.getMessage());
		}
		return false;
	}
}