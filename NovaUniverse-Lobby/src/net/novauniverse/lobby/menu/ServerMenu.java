package net.novauniverse.lobby.menu;

import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.novauniverse.commons.network.server.NovaServerType;
import net.novauniverse.main.NovaMain;
import net.zeeraa.novacore.spigot.module.modules.gui.GUIAction;
import net.zeeraa.novacore.spigot.module.modules.gui.callbacks.GUIClickCallback;
import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIHolder;
import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIReadOnlyHolder;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class ServerMenu {
	public static final int GUI_START_AT = 10;
	public static final int GUI_NEWLINE_AT = 7;
	public static final int GUI_NEWLINE_INCREMENT = 2;
	
	public static void show(Player player) {
		GUIHolder holder = new GUIReadOnlyHolder();
		Inventory inventory = Bukkit.getServer().createInventory(holder, 9 * 6, "Servers");

		ItemStack backgroundItem = new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").build();
		for (int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, backgroundItem);
		}

		int i = 0;

		for (NovaServerType serverType : NovaMain.getInstance().getNetworkManager().getServerTypes()) {
			if (!serverType.isShowInServerList()) {
				continue;
			}

			ItemStack stack = ServerTypeIconCreator.createIcon(serverType);

			int offset = ((int) (((double) i) / GUI_NEWLINE_AT)) * GUI_NEWLINE_INCREMENT;
			int slot = GUI_START_AT + i + offset;

			inventory.setItem(slot, stack);

			holder.addClickCallback(slot, new GUIClickCallback() {
				@Override
				public GUIAction onClick(Inventory clickedInventory, Inventory inventory, HumanEntity entity, int clickedSlot, SlotType slotType, InventoryAction clickType) {
					entity.sendMessage(ChatColor.GOLD + "Joining " + ChatColor.AQUA + serverType.getDisplayName());
					try {
						if (!NovaMain.getInstance().getNetworkManager().sendPlayerToServer(entity.getUniqueId(), serverType)) {
							entity.sendMessage(ChatColor.RED + "Could not find a server. Please try again later");
						}
					} catch (SQLException e) {
						entity.sendMessage(ChatColor.DARK_RED + e.getClass().getName());
						e.printStackTrace();
					}
					return GUIAction.NONE;
				}
			});

			i++;
		}

		player.openInventory(inventory);
	}
}