package net.novauniverse.lobby.spawn;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import net.novauniverse.lobby.NovaUniverseLobby;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;

public class NovaUniverseSpawn extends NovaModule implements Listener {
	private SimpleTask task;

	@Override
	public String getName() {
		return "NovaUniverseSpawn";
	}

	@Override
	public void onLoad() {
		this.task = null;
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStopTask(task);
		task = new SimpleTask(NovaUniverseLobby.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (hasSpawnLocation()) {
					for (Player player : Bukkit.getServer().getOnlinePlayers()) {
						player.setFoodLevel(20);
						player.setSaturation(20);
						
						if (player.getLocation().getWorld() != NovaUniverseLobby.getInstance().getSpawnLocation().getWorld()) {
							player.teleport(NovaUniverseLobby.getInstance().getSpawnLocation());
						}
						
						if (player.getLocation().getY() < -3) {
							player.teleport(NovaUniverseLobby.getInstance().getSpawnLocation());
							player.setFallDistance(0);
						}
					}
				}
			}
		}, 5L, 5L);
		task.start();
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (hasSpawnLocation()) {
			e.getPlayer().teleport(NovaUniverseLobby.getInstance().getSpawnLocation());
			
			e.getPlayer().setGameMode(GameMode.SURVIVAL);
			PlayerUtils.clearPlayerInventory(e.getPlayer());
			PlayerUtils.clearPotionEffects(e.getPlayer());
			PlayerUtils.resetPlayerXP(e.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		if (hasSpawnLocation()) {
			e.setRespawnLocation(NovaUniverseLobby.getInstance().getSpawnLocation());
		}
	}

	public boolean hasSpawnLocation() {
		return NovaUniverseLobby.getInstance().getSpawnLocation() != null;
	}
}