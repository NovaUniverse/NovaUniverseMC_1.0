package net.novauniverse.lobby.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIHolder;
import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIReadOnlyHolder;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class ServerMenu {
	
	public static void show(Player player) {
		GUIHolder holder = new GUIReadOnlyHolder();
		Inventory inventory = Bukkit.getServer().createInventory(holder, 9 * 6, "Servers");
		
		ItemStack backgroundItem = new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").build();
		for(int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, backgroundItem);
		}
		
		int startAt = 10;
		
		
		
		player.openInventory(inventory);
	}
}