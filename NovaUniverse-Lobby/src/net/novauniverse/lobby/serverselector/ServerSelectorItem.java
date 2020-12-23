package net.novauniverse.lobby.serverselector;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.novauniverse.lobby.menu.ServerMenu;
import net.zeeraa.novacore.spigot.module.modules.customitems.CustomItem;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class ServerSelectorItem extends CustomItem {
	@Override
	protected ItemStack createItemStack(Player player) {
		return new ItemBuilder(Material.COMPASS).setName(ChatColor.GOLD + "" + ChatColor.BOLD + "Server Selector").build();
	}

	@Override
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		ServerMenu.show(event.getPlayer());
	}
}