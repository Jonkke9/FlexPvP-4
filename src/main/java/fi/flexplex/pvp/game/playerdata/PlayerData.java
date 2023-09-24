package fi.flexplex.pvp.game.playerdata;

import fi.flexplex.core.api.Leaderboard;
import fi.flexplex.core.module.leaderboard.LeaderboardModule;
import fi.flexplex.pvp.game.arena.Arena;
import fi.flexplex.pvp.game.kit.Kit;
import fi.flexplex.pvp.game.arena.ArenaManager;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PlayerData {

	private final Player player;

	private Arena arena = null;
	private PlayerDataManager.TimeFrame timeFrame = PlayerDataManager.TimeFrame.MONTHLY;

	private Kit kit = null;

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
		final UUID uuid = this.player.getUniqueId();


		CompletableFuture.supplyAsync(() -> Leaderboard.getScore(uuid, Leaderboard.Key.KILLS)).whenComplete((kills, throwable) -> {
			if (throwable == null) {
				this.killsAllTime = kills.getScore(LeaderboardModule.Timeframe.ALL_TIME);
				this.killsThisMonth = kills.getScore(LeaderboardModule.Timeframe.MONTHLY);
			}
		});

		CompletableFuture.supplyAsync(() -> Leaderboard.getScore(uuid, Leaderboard.Key.DEATHS)).whenComplete((deaths, throwable) -> {
			if (throwable == null) {
				this.deathsAllTime = deaths.getScore(LeaderboardModule.Timeframe.ALL_TIME);
				this.deathsThisMonth = deaths.getScore(LeaderboardModule.Timeframe.MONTHLY);
			}
		});

		CompletableFuture.supplyAsync(() -> Leaderboard.getScore(uuid, Leaderboard.Key.TOP_STREAK)).whenComplete((topStreak, throwable) -> {
			if (throwable == null) {
				this.topStreakAllTime = topStreak.getScore(LeaderboardModule.Timeframe.ALL_TIME);
				this.topStreakThisMonth = topStreak.getScore(LeaderboardModule.Timeframe.MONTHLY);
			}
		});

		CompletableFuture.supplyAsync(() -> Leaderboard.getScore(uuid, Leaderboard.Key.WINS)).whenComplete((wins, throwable) -> {
			if (throwable == null) {
				this.winsAllTime = wins.getScore(LeaderboardModule.Timeframe.ALL_TIME);
				this.winsThisMonth = wins.getScore(LeaderboardModule.Timeframe.MONTHLY);
			}
		});

		CompletableFuture.supplyAsync(() -> Leaderboard.getScore(uuid, Leaderboard.Key.LOSES)).whenComplete((loses, throwable) -> {
			if (throwable == null) {
				this.losesAllTime = loses.getScore(LeaderboardModule.Timeframe.ALL_TIME);
				this.losesThisMonth = loses.getScore(LeaderboardModule.Timeframe.MONTHLY);
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
		double kd = 0;
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
		this.arena.removePlayer(this.player, causeDeath);
		newArena.addPlayer(this.player);
		this.arena = newArena;
	}

	public Arena getArena() {
		if (this.arena != null) {
			return this.arena;
		}
		return ArenaManager.getLobby();
	}


	public int getKills(final PlayerDataManager.TimeFrame timeFrame) {
		switch (timeFrame) {
			case MONTHLY -> {
				return this.killsThisMonth;
			}
			case THIS_SESSION -> {
				return this.killsThisSession;
			}
		}
		return this.killsAllTime;
	}

	public int getDeaths(final PlayerDataManager.TimeFrame timeFrame) {
		switch (timeFrame) {
			case MONTHLY -> {
				return this.deathsThisMonth;
			}
			case THIS_SESSION -> {
				return this.deathsThisSession;
			}
		}
		return this.deathsAllTime;
	}

	public int getTopStreak(final PlayerDataManager.TimeFrame timeFrame) {
		switch (timeFrame) {
			case MONTHLY -> {
				return this.topStreakThisMonth;
			}
			case THIS_SESSION -> {
				return this.topStreakThisSession;
			}
		}
		return this.topStreakAllTime;
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
		return this.getKills(this.timeFrame);
	}

	public int getDeaths() {
		return this.getDeaths(this.timeFrame);
	}

	public int getTopStreak() {
		return this.getTopStreak(this.timeFrame);
	}

	public double getKD() {
		return this.getKD(this.timeFrame);
	}

	public int getCurrentStreak() {
		return currentStreak;
	}

	public void resetCurrentStreak() {
		this.currentStreak = 0;
	}

	public Kit getKit() {
		return this.kit;
	}

	public void setKit(final Kit kit) {
		this.kit = kit;
	}

	public PlayerDataManager.TimeFrame timeFrame() {
		return this.timeFrame;
	}

	public void timeFrame(final PlayerDataManager.TimeFrame timeFrame) {
		this.timeFrame = timeFrame;
	}

	public Player player() {
		return this.player;
	}
}
