package net.novauniverse.main.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.gamelobby.GameLobby;

public class SpectateGameCommand extends NovaCommand {
	public SpectateGameCommand() {
		super("spectategame", NovaMain.getInstance());

		setDescription("Join the game as a spectator instead of a player");
		setUsage("/spectategame");
		setPermission("novauniverse.command.spectategame");
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setAllowedSenders(AllowedSenders.PLAYERS);
		addHelpSubCommand();
		setEmptyTabMode(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Player player = (Player) sender;

		if (GameManager.getInstance().getActiveGame().hasStarted()) {
			sender.sendMessage(ChatColor.RED + "The game has already started");
		} else {
			if (GameLobby.getInstance().isEnabled()) {
				if (GameLobby.getInstance().getWaitingPlayers().contains(player.getUniqueId())) {
					GameLobby.getInstance().getWaitingPlayers().remove(player.getUniqueId());
				}
				sender.sendMessage(ChatColor.GREEN + "You will be a spectator this game. If you want to join the game as a player please reconnect");
			}
		}
		return true;
	}
}