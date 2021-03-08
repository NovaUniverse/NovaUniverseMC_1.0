package net.novauniverse.lobby.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.novauniverse.commons.NovaUniverseCommons;
import net.novauniverse.commons.network.server.NovaServerType;
import net.zeeraa.novacore.spigot.module.modules.gui.GUIAction;
import net.zeeraa.novacore.spigot.module.modules.gui.callbacks.GUIClickCallback;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class SingleServerMenu {
	private static final int GUI_SERVER_ICON_SLOT = 4;

	public static void show(Player player, NovaServerType serverType) {
		ServerMenuHolder holder = new ServerMenuHolder();
		Inventory inventory = Bukkit.getServer().createInventory(holder, 9 * 1, serverType.getDisplayName());

		ItemStack backgroundItem = new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").build();
		for (int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, backgroundItem);
		}

		ItemStack stack = ServerTypeIconCreator.createIcon(serverType);

		inventory.setItem(GUI_SERVER_ICON_SLOT, stack);

		holder.getServerTypeSlots().put(serverType, GUI_SERVER_ICON_SLOT);
		holder.addClickCallback(GUI_SERVER_ICON_SLOT, new GUIClickCallback() {
			@Override
			public GUIAction onClick(Inventory clickedInventory, Inventory inventory, HumanEntity entity, int clickedSlot, SlotType slotType, InventoryAction clickType) {
				entity.sendMessage(ChatColor.GOLD + "Joining " + ChatColor.AQUA + serverType.getDisplayName());
				NovaUniverseCommons.getServerFinder().joinServerType(entity.getUniqueId(), serverType);
				return GUIAction.NONE;
			}
		});

		player.openInventory(inventory);
	}
}