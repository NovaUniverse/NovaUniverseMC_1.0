package net.novauniverse.main.modules.shutdownrequest;

import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;

public class ShutdownGameEngine {
	public static void shutdownEngine() {
		if (GameManager.getInstance().isEnabled()) {
			if (GameManager.getInstance().hasGame()) {
				if (GameManager.getInstance().getActiveGame().isRunning()) {
					GameManager.getInstance().getActiveGame().endGame(GameEndReason.SERVER_ENDED_GAME);
				}
			}
		}
	}
}