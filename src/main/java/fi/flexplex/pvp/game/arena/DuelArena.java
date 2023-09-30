package fi.flexplex.pvp.game.arena;

import fi.flexplex.pvp.game.duel.Duel;
import fi.flexplex.pvp.game.duel.DuelState;
import fi.flexplex.pvp.game.kit.Kit;
import fi.flexplex.pvp.game.playerdata.PlayerDataManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public final class DuelArena extends PvpArena{

	private final Location[] locations;
	private final DuelArenaTemplate template;
	private Duel activeDuel;

	private boolean ready;

	public DuelArena(final String name, final Location bounds1, final Location bounds2, final Location[] locations, final DuelArenaTemplate template) {
		super(name, bounds1, bounds2);
		this.template = template;
		this.locations = locations;
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



}
