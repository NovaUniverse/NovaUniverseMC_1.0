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
			meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + "Oh no, you possess Golden's head.", ChatColor.WHITE + "Make sure to hide it, there very rare."));
		}

		if (p.getUniqueId().toString().equalsIgnoreCase("ca2e347b-025a-4e7b-8019-752b83661f7f")) {
			// Special head for Cirbyz
			meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + "Might be toxic", ChatColor.WHITE + "eat at your own risk"));
		}

		
		if (p.getUniqueId().toString().equalsIgnoreCase("5203face-89ca-49b7-a5a0-f2cf0fe230e7")) {
			// Special head for Woltry
			meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + "Galaxer i mina braxer", ChatColor.WHITE + "-Captain Zoom"));
		}
		
		if (p.getUniqueId().toString().equalsIgnoreCase("93bdaf65-eee6-46e3-b215-b30f6435df0a")) {
			// Special head for TheNolle
			meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + "The cutest king we've ever encountered", ChatColor.WHITE + "- Everyone"));
		}
		
		if (p.getUniqueId().toString().equalsIgnoreCase("37dc6e39-2ef9-47ad-ba8a-1f1a162800ba")) {
			// Special head for Footi_
			meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + "How did you get this?"));
		}

		playerHead.setItemMeta(meta);

		p.getWorld().dropItem(p.getLocation(), playerHead);
	}

	@Override
	public String getName() {
		return "NovaUniversePlayerHeadDrop";
	}
}