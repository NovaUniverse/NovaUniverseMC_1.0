package net.novauniverse.commons.network.server;

public class NovaServer {
	private int id;
	private String name;
	private String host;
	private int port;
	private NovaServerType serverType;
	private boolean minigameStarted;
	private boolean hasFailed;

	public NovaServer(int id, String name, String host, int port, NovaServerType serverType, boolean minigameStarted, boolean hasFailed) {
		this.id = id;
		this.name = name;
		this.host = host;
		this.port = port;
		this.serverType = serverType;
		this.minigameStarted = minigameStarted;
		this.hasFailed = hasFailed;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public NovaServerType getServerType() {
		return serverType;
	}

	public boolean hasMinigameStarted() {
		return minigameStarted;
	}

	public boolean hasFailed() {
		return hasFailed;
	}
	
	/**
	 * Compare the id of 2 servers
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NovaServer) {
			return this.getId() == ((NovaServer) obj).getId();
		}
		return false;
	}
}