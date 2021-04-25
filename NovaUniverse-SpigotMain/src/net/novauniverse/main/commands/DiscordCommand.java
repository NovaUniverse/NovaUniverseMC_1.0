package net.novauniverse.main.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import me.rayzr522.jsonmessage.JSONMessage;
import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class DiscordCommand extends NovaCommand {

	public DiscordCommand() {
		super("discord", NovaMain.getInstance());

		setAllowedSenders(AllowedSenders.PLAYERS);
		setPermission("novauniverse.command.discord");
		setPermissionDefaultValue(PermissionDefault.TRUE);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		JSONMessage.create("Click here to join our discord server").color(ChatColor.GOLD).style(ChatColor.BOLD).openURL("https://discord.gg/4gZSVJ7").send((Player) sender);
		return true;
	}
}