package fi.flexplex.pvp.game.playerdata;

import fi.flexplex.core.api.Leaderboard;
import fi.flexplex.core.module.leaderboard.LeaderboardModule;
import fi.flexplex.pvp.game.arena.Arena;
import fi.flexplex.pvp.game.arena.ArenaManager;
import fi.flexplex.pvp.misc.scoreboard.PvpScoreboard;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PlayerData {

	private final Player player;

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

	protected PlayerData(final Player player, final Arena arena) {
		this.player = player;
		this.arena = arena;
		loadStats();
	}

	protected void loadStats() {
		final UUID uuid = player.getUniqueId();

		CompletableFuture.supplyAsync(() -> Leaderboard.getScore(uuid, Leaderboard.Key.KILLS)).whenComplete((kills, throwable) -> {
			if (throwable == null) {
				killsAllTime = kills.getScore(LeaderboardModule.Timeframe.ALL_TIME);
				killsThisMonth = kills.getScore(LeaderboardModule.Timeframe.MONTHLY);
			}
		});

		CompletableFuture.supplyAsync(() -> Leaderboard.getScore(uuid, Leaderboard.Key.DEATHS)).whenComplete((deaths, throwable) -> {
			if (throwable == null) {
				deathsAllTime = deaths.getScore(LeaderboardModule.Timeframe.ALL_TIME);
				deathsThisMonth = deaths.getScore(LeaderboardModule.Timeframe.MONTHLY);
			}
		});

		CompletableFuture.supplyAsync(() -> Leaderboard.getScore(uuid, Leaderboard.Key.TOP_STREAK)).whenComplete((topStreak, throwable) -> {
			if (throwable == null) {
				topStreakAllTime = topStreak.getScore(LeaderboardModule.Timeframe.ALL_TIME);
				topStreakThisMonth = topStreak.getScore(LeaderboardModule.Timeframe.MONTHLY);
			}
		});

		CompletableFuture.supplyAsync(() -> Leaderboard.getScore(uuid, Leaderboard.Key.WINS)).whenComplete((wins, throwable) -> {
			if (throwable == null) {
				winsAllTime = wins.getScore(LeaderboardModule.Timeframe.ALL_TIME);
				winsThisMonth = wins.getScore(LeaderboardModule.Timeframe.MONTHLY);
			}
		});

		CompletableFuture.supplyAsync(() -> Leaderboard.getScore(uuid, Leaderboard.Key.LOSES)).whenComplete((loses, throwable) -> {
			if (throwable == null) {
				losesAllTime = loses.getScore(LeaderboardModule.Timeframe.ALL_TIME);
				losesThisMonth = loses.getScore(LeaderboardModule.Timeframe.MONTHLY);
			}
		});
	}

	protected void onFfaKill() {
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

	protected void onFfaDeath() {
		deathsAllTime++;
		deathsThisMonth++;
		deathsThisSession++;

		currentStreak = 0;
	}

	protected void onRankedDuelWin() {
		winsAllTime++;
		winsThisMonth++;
	}

	protected void onRankedDuelLose() {
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
}
