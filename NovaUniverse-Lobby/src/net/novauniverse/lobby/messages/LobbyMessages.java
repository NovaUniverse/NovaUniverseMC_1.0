package net.novauniverse.lobby.messages;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.rayzr522.jsonmessage.JSONMessage;
import net.novauniverse.commons.NovaUniverseCommons;
import net.novauniverse.lobby.NovaUniverseLobby;
import net.novauniverse.lobby.misc.PlayerMessages;
import net.zeeraa.novacore.commons.async.AsyncManager;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class LobbyMessages extends NovaModule implements Listener {
	private static LobbyMessages instance;
	private SimpleTask task;

	@Override
	public String getName() {
		return "LobbyMessages";
	}

	public static LobbyMessages getInstance() {
		return instance;
	}

	@Override
	public void onLoad() {
		LobbyMessages.instance = this;
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStopTask(task);
		task = new SimpleTask(NovaUniverseLobby.getInstance(), new Runnable() {
			@Override
			public void run() {

			}
		}, 5L, 5L);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.setJoinMessage(PlayerMessages.getJoinMessage(e.getPlayer()));

		UUID uuid = e.getPlayer().getUniqueId();

		AsyncManager.runAsync(new Runnable() {
			@Override
			public void run() {
				String sql = "SELECT reconnect_server FROM players WHERE uuid = ?";

				try {
					PreparedStatement ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);

					ps.setString(1, uuid.toString());

					ResultSet rs = ps.executeQuery();

					if (rs.next()) {
						if (rs.getInt("reconnect_server") > 0) {
							AsyncManager.runSync(new Runnable() {
								@Override
								public void run() {
									Player pl = Bukkit.getServer().getPlayer(uuid);

									if (pl != null) {
										if (pl.isOnline()) {
											JSONMessage.create("A game is in progress!").color(ChatColor.GOLD).style(ChatColor.BOLD).send(pl);
											JSONMessage.create("Use /reconnect or click ").color(ChatColor.GOLD).style(ChatColor.BOLD).then("[Here]").color(ChatColor.GREEN).tooltip("Click to reconnect").runCommand("/reconnect").style(ChatColor.BOLD).then(" to reconnect").color(ChatColor.GOLD).style(ChatColor.BOLD).send(pl);
										}
									}
								}
							});
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		e.setQuitMessage(PlayerMessages.getLeaveMessage(e.getPlayer()));
	}
}