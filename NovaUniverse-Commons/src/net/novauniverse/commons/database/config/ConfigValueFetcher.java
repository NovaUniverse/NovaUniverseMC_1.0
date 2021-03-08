package net.novauniverse.commons.database.config;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.novauniverse.commons.NovaUniverseCommons;

public class ConfigValueFetcher {
	public static boolean hasKey(String key) throws SQLException {
		return ConfigValueFetcher.get(key) != null;
	}

	public static String get(String key) throws SQLException {
		String sql = "SELECT data_value FROM config WHERE data_key = ?";
		String result = null;

		PreparedStatement ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);

		ps.setString(1, key);

		ResultSet rs = ps.executeQuery();

		if (rs.next()) {
			result = rs.getString("data_value");
		}

		rs.close();
		ps.close();

		return result;
	}

	public static void set(String key, String value) throws SQLException {
		String sql;

		if (ConfigValueFetcher.hasKey(key)) {
			sql = "UPDATE config SET data_value = ? WHERE data_key = ?";
		} else {
			sql = "INSERT INTO config (data_value, data_key) VALUES (?, ?)";
		}

		PreparedStatement ps = NovaUniverseCommons.getDbConnection().getConnection().prepareStatement(sql);

		ps.setString(1, value);
		ps.setString(2, key);

		ps.executeUpdate();

		ps.close();
	}
}