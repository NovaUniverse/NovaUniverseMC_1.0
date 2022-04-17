package net.novauniverse.main.modules.shutdownrequest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.novauniverse.commons.NovaUniverseCommons;
import net.novauniverse.main.NovaMain;
import net.novauniverse.main.modules.GameEndManager;
import net.zeeraa.novacore.commons.async.AsyncManager;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class CheckShutdownRequest extends NovaModule {
	private Task task;

	public CheckShutdownRequest() {
		super("NovaUniverse.CheckShutdownRequest");
	}

	@Override
	public void onLoad() {
		this.task = new SimpleTask(NovaMain.getInstance(), new Runnable() {

			@Override
			public void run() {
				int serverId = NovaMain.getInstance().getServerId();
				AsyncManager.runAsync(new Runnable() {
					@Override
					public void run() {
						String sql = "SELECT request_shutdown FROM servers WHERE id = ?";
						try {
							PreparedStatement ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);

							ps.setInt(1, serverId);

							ResultSet rs = ps.executeQuery();

							if (rs.next()) {
								if (rs.getBoolean("request_shutdown")) {
									try {
										AsyncManager.runSync(new Runnable() {
											@Override
											public void run() {
												task.tryStop();

												NovaMain.getInstance().sendWebhookLog("Info", NovaMain.getInstance().getFullServerNameForLogs() + " received shutdown request from system");

												Bukkit.getServer().broadcastMessage(ChatColor.RED + "Shutdown request received");

												if (GameEndManager.getInstance().isEnabled()) {
													GameEndManager.getInstance().setPreventShutdown(true);
												}

												if (NovaCore.isNovaGameEngineEnabled()) {
													ShutdownGameEngine.shutdownEngine();
												}

												new BukkitRunnable() {
													@Override
													public void run() {
														for (Player player : Bukkit.getServer().getOnlinePlayers()) {
															NovaMain.getInstance().sendToLobby(player, true);
														}

														new BukkitRunnable() {
															@Override
															public void run() {
																for (Player player : Bukkit.getServer().getOnlinePlayers()) {
																	NovaMain.getInstance().sendToLobby(player, true);
																}

																new BukkitRunnable() {
																	@Override
																	public void run() {
																		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
																			player.kickPlayer(ChatColor.RED + "Failed to send you to the lobby. Please reconnect");
																		}

																		Bukkit.getServer().shutdown();
																	}
																}.runTaskLater(NovaMain.getInstance(), 60L);
															}
														}.runTaskLater(NovaMain.getInstance(), 60L);
													}
												}.runTaskLater(NovaMain.getInstance(), 40L);
											}
										});
									} catch (NoClassDefFoundError ex) {
										Bukkit.getServer().shutdown();
										Task.tryStopTask(task);
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		}, 100L);
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