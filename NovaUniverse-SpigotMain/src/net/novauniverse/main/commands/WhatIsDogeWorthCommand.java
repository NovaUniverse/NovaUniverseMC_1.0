package net.novauniverse.main.commands;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.commons.async.AsyncManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class WhatIsDogeWorthCommand extends NovaCommand {
	public static final String API_URL = "https://novauniverse.net/api/private/dogecoin_price/";

	public WhatIsDogeWorthCommand() {
		super("whatisdogeworth", NovaMain.getInstance());
		setAliases(generateAliasList("whatisdogecoinworth"));
		setAllowedSenders(AllowedSenders.ALL);
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setEmptyTabMode(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		sender.sendMessage(ChatColor.GREEN + "Checking price...");

		AsyncManager.runAsync(new Runnable() {
			@Override
			public void run() {
				try {
					URL url = new URL(API_URL);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setRequestProperty("User-Agent", "Mozilla/5.0");
					connection.setRequestProperty("accept", "application/json");

					connection.getResponseCode();

					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();

					JSONObject json = new JSONObject(response.toString());

					final String price = json.getString("price");
					final String priceBase = json.getString("price_base");
					final String exchange = json.getString("exchange");

					AsyncManager.runSync(new BukkitRunnable() {
						@Override
						public void run() {
							sender.sendMessage(ChatColor.GOLD + "Dogecoin is worth " + ChatColor.AQUA + price + " " + priceBase + ChatColor.GOLD + ". Price data found on " + exchange);
						}
					});
				} catch (Exception e) {
					Log.error("WhatIsDogeWorthCommand", "Failed to fetch dogecoin price. " + e.getClass().getName() + " " + e.getMessage());
					e.printStackTrace();
					AsyncManager.runSync(new BukkitRunnable() {
						@Override
						public void run() {
							sender.sendMessage(ChatColor.RED + "Failed to fetch price. Check that https://novauniverse.net is up");
						}
					});
				}
			}
		});
		return true;
	}
}