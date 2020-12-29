package net.novauniverse.main.commands;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;

import me.kaimu.hastebin.Hastebin;
import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import net.zeeraa.novacore.spigot.utils.BukkitSerailization;

public class Base64DumpItemCommand extends NovaCommand {

	public Base64DumpItemCommand() {
		super("base64dumpitem", NovaMain.getInstance());

		setAllowedSenders(AllowedSenders.PLAYERS);
		setEmptyTabMode(true);

		setPermission("novauniverse.command.base64dump");
		setPermissionDefaultValue(PermissionDefault.OP);

	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Player player = (Player) sender;

		ItemStack item = NovaCore.getInstance().getVersionIndependentUtils().getItemInMainHand(player);

		if (item == null) {
			player.sendMessage(ChatColor.RED + "Please hold an item in your main hand");
			return false;
		}

		String base64;
		try {
			base64 = BukkitSerailization.itemStackToBase64(item);
		} catch (IOException e) {
			player.sendMessage(ChatColor.DARK_RED + e.getMessage());
			e.printStackTrace();
			return false;
		}

		Hastebin hastebin = new Hastebin();

		try {
			String url = hastebin.post(base64, true);

			player.sendMessage(ChatColor.GREEN + url);
		} catch (IOException e) {
			player.sendMessage(ChatColor.DARK_RED + e.getMessage());
			e.printStackTrace();
			return false;
		}

		return true;
	}
}