package net.novauniverse.main.gamestarter;

import org.bukkit.event.Listener;

import net.zeeraa.novacore.spigot.module.modules.game.events.GameLoadedEvent;

public abstract class GameStarter implements Listener {
	/**
	 * Get the name of the {@link GameStarter}
	 * 
	 * @return name of the {@link GameStarter}
	 */
	public abstract String getName();

	/**
	 * Called when the {@link GameStarter} is enabled by the {@link GameLoadedEvent}
	 * <p>
	 * Called before the events are registered
	 */
	public abstract void onEnable();

	public abstract boolean shouldShowCountdown();

	public abstract long getTimeLeft();
}