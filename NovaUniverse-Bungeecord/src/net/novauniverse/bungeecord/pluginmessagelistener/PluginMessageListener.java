package net.novauniverse.bungeecord.pluginmessagelistener;

import java.util.UUID;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.novauniverse.bungeecord.NovaUniverseBungeecord;
import net.novauniverse.commons.NovaUniverseCommons;
import net.novauniverse.commons.network.server.NovaServerType;
import net.zeeraa.novacore.commons.NovaCommons;
import net.zeeraa.novacore.commons.log.Log;

public class PluginMessageListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPluginMessage(PluginMessageEvent e) {
		if (e.getTag().equalsIgnoreCase("NovaUniverse")) {
			e.setCancelled(true);
			if (e.getSender() instanceof ProxiedPlayer) {
				Log.warn("PluginMessageListener", "Illegal sender for NovaUniverse plugin message: " + e.getSender().toString());
				return;
			}

			Log.trace("Received message on channel " + e.getTag());

			ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
			String subChannel = in.readUTF();

			switch (subChannel) {
			case "ServerFinder":
				String command = in.readUTF();

				switch (command) {
				case "Find":
					UUID uuid = UUID.fromString(in.readUTF());
					NovaServerType type = NovaUniverseBungeecord.getInstance().getNetworkManager().getServerTypeByName(in.readUTF());

					if (type == null) {
						NovaCommons.getAbstractPlayerMessageSender().trySendMessage(uuid, ChatColor.DARK_RED + "ERR:MISSING_SERVER_TYPE");
						return;
					}
					NovaUniverseCommons.getServerFinder().joinServerType(uuid, type);
					break;

				default:
					Log.warn("PluginMessageListener", "NovaUniverse message::Bad command for server finder: " + command);
					break;
				}

				break;

			default:
				Log.warn("PluginMessageListener", "NovaUniverse message::Bad sub channel: " + subChannel);
				return;
			}
		}
	}
}