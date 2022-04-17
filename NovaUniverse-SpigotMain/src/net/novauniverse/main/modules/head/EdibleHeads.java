package net.novauniverse.main.modules.head;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class EdibleHeads extends NovaModule implements Listener {
	public EdibleHeads() {
		super("NovaUniverse.NovaUniverseEdibleHeads");
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			if (VersionIndependantUtils.get().getItemInMainHand(p) != null) {
				if(VersionIndependantUtils.get().getVersionIndependantItems().isPlayerSkull(VersionIndependantUtils.get().getItemInMainHand(p))) {
					if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {

						e.setCancelled(true);

						if (VersionIndependantUtils.get().getItemInMainHand(p).getAmount() > 1) {
							VersionIndependantUtils.get().getItemInMainHand(p).setAmount(p.getItemInHand().getAmount() - 1);
						} else {
							VersionIndependantUtils.get().setItemInMainHand(p, ItemBuilder.AIR);
						}

						p.getLocation().getWorld().playSound(p.getLocation(), Sound.EAT, 1F, 1F);
						p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 0));
					}
				}
			}
		}
	}
}