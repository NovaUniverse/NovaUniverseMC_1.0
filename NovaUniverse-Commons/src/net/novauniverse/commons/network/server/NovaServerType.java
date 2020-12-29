package net.novauniverse.commons.network.server;

public class NovaServerType {
	private int id;
	private String name;
	private String displayName;
	private int softPlayerLimit;
	private int hardPlayerLimit;
	private int targetPlayerCount;
	private boolean isMinigame;
	private NovaServerType returnToServerType;
	private String serverNamingScheme;
	private String lore;
	private boolean showInServerList;
	
	public NovaServerType(int id, String name, String displayName, int softPlayerLimit, int hardPlayerLimit, int targetPlayerCount, boolean isMinigame, NovaServerType returnToServerType, String serverNamingScheme, String lore, boolean showInServerList) {
		this.id = id;
		this.name = name;
		this.displayName = displayName;
		this.softPlayerLimit = softPlayerLimit;
		this.hardPlayerLimit = hardPlayerLimit;
		this.targetPlayerCount = targetPlayerCount;
		this.isMinigame = isMinigame;
		this.returnToServerType = returnToServerType;
		this.serverNamingScheme = serverNamingScheme;
		this.lore = lore;
		this.showInServerList = showInServerList;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getSoftPlayerLimit() {
		return softPlayerLimit;
	}

	public int getHardPlayerLimit() {
		return hardPlayerLimit;
	}

	public int getTargetPlayerCount() {
		return targetPlayerCount;
	}

	public boolean isMinigame() {
		return isMinigame;
	}

	public void setReturnToServerType(NovaServerType returnToServerType) {
		this.returnToServerType = returnToServerType;
	}

	public NovaServerType getReturnToServerType() {
		return returnToServerType;
	}
	
	public String getServerNamingScheme() {
		return serverNamingScheme;
	}
	
	public String getLore() {
		return lore;
	}
	
	public boolean isShowInServerList() {
		return showInServerList;
	}

	/**
	 * Compare the id of 2 server types
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NovaServerType) {
			return this.getId() == ((NovaServerType) obj).getId();
		}
		return false;
	}
}