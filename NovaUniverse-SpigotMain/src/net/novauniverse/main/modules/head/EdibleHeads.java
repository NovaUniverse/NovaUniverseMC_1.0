package net.novauniverse.main.modules.head;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class EdibleHeads extends NovaModule implements Listener {
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			if (p.getItemInHand() != null) {
				if(VersionIndependantUtils.get().getVersionIndependantItems().isPlayerSkull(e.getItem())) {
					if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {

						e.setCancelled(true);

						if (p.getItemInHand().getAmount() > 1) {
							p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
						} else {
							p.setItemInHand(new ItemStack(Material.AIR));
						}

						p.getLocation().getWorld().playSound(p.getLocation(), Sound.EAT, 1F, 1F);
						p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 0));
					}
				}
			}
		}
	}

	@Override
	public String getName() {
		return "NovaUniverseEdibleHeads";
	}
}