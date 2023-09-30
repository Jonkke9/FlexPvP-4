package fi.flexplex.pvp.listener;

import fi.flexplex.core.util.PermissionNodes;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;


public class WorldProtectListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(final BlockBreakEvent event) {
		if (!event.getPlayer().hasPermission(PermissionNodes.WORLDPROTECT)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(final BlockPlaceEvent event) {
		if (!event.getPlayer().hasPermission(PermissionNodes.WORLDPROTECT) || event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockFade(final BlockFadeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		final Material type = event.getClickedBlock().getType();

		if (!Tag.BUTTONS.getValues().contains(type) && !Tag.DOORS.getValues().contains(type)) {
			if ((event.getAction() == Action.RIGHT_CLICK_BLOCK && type.isInteractable())) {
				if (event.getPlayer().getGameMode() == GameMode.CREATIVE && event.getPlayer().hasPermission(PermissionNodes.WORLDPROTECT)) {
					return;
				}
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onHangingBreakByEntity(final HangingBreakByEntityEvent event) {
		if (event.getRemover() instanceof final Player player) {
			if (! player.hasPermission(PermissionNodes.WORLDPROTECT)  || player.getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onVehicleDamage(final VehicleDamageEvent event) {
		if (event.getAttacker() instanceof final Player player) {
			if (! player.hasPermission(PermissionNodes.WORLDPROTECT)  || player.getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityExplode(final EntityExplodeEvent event) {
		event.blockList().removeAll(event.blockList());
	}
}
