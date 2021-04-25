package net.novauniverse.main.modules.utils;

import net.zeeraa.novacore.spigot.module.modules.game.GameManager;

public class NovaGameEngineInterface implements GameInterface {

	@Override
	public boolean hasStarted() {
		if(GameManager.getInstance().isEnabled()) {
			if(GameManager.getInstance().hasGame()) {
				return GameManager.getInstance().getActiveGame().hasStarted();
			}
		}
		return false;
	}

	@Override
	public boolean hasEnded() {
		if(GameManager.getInstance().isEnabled()) {
		if(GameManager.getInstance().hasGame()) {
			return GameManager.getInstance().getActiveGame().hasEnded();
		}
	}
		return false;
	}

	@Override
	public int getInGamePlayers() {
		if(GameManager.getInstance().isEnabled()) {
			if(GameManager.getInstance().hasGame()) {
				return GameManager.getInstance().getActiveGame().getPlayers().size();
			}
		}
		return 0;
	}
}