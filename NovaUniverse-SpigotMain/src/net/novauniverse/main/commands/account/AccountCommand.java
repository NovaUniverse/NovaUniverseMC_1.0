package net.novauniverse.main.commands.account;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.main.NovaMain;
import net.novauniverse.main.commands.account.sub.LinkAccountCommand;
import net.novauniverse.main.commands.account.sub.UnlinkAccountCommand;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class AccountCommand extends NovaCommand {
	public AccountCommand() {
		super("account", NovaMain.getInstance());

		setAllowedSenders(AllowedSenders.PLAYERS);
		setPermission("novauniverse.command.account");
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setDescription("Command used to manage your account");

		setFilterAutocomplete(true);
		setEmptyTabMode(true);

		addSubCommand(new UnlinkAccountCommand());
		addSubCommand(new LinkAccountCommand());
		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		sender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.AQUA + "/account help " + ChatColor.GOLD + "for help");
		return true;
	}
}