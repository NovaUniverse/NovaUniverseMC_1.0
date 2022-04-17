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

	private boolean switching;
	private int colorAnimationIndex = 0;
	private ChatColor[] colorAnimation = { ChatColor.AQUA, ChatColor.LIGHT_PURPLE, ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN };

	public TabList() {
		super("NovaUniverse.TabList");
	}

	@Override
	public void onLoad() {
		switching = false;
		updateTask = new SimpleTask(NovaMain.getInstance(), new Runnable() {
			@Override
			public void run() {
				switching = !switching;
				colorAnimationIndex++;

				if (colorAnimationIndex >= colorAnimation.length) {
					colorAnimationIndex = 0;
				}

				double tps = -1;
				try {
					tps = NovaCore.getInstance().getVersionIndependentUtils().getRecentTps()[0];
				} catch (Exception e) {
					Log.trace("TabList", "Failed to fetch server ping " + e.getClass().getName() + " " + e.getMessage());
				}
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					String header = "";
					String footer = "";

					header += colorAnimation[colorAnimationIndex] + "" + ChatColor.BOLD + " NovaUniverse " + ChatColor.YELLOW + "" + ChatColor.BOLD + NovaMain.getInstance().getServerType().getDisplayName();

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
		}, 7L);
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