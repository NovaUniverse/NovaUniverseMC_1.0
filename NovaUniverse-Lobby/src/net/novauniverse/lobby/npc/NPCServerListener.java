package net.novauniverse.lobby.npc;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.novauniverse.commons.network.server.NovaServerType;
import net.novauniverse.lobby.menu.SingleServerMenu;
import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class NPCServerListener extends NovaModule implements Listener {
	private static NPCServerListener instance;

	private Map<Integer, String> npcServerGroupMap;

	@Override
	public void onLoad() {
		NPCServerListener.instance = this;
		this.npcServerGroupMap = new HashMap<Integer, String>();
	}

	public static NPCServerListener getInstance() {
		return instance;
	}

	@Override
	public String getName() {
		return "NPCServerListener";
	}

	public Map<Integer, String> getNpcServerGroupMap() {
		return npcServerGroupMap;
	}

	@EventHandler
	public void onNPCClick(NPCRightClickEvent e) {
		this.handleClickEvent(e);
	}

	@EventHandler
	public void onNPCClick(NPCLeftClickEvent e) {
		this.handleClickEvent(e);
	}

	public void handleClickEvent(NPCClickEvent e) {
		int npcId = e.getNPC().getId();

		// System.out.println("npcServerGroupMap.containsKey(" + npcId + ") : " +
		// npcServerGroupMap.containsKey(npcId));

		if (npcServerGroupMap.containsKey(npcId)) {
			String serverTypeName = npcServerGroupMap.get(npcId);
			NovaServerType serverType = NovaMain.getInstance().getNetworkManager().getServerTypeByName(serverTypeName);

			if (serverType == null) {
				e.getClicker().sendMessage(ChatColor.RED + "Error: Could not find server type: " + serverTypeName);
				return;
			}

			SingleServerMenu.show(e.getClicker(), serverType);
		}
	}
}