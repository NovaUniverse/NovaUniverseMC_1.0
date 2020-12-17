package net.novauniverse.lobby.messages;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.novauniverse.lobby.NovaUniverseLobby;
import net.novauniverse.lobby.misc.PlayerMessages;
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

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		e.setQuitMessage(PlayerMessages.getLeaveMessage(e.getPlayer()));
	}
}