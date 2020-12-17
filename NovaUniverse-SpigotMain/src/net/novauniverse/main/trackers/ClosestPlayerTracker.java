package net.novauniverse.main.trackers;

import org.bukkit.entity.Player;

import net.zeeraa.novacore.spigot.module.modules.compass.CompassTarget;
import net.zeeraa.novacore.spigot.module.modules.compass.CompassTrackerTarget;

public class ClosestPlayerTracker implements CompassTrackerTarget {
	@Override
	public CompassTarget getCompassTarget(Player player) {
		return null;
	}
}