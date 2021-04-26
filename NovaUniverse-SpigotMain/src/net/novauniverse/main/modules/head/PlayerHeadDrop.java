package net.novauniverse.main.modules.head;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class PlayerHeadDrop extends NovaModule implements Listener {
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		ItemStack playerHead = VersionIndependantUtils.get().getVersionIndependantItems().getPlayerSkull();

		SkullMeta meta = (SkullMeta) playerHead.getItemMeta();

		meta.setOwner(p.getName());

		if (p.getUniqueId().toString().equalsIgnoreCase("3442be05-4211-4a15-a10c-4bdb2b6060fa")) {
			// Special head for THEGOLDENPRO
			meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + "Not to be confused", ChatColor.WHITE + "with " + ChatColor.GOLD + ChatColor.BOLD + "Golden Head"));
		}
		
		if (p.getUniqueId().toString().equalsIgnoreCase("ca2e347b-025a-4e7b-8019-752b83661f7f")) {
			// Special head for Cirbyz
			meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + "Might be toxic", ChatColor.WHITE + "eat at your own risk"));
		}

		playerHead.setItemMeta(meta);

		p.getWorld().dropItem(p.getLocation(), playerHead);
	}

	@Override
	public String getName() {
		return "NovaUniversePlayerHeadDrop";
	}
}