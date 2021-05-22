package net.novauniverse.main.modules;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import net.zeeraa.novacore.spigot.module.NovaModule;

public class PreventingAleksaFromBreakingTheServer extends NovaModule implements Listener {
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			if (((Player) e.getEntity()).getGameMode() == GameMode.SPECTATOR) {
				e.setCancelled(true);
			}
		}
	}

	@Override
	public String getName() {
		return "PreventingAleksaFromBreakingTheServer";
	}
}