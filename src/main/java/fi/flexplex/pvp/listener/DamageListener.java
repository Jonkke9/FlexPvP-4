package fi.flexplex.pvp.listener;

import fi.flexplex.pvp.game.arena.ArenaManager;
import fi.flexplex.pvp.game.arena.PvpArena;
import fi.flexplex.pvp.game.playerdata.PlayerData;
import fi.flexplex.pvp.game.playerdata.PlayerDataManager;
import fi.flexplex.pvp.misc.scoreboard.PvpScoreboard;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public final class DamageListener implements Listener {

	@EventHandler
	public void onDamage(final EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		final Player player = ((Player) event.getEntity()).getPlayer();
		final PlayerData data = PlayerDataManager.getPlayerData(player);

		if (!(data.getArena() instanceof PvpArena)) {
			event.setCancelled(true);
			return;
		}
		final PvpArena pvpArena = (PvpArena) data.getArena();

		if (!pvpArena.allowDamage(event.getCause())) {
			event.setCancelled(true);
			return;
		}

		if (event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
			event.setDamage(Integer.MAX_VALUE);
		}else if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
			event.setCancelled(true);
			data.changeArena(ArenaManager.getLobby(),true);
			return;
		}
		if (data.getArena() == ArenaManager.getFfaArena()) {
			PvpScoreboard.updateFFABellowNameScoreboard(player, (int) (player.getHealth() - event.getFinalDamage()));
		}

		Player playerDamager;

		if (event instanceof EntityDamageByEntityEvent) {
			playerDamager = resolveToPlayerDamager(((EntityDamageByEntityEvent) event).getDamager());

			playerDamager = (playerDamager != player) ? playerDamager : null;

			if (playerDamager != null && PlayerDataManager.getPlayerData(playerDamager).getArena() != data.getArena()) {
				event.setCancelled(true);
				return;
			}
		} else playerDamager = null;


		if (! event.isCancelled() && event.getFinalDamage() >= player.getHealth()) {
			event.setCancelled(true);
			pvpArena.onDeath(player, (playerDamager != null) ? playerDamager : ((player.getKiller() != player) ? player.getKiller() : null));
		}
	}

	private Player resolveToPlayerDamager(Entity damager) {
		while (true) {
			if (damager instanceof Player) {
				return (Player) damager;
			} else if (damager instanceof Projectile) {
				final Projectile projectile = (Projectile) damager;

				if (projectile.getShooter() instanceof Entity) {
					damager = (Entity) projectile.getShooter();
				} else {
					return null;
				}
			} else if (damager instanceof Tameable) {
				final Tameable tameable = (Tameable) damager;

				if (tameable.getOwner() instanceof Entity) {
					damager = (Entity) tameable.getOwner();
				} else if (tameable.getOwner() instanceof OfflinePlayer) {
					// Player or null:
					return ((OfflinePlayer) tameable.getOwner()).getPlayer();
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}
}
