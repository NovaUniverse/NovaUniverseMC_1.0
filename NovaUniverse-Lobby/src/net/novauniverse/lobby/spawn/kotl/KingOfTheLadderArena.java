package net.novauniverse.lobby.spawn.kotl;

import org.bukkit.Location;

public class KingOfTheLadderArena {
	private Location centerLocation;
	private Location respawnLocation;
	private double radius;
	private double minY;
	
	public KingOfTheLadderArena(Location centerLocation, Location respawnLocation, double radius, double minY) {
		this.centerLocation = centerLocation;
		this.respawnLocation = respawnLocation;
		this.radius = radius;
		this.minY = minY;
	}
	
	public Location getCenterLocation() {
		return centerLocation;
	}
	
	public Location getRespawnLocation() {
		return respawnLocation;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public double getMinY() {
		return minY;
	}
}