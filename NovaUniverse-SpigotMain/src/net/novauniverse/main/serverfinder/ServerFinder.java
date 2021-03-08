package net.novauniverse.main.serverfinder;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.novauniverse.commons.abstraction.AbstractServerFinder;
import net.novauniverse.commons.network.server.NovaServerType;
import net.novauniverse.main.NovaMain;

public class ServerFinder implements AbstractServerFinder {
	@Override
	public void joinServerType(UUID player, NovaServerType type) {
		Player bukkitPlayer = Bukkit.getServer().getPlayer(player);
		if (bukkitPlayer != null) {
			if (bukkitPlayer.isOnline()) {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("ServerFinder");
				out.writeUTF("Find");
				out.writeUTF(player.toString());
				out.writeUTF(type.getName());

				bukkitPlayer.sendPluginMessage(NovaMain.getInstance(), "NovaUniverse", out.toByteArray());
			}
		}
	}
}