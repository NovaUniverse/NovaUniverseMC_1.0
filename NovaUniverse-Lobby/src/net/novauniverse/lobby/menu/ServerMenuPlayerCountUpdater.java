package net.novauniverse.lobby.menu;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.novauniverse.commons.network.server.NovaServerType;
import net.novauniverse.lobby.NovaUniverseLobby;
import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class ServerMenuPlayerCountUpdater extends NovaModule {
	private Task updateTask;
	private Task networlkUpdateTask;

	public ServerMenuPlayerCountUpdater() {
		super("NovaUniverse.ServerMenuPlayerCountUpdater");
	}
	
	@Override
	public void onLoad() {
		updateTask = new SimpleTask(NovaUniverseLobby.getInstance(), new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					if (player.getOpenInventory() != null) {
						if (player.getOpenInventory().getTopInventory() != null) {
							if (player.getOpenInventory().getTopInventory().getHolder() instanceof ServerMenuHolder) {
								Inventory inventory = player.getOpenInventory().getTopInventory();
								ServerMenuHolder holder = (ServerMenuHolder) inventory.getHolder();

								for (NovaServerType serverType : NovaMain.getInstance().getNetworkManager().getServerTypes()) {
									if (holder.getServerTypeSlots().containsKey(serverType)) {
										int slot = holder.getServerTypeSlots().get(serverType);

										ItemStack item = inventory.getItem(slot);

										ItemMeta meta = item.getItemMeta();

										if (!meta.hasLore()) {
											continue;
										}

										List<String> lore = meta.getLore();

										lore.set(0, ChatColor.AQUA + "" + serverType.getPlayerCount() + ChatColor.GOLD + " Players online");

										meta.setLore(lore);

										item.setItemMeta(meta);
									}
								}
							}
						}
					}
				}
			}
		}, 10L);

		networlkUpdateTask = new SimpleTask(NovaUniverseLobby.getInstance(), new Runnable() {
			@Override
			public void run() {
				try {
					NovaMain.getInstance().getNetworkManager().updatePlayerCount();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 40L);
	}

	@Override
	public void onEnable() throws Exception {
		updateTask.start();
		networlkUpdateTask.start();
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(updateTask);
		Task.tryStopTask(networlkUpdateTask);
	}
}