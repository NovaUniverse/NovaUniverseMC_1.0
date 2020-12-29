package net.novauniverse.main.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class TabList extends NovaModule {
	private Task updateTask;

	@Override
	public String getName() {
		return "TabList";
	}

	@Override
	public void onLoad() {
		updateTask = new SimpleTask(NovaMain.getInstance(), new Runnable() {
			@Override
			public void run() {
				double tps = -1;
				try {
					tps = NovaCore.getInstance().getVersionIndependentUtils().getRecentTps()[0];
				} catch (Exception e) {
					Log.trace("TabList", "Failed to fetch server ping " + e.getClass().getName() + " " + e.getMessage());
				}
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					String header = "";
					String footer = "";

					int ping = NovaCore.getInstance().getVersionIndependentUtils().getPlayerPing(player);

					footer += ChatColor.AQUA + "-=[ ";
					footer += ChatColor.GOLD + "Ping: " + net.novauniverse.commons.utils.TextUtils.formatPing(ping);

					if (tps != -1) {
						footer += ChatColor.GOLD + " TPS: " + net.novauniverse.commons.utils.TextUtils.formatTps(tps);
					}

					footer += ChatColor.AQUA + " ]=-";

					NovaCore.getInstance().getVersionIndependentUtils().sendTabList(player, header, footer);
				}
			}
		}, 10);
	}

	@Override
	public void onEnable() throws Exception {
		updateTask.start();
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(updateTask);
	}

}
