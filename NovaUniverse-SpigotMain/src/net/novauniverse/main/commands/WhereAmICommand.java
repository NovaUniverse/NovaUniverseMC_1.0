package net.novauniverse.main.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class WhereAmICommand extends NovaCommand {
	public WhereAmICommand() {
		super("whereami", NovaMain.getInstance());

		setEmptyTabMode(true);
		setAllowedSenders(AllowedSenders.ALL);
		setPermission("novauniverse.command.whereami");
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setAliases(NovaCommand.generateAliasList("wai"));
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		sender.sendMessage(ChatColor.GOLD + "Playing on: " + ChatColor.AQUA + NovaMain.getInstance().getServerType().getDisplayName() + ChatColor.GRAY + " (" + NovaMain.getInstance().getServerType().getName() + ")" + ChatColor.GOLD + " Server id: " + ChatColor.AQUA + NovaMain.getInstance().getServerName());
		return true;
	}
}