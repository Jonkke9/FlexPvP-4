package fi.flexplex.pvp.game.playerdata;

import fi.flexplex.core.Main;
import fi.flexplex.core.api.Leaderboard;
import fi.flexplex.pvp.game.arena.Arena;
import fi.flexplex.pvp.game.arena.ArenaManager;
import fi.flexplex.pvp.misc.scoreboard.PvpScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.UUID;

public final class PlayerData {

	private final Player player;
	private final PermissionAttachment attachment;


	private Arena arena = null;
	private PlayerDataManager.TimeFrame timeFrame = PlayerDataManager.TimeFrame.MONTHLY;

	private int killsAllTime = 0;
	private int killsThisMonth = 0;
	private int killsThisSession = 0;

	private int deathsAllTime = 0;
	private int deathsThisMonth = 0;
	private int deathsThisSession = 0;

	private int topStreakAllTime = 0;
	private int topStreakThisMonth = 0;
	private int topStreakThisSession = 0;

	private int winsAllTime = 0;
	private int winsThisMonth = 0;

	private int losesAllTime = 0;
	private int losesThisMonth = 0;

	private int currentStreak = 0;

	PlayerData(final Player player, final Arena arena) {
		this.attachment = player.addAttachment(fi.flexplex.pvp.Main.getInstance());
		this.player = player;
		this.arena = arena;

		final UUID uuid = player.getUniqueId();


		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {

			//kills
			killsAllTime = Leaderboard.getScore(uuid, Leaderboard.Key.KILLS, Leaderboard.Timeframe.ALL_TIME);
			killsThisMonth = Leaderboard.getScore(uuid, Leaderboard.Key.KILLS, Leaderboard.Timeframe.MONTHLY);

			//deaths
			deathsAllTime = Leaderboard.getScore(uuid, Leaderboard.Key.DEATHS, Leaderboard.Timeframe.ALL_TIME);
			deathsThisMonth = Leaderboard.getScore(uuid, Leaderboard.Key.DEATHS, Leaderboard.Timeframe.MONTHLY);

			//top streak
			topStreakAllTime = Leaderboard.getScore(uuid, Leaderboard.Key.TOP_STREAK, Leaderboard.Timeframe.ALL_TIME);
			topStreakThisMonth = Leaderboard.getScore(uuid, Leaderboard.Key.TOP_STREAK, Leaderboard.Timeframe.MONTHLY);

			//wins
			winsAllTime = Leaderboard.getScore(uuid, Leaderboard.Key.WINS, Leaderboard.Timeframe.ALL_TIME);
			winsThisMonth = Leaderboard.getScore(uuid, Leaderboard.Key.WINS, Leaderboard.Timeframe.MONTHLY);

			//loses
			losesAllTime = Leaderboard.getScore(uuid, Leaderboard.Key.LOSES, Leaderboard.Timeframe.ALL_TIME);
			losesThisMonth = Leaderboard.getScore(uuid, Leaderboard.Key.LOSES, Leaderboard.Timeframe.ALL_TIME);

		});

	}

	void onFfaKill() {
		killsAllTime++;
		killsThisMonth++;
		killsThisSession++;
		currentStreak++;

		if (currentStreak > topStreakThisSession) {
			topStreakThisSession = currentStreak;
			if (currentStreak > topStreakThisMonth) {
				topStreakThisMonth = currentStreak;
				if (currentStreak > topStreakAllTime) {
					topStreakAllTime = currentStreak;
				}
			}
		}
	}

	void onFfaDeath() {
		deathsAllTime++;
		deathsThisMonth++;
		deathsThisSession++;

		currentStreak = 0;
	}

	void onRankedDuelWin() {
		winsAllTime++;
		winsThisMonth++;
	}

	void onRankedDuelLose() {
		losesAllTime++;
		losesAllTime++;
	}

	private double kd(final int kills, final int deaths) {
		double kd = kills;
		if (kills > 0 && deaths > 0) {
			kd = (double) kills / deaths;
		}
		return kd;
	}

	public void changeArena(final Arena newArena) {
		changeArena(newArena, false);
	}
	public void changeArena(Arena newArena, final boolean causeDeath) {
		if (newArena == null) {
			newArena = ArenaManager.getLobby();
		}
		arena.removePlayer(player, causeDeath);
		newArena.addPlayer(player);
		arena = newArena;
	}

	public Arena getArena() {
		if (arena != null) {
			return arena;
		}
		return ArenaManager.getLobby();
	}

	public int getKills(final PlayerDataManager.TimeFrame timeFrame) {
		switch (timeFrame) {
			case MONTHLY -> {
				return killsThisMonth;
			}
			case THIS_SESSION -> {
				return killsThisSession;
			}
		}
		return killsAllTime;
	}

	public int getDeaths(final PlayerDataManager.TimeFrame timeFrame) {
		switch (timeFrame) {
			case MONTHLY -> {
				return deathsThisMonth;
			}
			case THIS_SESSION -> {
				return deathsThisSession;
			}
		}
		return deathsAllTime;
	}

	public int getTopStreak(final PlayerDataManager.TimeFrame timeFrame) {
		switch (timeFrame) {
			case MONTHLY -> {
				return topStreakThisMonth;
			}
			case THIS_SESSION -> {
				return topStreakThisSession;
			}
		}
		return topStreakAllTime;
	}

	public double getKD(final PlayerDataManager.TimeFrame timeFrame) {
		switch (timeFrame) {
			case MONTHLY -> {
				return kd(killsThisMonth, deathsThisMonth);
			}
			case THIS_SESSION -> {
				return kd(killsThisSession, deathsThisSession);
			}
		}
		return kd(killsAllTime, deathsAllTime);
	}

	public int getKills() {
		return getKills(timeFrame);
	}

	public int getDeaths() {
		return getDeaths(timeFrame);
	}

	public int getTopStreak() {
		return getTopStreak(timeFrame);
	}

	public double getKD() {
		return getKD(timeFrame);
	}

	public int getCurrentStreak() {
		return currentStreak;
	}

	public void resetCurrentStreak() {
		currentStreak = 0;
	}

	public PlayerDataManager.TimeFrame timeFrame() {
		return timeFrame;
	}

	public void timeFrame(final PlayerDataManager.TimeFrame newTimeFrame) {
		timeFrame = newTimeFrame;
		if (arena == ArenaManager.getFfaArena()) {
			PvpScoreboard.sendFFASidebarScoreboard(player);
		}
	}

	public Player player() {
		return player;
	}

	public PermissionAttachment getAttachment() {
		return attachment;
	}
}
