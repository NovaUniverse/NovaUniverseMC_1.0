package net.novauniverse.lobby.spawn.kotl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.novauniverse.lobby.NovaUniverseLobby;
import net.novauniverse.lobby.misc.DoubleJump;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.utils.LocationUtils;

public class KingOfTheLadderManager extends NovaModule implements Listener {
	private static KingOfTheLadderManager instance;
	private List<KingOfTheLadderArena> arenas;

	private List<UUID> doubleJumpDisabled;

	private SimpleTask task;

	public KingOfTheLadderManager() {
		super("NovaUniverse.KingOfTheLadderManager");
	}

	public static KingOfTheLadderManager getInstance() {
		return instance;
	}

	@Override
	public void onLoad() {
		KingOfTheLadderManager.instance = this;
		arenas = new ArrayList<KingOfTheLadderArena>();
		doubleJumpDisabled = new ArrayList<UUID>();
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStopTask(task);
		task = new SimpleTask(NovaUniverseLobby.getInstance(), new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					KingOfTheLadderArena arena = getPlayerArena(player);

					if (doubleJumpDisabled.contains(player.getUniqueId())) {
						if (arena == null) {
							doubleJumpDisabled.remove(player.getUniqueId());
							DoubleJump.getInstance().getDisabledPlayers().remove(player.getUniqueId());
						}
					} else {
						if (arena != null) {
							doubleJumpDisabled.add(player.getUniqueId());
							DoubleJump.getInstance().getDisabledPlayers().add(player.getUniqueId());
						}
					}

					KingOfTheLadderArena arena2 = getPlayerArena(player, 10);

					if (arena2 != null) {
						if (player.getLocation().getY() < arena2.getMinY()) {
							player.teleport(arena2.getRespawnLocation());
						}
					}
				}
			}
		}, 2L, 2L);
		task.start();
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}

	public void loadArenas(File file) throws JSONException, IOException {
		JSONArray array = JSONFileUtils.readJSONArrayFromFile(file);

		arenas.clear();

		for (int i = 0; i < array.length(); i++) {
			JSONObject json = array.getJSONObject(i);

			Location location = LocationUtils.fromJSONObject(json.getJSONObject("location"));
			Location respawnLocation = LocationUtils.fromJSONObject(json.getJSONObject("respawn_location"));
			double radius = json.getDouble("radius");
			double minY = json.getDouble("min_y");

			arenas.add(new KingOfTheLadderArena(location, respawnLocation, radius, minY));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onPlayerDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			if (isInArena(e.getEntity())) {
				e.setCancelled(false);
				e.setDamage(0);
			}
		}
	}

	public boolean isInArena(Entity player) {
		return getPlayerArena(player) != null;
	}

	public KingOfTheLadderArena getPlayerArena(Entity player) {
		return this.getPlayerArena(player, 0);
	}

	public KingOfTheLadderArena getPlayerArena(Entity player, double extraDistance) {
		for (KingOfTheLadderArena arena : arenas) {
			if (arena.getCenterLocation().getWorld().equals(player.getLocation().getWorld())) {

				double x = player.getLocation().getBlockX() - arena.getCenterLocation().getX();
				double z = player.getLocation().getBlockZ() - arena.getCenterLocation().getZ();

				double distance = Math.hypot(x, z);

				if (distance <= (arena.getRadius() + extraDistance)) {
					return arena;
				}
			}
		}
		return null;
	}
}