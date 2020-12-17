package net.novauniverse.lobby.spawn;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import net.zeeraa.novacore.spigot.module.NovaModule;

public class NovaUniverseSpawnProtection extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "NovaUniverseSpawnProtection";
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		if (shouldCancel(e.getPlayer())) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPlace(BlockBreakEvent e) {
		if (shouldCancel(e.getPlayer())) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			e.setCancelled(true);
		}
	}

	public boolean shouldCancel(Player player) {
		if (player != null) {
			if (player.getGameMode() != GameMode.CREATIVE) {
				return true;
			}
		}
		return false;
	}
}