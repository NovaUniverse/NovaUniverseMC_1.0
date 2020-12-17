package net.novauniverse.main.modules;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import net.zeeraa.novacore.spigot.module.NovaModule;

public class NoEnderPearlDamage extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "NoEnderPearlDamage";
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if (e.getCause() == TeleportCause.ENDER_PEARL) {
			Player p = e.getPlayer();
			e.setCancelled(true);
			p.setNoDamageTicks(1);
			p.teleport(e.getTo());
		}
	}
}