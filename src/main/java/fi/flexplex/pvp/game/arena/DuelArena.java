package fi.flexplex.pvp.game.arena;

import fi.flexplex.pvp.game.duel.Duel;
import fi.flexplex.pvp.game.duel.DuelState;
import fi.flexplex.pvp.game.kit.Kit;
import fi.flexplex.pvp.game.playerdata.PlayerDataManager;
import fi.flexplex.pvp.misc.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public final class DuelArena extends PvpArena{

	private final Location[] locations;
	private final DuelArenaTemplate template;
	private Duel activeDuel;
	private Set<Location> changedBlocks;
	private boolean ready;

	public DuelArena(final String name, final Location bounds1, final Location bounds2, final Location[] locations, final DuelArenaTemplate template) {
		super(name, bounds1, bounds2);
		this.template = template;
		this.locations = locations;
		changedBlocks = new HashSet<>();
		activeDuel = null;
		ready = false;
	}

	@Override
	public void onJoin(final Player player) {
		if (activeDuel == null || !ready) {
			PlayerDataManager.getPlayerData(player).changeArena(ArenaManager.getLobby());
			return;
		}

		if (player == activeDuel.fromPlayer()) {
			player.teleport(locations[0]);
		}else if (player == activeDuel.toPlayer()) {
			player.teleport(locations[1]);
		} else return;

		player.setGameMode(activeDuel.getSettings().gameMode());
	}

	@Override
	void onLeave(final Player player, boolean causeDeath) {
		if (activeDuel != null) {
			activeDuel.onLeave(player);
		}
	}

	@Override
	public void onDeath(final Player victim, final Player killer) {
		if (victim.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {
			victim.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
			Util.totemEffect(victim);
			return;
		} else if (victim.getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING) {
			victim.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
			Util.totemEffect(victim);
			return;
		}
		if (activeDuel != null) {
			activeDuel.onDeath(victim, true);
		}
	}

	@Override
	public void send(final Player player, final Kit kit) {

	}

	@Override
	public boolean allowDamage(final EntityDamageEvent.DamageCause cause) {
		if (activeDuel == null) {
			return false;
		}
		return activeDuel.getState() == DuelState.ACTIVE;
	}

	public boolean isFree() {
		return activeDuel == null;
	}

	public void setDuel(final Duel duel) {
		activeDuel = duel;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(final boolean ready) {
		this.ready = ready;
	}

	@Override
	public boolean allowHunger() {
		if (activeDuel == null) {
			return false;
		}

		return activeDuel.getSettings().hunger();
	}

	public DuelArenaTemplate getTemplate() {
		return template;
	}

	private arenaBlockCoordinate getCentralizedCoords(final Location location) {
		return new arenaBlockCoordinate(location.getBlockX() - bounds1.getBlockX(), location.getBlockY() - bounds1.getBlockY(), location.getBlockZ() - bounds1.getBlockZ());
	}

	public boolean canBlockBeChanged(final Location loc) {
		if (!isInBounds(loc)) {
			return false;
		}

		final arenaBlockCoordinate coords = getCentralizedCoords(loc);

		return getTemplate().canBuild(coords.x, coords.y, coords.z);

	}

	private class arenaBlockCoordinate {
		final int x, y, z;

		private arenaBlockCoordinate(final int x, final int y, final int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	public void onBlockChange(final Location location) {
		changedBlocks.add(location);
	}

}
