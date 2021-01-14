package net.novauniverse.main.modules;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.novauniverse.commons.NovaUniverseCommons;
import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameEndEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameStartEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.PlayerEliminatedEvent;

public class NovaSetReconnectServer extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "NovaSetReconnectServer";
	}

	@Override
	public void onDisable() throws Exception {
		try {
			clearAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void clearAll() throws SQLException {
		int serverId = NovaMain.getInstance().getServerId();

		String sql = "UPDATE players SET reconnect_server = null WHERE reconnect_server = ?";
		PreparedStatement ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);

		ps.setInt(1, serverId);

		ps.executeUpdate();
		ps.close();
	}

	public void setPlayerServer(UUID player, Integer serverId) throws SQLException {
		String sql = "UPDATE players SET reconnect_server = ? WHERE uuid = ?";
		PreparedStatement ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);

		if (serverId == null) {
			ps.setNull(1, Types.INTEGER);
		} else {
			ps.setInt(1, serverId);
		}

		ps.setString(2, player.toString());

		ps.executeUpdate();
		ps.close();
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (GameManager.getInstance().isEnabled()) {
			if (GameManager.getInstance().hasGame()) {
				return;
			}
		}

		try {
			setPlayerServer(e.getPlayer().getUniqueId(), NovaMain.getInstance().getServerId());
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onGameStart(GameStartEvent e) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			try {
				setPlayerServer(player.getUniqueId(), NovaMain.getInstance().getServerId());
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onGameEnd(GameEndEvent e) {
		try {
			clearAll();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerEliminated(PlayerEliminatedEvent e) {
		try {
			setPlayerServer(e.getPlayer().getUniqueId(), null);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}