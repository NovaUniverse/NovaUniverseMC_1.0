package net.novauniverse.commons;

import net.novauniverse.commons.abstraction.AbstractServerFinder;
import net.novauniverse.commons.network.NovaNetworkManager;
import net.zeeraa.novacore.commons.database.DBConnection;

public class NovaUniverseCommons {
	private static DBConnection dbConnection = null;
	private static NovaNetworkManager networkManager = null;
	private static AbstractServerFinder serverFinder = null;

	public static void setDbConnection(DBConnection dbConnection) {
		NovaUniverseCommons.dbConnection = dbConnection;
	}

	public static void setServerFinder(AbstractServerFinder serverFinder) {
		NovaUniverseCommons.serverFinder = serverFinder;
	}
	
	public static DBConnection getDbConnection() {
		return dbConnection;
	}
	
	public static AbstractServerFinder getServerFinder() {
		return serverFinder;
	}
	
	public static NovaNetworkManager getNetworkManager() {
		return networkManager;
	}
	
	public static void setNetworkManager(NovaNetworkManager networkManager) {
		NovaUniverseCommons.networkManager = networkManager;
	}
}