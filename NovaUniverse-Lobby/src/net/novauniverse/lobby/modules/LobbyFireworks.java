package net.novauniverse.lobby.modules;

import java.util.Random;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import net.novauniverse.commons.database.config.ConfigValueFetcher;
import net.novauniverse.lobby.NovaUniverseLobby;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.RandomGenerator;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.utils.LocationUtils;
import net.zeeraa.novacore.spigot.utils.RandomFireworkEffect;
import net.zeeraa.novacore.spigot.utils.VectorArea;

public class LobbyFireworks extends NovaModule {
	private boolean fireworksEnabled;
	private Task checkTask;
	private Task fireworkTask;

	private Random random;

	private VectorArea area;

	@Override
	public String getName() {
		return "LobbyFireworks";
	}

	@Override
	public void onLoad() {
		area = new VectorArea(95, 64, -60, -88, 64, 135);
		fireworksEnabled = false;
		random = new Random();
		checkTask = new SimpleTask(NovaUniverseLobby.getInstance(), new Runnable() {
			@Override
			public void run() {
				try {
					String value = ConfigValueFetcher.get("lobby_fireworks");

					fireworksEnabled = value.equalsIgnoreCase("1");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 200L);

		fireworkTask = new SimpleTask(NovaUniverseLobby.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (fireworksEnabled) {
					for (int i = 0; i < 10; i++) {
						Location location = LocationUtils.getLocation(NovaUniverseLobby.getInstance().getWorld(), area.getRandomVectorWithin(random));

						FireworkEffect effect = RandomFireworkEffect.randomFireworkEffect(random);

						Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
						FireworkMeta meta = firework.getFireworkMeta();

						meta.addEffect(effect);
						meta.setPower(RandomGenerator.generate(1, 3, random));

						firework.setFireworkMeta(meta);
					}
				}
			}
		}, 100L);
	}

	@Override
	public void onEnable() throws Exception {
		checkTask.start();
		fireworkTask.start();
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(checkTask);
		Task.tryStopTask(fireworkTask);
	}
}