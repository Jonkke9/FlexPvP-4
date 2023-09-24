package fi.flexplex.pvp.game.duel;

public final class DuelSettings {

	private final boolean hunger;
	private final int regenSpeed;

	private DuelSettings(final boolean hunger, final int regenSpeed) {
		this.hunger = hunger;
		this.regenSpeed = regenSpeed;
	}

	public boolean hunger() {
		return hunger;
	}

	public int regenSpeed() {
		return regenSpeed;
	}

	public static DuelSettings defaultSettings() {
		return new DuelSettings(false, 40);
	}

}
