package fi.flexplex.pvp.listener;

import fi.flexplex.pvp.Main;
import fi.flexplex.pvp.game.playerdata.PlayerData;
import fi.flexplex.pvp.game.playerdata.PlayerDataManager;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public final class PvPListener implements Listener {

	@EventHandler
	public void onFoodLevelChange(final FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player player) {
			final PlayerData data = PlayerDataManager.getPlayerData(player);

			if (!data.getArena().allowHunger()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDropItem(final PlayerDropItemEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityPickupItem(final EntityPickupItemEvent event) {
		if (event.getEntityType() != EntityType.PLAYER
				|| ((Player) event.getEntity()).getGameMode() != GameMode.CREATIVE) {

			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityLoadCrossbow(final EntityLoadCrossbowEvent event) {
		if (event.getCrossbow().hasItemMeta()
				&& event.getCrossbow().getItemMeta().hasEnchant(Enchantment.ARROW_INFINITE)) {

			event.setConsumeItem(false);
		}
	}

	@EventHandler
	public void onProjectileLaunch(final ProjectileLaunchEvent event) {
		if (event.getEntity() instanceof AbstractArrow) {
			((AbstractArrow) event.getEntity()).setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);

			Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
				if (event.getEntity().isValid()) {
					event.getEntity().remove();
				}
			}, 20*10);
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onPlayer(final PlayerArmorStandManipulateEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerInterectAtEntity(final PlayerInteractAtEntityEvent event) {
		event.setCancelled(true);
	}

}
