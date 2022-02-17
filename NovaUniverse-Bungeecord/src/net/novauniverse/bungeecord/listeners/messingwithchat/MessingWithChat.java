package net.novauniverse.bungeecord.listeners.messingwithchat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.zeeraa.novacore.commons.async.AsyncManager;
import net.zeeraa.novacore.commons.log.Log;

public class MessingWithChat implements Listener {
	private static final String API_ENDPOINT = "https://nameless-frog-4917.zeeraa.net/";
	private static final String[] THE_CHOSEN_ONES = { "8ec663e7-9a3d-4014-9bc6-a6915e629a56", "22a9eca8-2221-4bc9-b463-de0f3a0cc652" };
	private Map<String, String> messingWithPeopleList;

	public MessingWithChat() {
		messingWithPeopleList = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(ChatEvent e) {
		if (e.isCancelled()) {
			return;
		}

		if (e.getSender() instanceof ProxiedPlayer) {
			ProxiedPlayer sender = (ProxiedPlayer) e.getSender();
			if (e.getMessage().startsWith(".translator")) {
				boolean isTheChosenOne = false;
				for (String theAllPowerfullPlayer : THE_CHOSEN_ONES) {
					if (sender.getUniqueId().toString().equalsIgnoreCase(theAllPowerfullPlayer)) {
						isTheChosenOne = true;
					}
				}
				if (isTheChosenOne) {
					e.setCancelled(true);
					String[] parts = e.getMessage().split("\\s+");

					if (parts.length > 1) {
						String targetName = parts[1];

						if (targetName.equalsIgnoreCase("list")) {
							sender.sendMessage(new TextComponent(ChatColor.RED + "They are suffering:"));
							messingWithPeopleList.keySet().forEach(key -> {
								sender.sendMessage(new TextComponent(key + " " + messingWithPeopleList.get(key)));
							});
							sender.sendMessage(new TextComponent(ChatColor.RED + "End of pain"));
						} else {

							ProxiedPlayer target = null;
							for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
								if (proxiedPlayer.getName().equalsIgnoreCase(targetName)) {
									target = proxiedPlayer;
								}
							}

							if (target != null) {
								if (parts.length == 2) {
									messingWithPeopleList.remove(target.getUniqueId().toString());
									sender.sendMessage(new TextComponent(ChatColor.GREEN + "Curse removed"));
								} else {
									String curse = parts[2];
									messingWithPeopleList.put(target.getUniqueId().toString(), curse);
									sender.sendMessage(new TextComponent(ChatColor.GREEN + "It has been done"));
								}
							} else {
								sender.sendMessage(new TextComponent(ChatColor.RED + "Target not online"));
							}
						}
					} else {
						sender.sendMessage(new TextComponent(ChatColor.RED + "Provide target"));
					}
				}
			} else {
				if (messingWithPeopleList.containsKey(sender.getUniqueId().toString())) {
					e.setCancelled(true);
					String curse = messingWithPeopleList.get(sender.getUniqueId().toString());
					String message = e.getMessage();

					AsyncManager.runAsync(new Runnable() {
						@Override
						public void run() {
							String url = API_ENDPOINT + curse + "/translate";

							StringEntity myEntity = new StringEntity(message, ContentType.create("text/plain", "UTF-8"));

							CloseableHttpClient client = HttpClients.createDefault();
							HttpPost httpPost = new HttpPost(url);
							httpPost.setEntity(myEntity);
							try {
								CloseableHttpResponse response = client.execute(httpPost);

								String output = "";

								if (response.getStatusLine().getStatusCode() == 200) {
									HttpEntity entity = response.getEntity();
									String responseString = EntityUtils.toString(entity, "UTF-8");

									try {
									JSONObject json = new JSONObject(responseString);

									if (json.getBoolean("success")) {
										output = json.getString("result");
									} else {
										output = message;
										Log.warn("MessingWithChat", "Non success response while trying to translate using url " + url + ". Response: " + json.toString() + ". the player is " + sender.getName());
									}
									} catch(JSONException je) {
										Log.warn("MessingWithChat", "Invalid json: " + responseString);
									}
								} else {
									output = message;
									Log.warn("MessingWithChat", "Non 200 status code while trying to translate using url " + url + ". the player is " + sender.getName());
								}

								sender.chat(output);

								response.close();
							} catch (IOException e) {
								Log.error("MessingWithChat", e.getClass().getName() + " while trying to translate using url " + url + ". the player is " + sender.getName());
								e.printStackTrace();
							}

							try {
								client.close();
							} catch (IOException e) {
								Log.error("MessingWithChat", e.getClass().getName() + " while trying to close http client");
								e.printStackTrace();
							}
						}
					}, 1L);
				}
			}
		}
	}
}