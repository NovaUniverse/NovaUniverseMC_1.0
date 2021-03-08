package net.novauniverse.main.gamespecific;

import org.bukkit.ChatColor;

import net.novauniverse.games.deathswap.NovaDeathSwap;
import net.novauniverse.games.deathswap.game.DeathSwap;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class DeathSwapHandler extends NovaModule {
	public static final int GENERATION_LINE = 2;
	private boolean worldGenerationShown;
	private Task task;

	@Override
	public String getName() {
		return "DeathSwapHandler";
	}

	@Override
	public void onLoad() {
		worldGenerationShown = false;
		task = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				try {
					DeathSwap deathSwap = NovaDeathSwap.getInstance().getGame();

					if (!deathSwap.getWorldPreGenerator().isFinished()) {
						worldGenerationShown = true;

						NetherBoardScoreboard.getInstance().setGlobalLine(DeathSwapHandler.GENERATION_LINE, ChatColor.GOLD + "Generating world: " + ChatColor.AQUA + "" + ((int) (deathSwap.getWorldPreGenerator().getProgressValue() * 100)) + "%");
					} else if (worldGenerationShown) {
						worldGenerationShown = false;

						NetherBoardScoreboard.getInstance().clearGlobalLine(DeathSwapHandler.GENERATION_LINE);
					}
				} catch (NoClassDefFoundError e) {
					Log.fatal("DeathSwapHandler", "Caught " + e.getClass().getName() + " in task. This probably means that DeathSwap is not installed on this server. This module will now disable due to this error");
					ModuleManager.disable(DeathSwapHandler.class);
				}
			}
		}, 5L);
	}

	@Override
	public void onEnable() throws Exception {
		task.start();
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}
}