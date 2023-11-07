package fi.flexplex.pvp.misc;

import fi.flexplex.pvp.game.arena.ArenaManager;
import fi.flexplex.pvp.game.arena.PvpArena;
import fi.flexplex.pvp.misc.scoreboard.PvpScoreboard;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public final class HealTask implements Runnable {

	private final PvpArena arena;

	public HealTask(final PvpArena arena) {
		this.arena = arena;
	}

	@Override
	public void run() {
		final boolean ffaArena = arena == ArenaManager.getFfaArena();

		for (final Player player : arena.getPlayers()) {
			final double max = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

			if (player.getHealth() + 1 <= max) {
				player.setHealth(player.getHealth() + 1);
			} else {
				player.setHealth(player.getHealth() + (max - player.getHealth()));
			}

			if(ffaArena) {
				PvpScoreboard.updateFFABellowNameScoreboard(player, (int) player.getHealth());
			}
		}
	}
}
