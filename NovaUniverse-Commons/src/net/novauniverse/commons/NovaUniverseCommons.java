package net.novauniverse.commons;

import net.zeeraa.novacore.commons.database.DBConnection;

public class NovaUniverseCommons {
	private static DBConnection dbConnection = null;
	
	public static void setDbConnection(DBConnection dbConnection) {
		NovaUniverseCommons.dbConnection = dbConnection;
	}
	
	public static DBConnection getDbConnection() {
		return dbConnection;
	}
}