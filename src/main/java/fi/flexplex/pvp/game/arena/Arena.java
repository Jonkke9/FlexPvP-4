package fi.flexplex.pvp.game.arena;

import fi.flexplex.core.api.Language;
import fi.flexplex.pvp.misc.Util;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public abstract class Arena {

	protected final String name;
	protected final Set<Player> players;
	protected final Location bounds1;
	protected final Location bounds2;
	protected GameMode gameMode = GameMode.ADVENTURE;

	public Arena(final String name, final Location bounds1, final Location bounds2) {
		this.name = name;
		this.bounds1 = bounds1;
		this.bounds2 = bounds2;
		this.players = new HashSet<>();
	}

	public void broadcast(final String key, final String... args) {
		for (final Player p : players) {
			Language.sendMessage(p, key, args);
		}
	}

	public boolean hasPlayer(final Player player) {
		return players.contains(player);
	}

	public boolean isEmpty() {
		return players.isEmpty();
	}

	public String getName() {
		return name;
	}


	public Set<Player> getPlayers() {
		return this.players;
	}

	//try to use PlayerData.changeArena() instead if possible. these are only mend to be used when player logs in or out
	public boolean addPlayer(final Player player) {
		players.add(player);
		Util.resetPlayer(player);
		onJoin(player);
		return true;
	}


	public boolean removePlayer(final Player player, final boolean causeDeath) {
		players.remove(player);
		player.getInventory().clear();
		onLeave(player, causeDeath);
		return true;
	}

	public boolean allowHunger() {
		return false;
	}

	abstract void onJoin(final Player player);

	abstract void onLeave(final Player player, final boolean causeDeath);

	public Location getBounds1() {
		return bounds1;
	}

	public Location getBounds2() {
		return bounds2;
	}
	
	public boolean hasSpawnDelay() {
		return false;
	}

	public boolean isInBounds(final Location loc) {
		final Location smallestCorner = new Location(loc.getWorld(),
				Math.min(bounds1.getX(), bounds2.getX()),
				Math.min(bounds1.getY(), bounds2.getY()),
				Math.min(bounds1.getZ(), bounds2.getZ())
		);

		final Location largestCorner = new Location(loc.getWorld(),
				Math.max(bounds1.getX(), bounds2.getX()),
				Math.max(bounds1.getY(), bounds2.getY()),
				Math.max(bounds1.getZ(), bounds2.getZ())
		);

		if (smallestCorner.getX() <= loc.getX() && loc.getX() <= largestCorner.getX() &&
				(smallestCorner.getY() <= loc.getY() && loc.getY() <= largestCorner.getY()) &&
				(smallestCorner.getZ() <= loc.getZ() && loc.getZ() <= largestCorner.getZ())) {
			return true;
		}
		return false;
	}

}
