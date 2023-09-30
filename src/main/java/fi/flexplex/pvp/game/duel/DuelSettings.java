package fi.flexplex.pvp.game.duel;

import org.bukkit.GameMode;

public final class DuelSettings {

	private final boolean hunger;
	private final int regenSpeed;
	private final GameMode gameMode;

	public DuelSettings(final boolean hunger, final int regenSpeed, final GameMode gameMode) {
		this.hunger = hunger;
		this.regenSpeed = regenSpeed;
		this.gameMode = gameMode;
	}

	public boolean hunger() {
		return hunger;
	}

	public int regenSpeed() {
		return regenSpeed;
	}

	public GameMode gameMode() {
		return gameMode;
	}

	public static DuelSettings defaultSettings() {
		return new DuelSettings(false, 40, GameMode.ADVENTURE);
	}

}
