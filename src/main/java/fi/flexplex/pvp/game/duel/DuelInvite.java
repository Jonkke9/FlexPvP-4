package fi.flexplex.pvp.game.duel;

import fi.flexplex.pvp.game.arena.DuelArena;
import fi.flexplex.pvp.game.kit.Kit;
import org.bukkit.entity.Player;

import java.util.List;

public class DuelInvite {

	private final long timetag;

	private final Player from;
	private final Player to;
	private final List<Kit> kits;
	private final DuelSettings settings;
	private final DuelArena arena;

	public DuelInvite(final Player from, final Player to, final List<Kit> kits, final DuelSettings settings, final DuelArena arena) {
		this.from = from;
		this.to = to;
		this.kits = kits;
		this.settings = settings;
		this.arena = arena;
		timetag = System.currentTimeMillis();
	}

	public Player getFrom() {
		return from;
	}

	public Player getTo() {
		return to;
	}

	public List<Kit> getKits() {
		return kits;
	}

	public long getTimeTag() {
		return timetag;
	}

	public DuelArena getArena() {
		return this.arena;
	}

	public DuelSettings getSettings() {
		return settings;
	}
}
