package net.novauniverse.lobby.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import net.zeeraa.novacore.spigot.module.NovaModule;

public class DoubleJump extends NovaModule implements Listener {
	private static DoubleJump instance;
	
	public static DoubleJump getInstance() {
		return instance;
	}
	
	public DoubleJump() {
		super("NovaUniverse.DoubleJump");
	}

	private List<UUID> disabledPlayers;

	@Override
	public void onLoad() {
		DoubleJump.instance = this;
		disabledPlayers = new ArrayList<UUID>();
	}
	
	public List<UUID> getDisabledPlayers() {
		return disabledPlayers;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SURVIVAL) {
			player.setFlying(false);
			if (!disabledPlayers.contains(player.getUniqueId())) {
				player.setVelocity(player.getLocation().getDirection().multiply(1.5).setY(1));
				player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1F, 1F);
				Location pLocation = player.getLocation();
				pLocation.add(0.0, 1.5, 0.0);
				for (int i = 0; i <= 2; i++) {
					player.getWorld().playEffect(pLocation.clone().add(0, -1, 0), Effect.SMOKE, i);
				}
			}
			player.setAllowFlight(false);
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SURVIVAL) {
			if (!player.getAllowFlight()) {
				Location loc = player.getLocation();
				Block block = loc.getBlock().getRelative(BlockFace.DOWN);
				if (block.getType() != Material.AIR && block.getType().isSolid()) {
					if (!disabledPlayers.contains(player.getUniqueId())) {
						player.setAllowFlight(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (disabledPlayers.contains(e.getPlayer().getUniqueId())) {
			disabledPlayers.remove(e.getPlayer().getUniqueId());
		}
	}
}