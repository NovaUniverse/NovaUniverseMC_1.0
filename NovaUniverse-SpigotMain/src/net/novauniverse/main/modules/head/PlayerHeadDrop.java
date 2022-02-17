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

		// Some of the OG players heads have custom text
		if (p.getUniqueId().toString().equalsIgnoreCase("3442be05-4211-4a15-a10c-4bdb2b6060fa")) {
			// Special head for THEGOLDENPRO
			meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + "Not to be confused", ChatColor.WHITE + "with " + ChatColor.GOLD + ChatColor.BOLD + "Golden Head"));
		}

		if (p.getUniqueId().toString().equalsIgnoreCase("980dbf7d-0904-426f-9c02-d9af3c099fb2")) {
			// Special head for Istromus
			meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + "Visual glitch, Istromus never dies"));
		}

		if (p.getUniqueId().toString().equalsIgnoreCase("5203face-89ca-49b7-a5a0-f2cf0fe230e7")) {
			// Special head for Woltry
			meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + "Roses are red, Violets are blue", ChatColor.WHITE + "Fallschrimjägergewehr42"));
		}

		if (p.getUniqueId().toString().equalsIgnoreCase("93bdaf65-eee6-46e3-b215-b30f6435df0a")) {
			// Special head for TheNolle
			meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + "The cutest king we've ever encountered", ChatColor.WHITE + "- Everyone"));
		}

		if (p.getUniqueId().toString().equalsIgnoreCase("37dc6e39-2ef9-47ad-ba8a-1f1a162800ba")) {
			// Special head for Footi_
			meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + "How did you get this?"));
		}

		if (p.getUniqueId().toString().equalsIgnoreCase("45c49c88-950c-4f4b-afe0-55ca5d0593d8")) {
			// Special head for Aleksa
			if (e.getEntity().getKiller() != null) {
				meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + e.getEntity().getKiller().getName() + " is now on the", ChatColor.WHITE + "waffle mafia hitlist"));
			}
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