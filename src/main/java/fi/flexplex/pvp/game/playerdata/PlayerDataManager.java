package fi.flexplex.pvp.game.playerdata;

import fi.flexplex.core.api.Leaderboard;
import fi.flexplex.pvp.game.arena.ArenaManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public final class PlayerDataManager {
	private static final HashMap<UUID, PlayerData> cache = new HashMap<UUID, PlayerData>();


	public static void onJoin(final Player player) {
		cache.put(player.getUniqueId(), new PlayerData(player, ArenaManager.getLobby()));
		getPlayerData(player).loadStats();
	}

	public static void onLeave(final Player player) {
		if (cache.containsKey(player.getUniqueId())) {
			getPlayerData(player).getArena().removePlayer(player, true);
			cache.remove(player.getUniqueId());
		}
	}


	public static void onFfaKill(final Player killer, final int streak) {
		if (hasPlayerData(killer)) {
			getPlayerData(killer).onFfaKill();
		}
		Leaderboard.addScore(killer.getUniqueId(), Leaderboard.Key.KILLS, 1);
		Leaderboard.setHighscore(killer.getUniqueId(), Leaderboard.Key.TOP_STREAK, streak);
	}

	public static void onFfaDeath(final Player victim) {
		if (hasPlayerData(victim)) {
			getPlayerData(victim).onFfaDeath();
		}
		Leaderboard.addScore(victim.getUniqueId(), Leaderboard.Key.DEATHS, 1);
	}

	public static void onRankedDuelWin(final Player winner) {
		Leaderboard.addScore(winner.getUniqueId(), Leaderboard.Key.WINS, 1);
		if (hasPlayerData(winner)) {
			getPlayerData(winner).onRankedDuelWin();
		}
	}

	public static void onRankedDuelLoss(final Player loser) {
		Leaderboard.addScore(loser.getUniqueId(), Leaderboard.Key.LOSES, 1);
		if (hasPlayerData(loser)) {
			getPlayerData(loser).onRankedDuelLose();
		}
	}

	public static PlayerData getPlayerData(final Player player) {
		return cache.get(player.getUniqueId());
	}

	public static boolean hasPlayerData(final Player player) {
		return cache.containsKey(player.getUniqueId());
	}

	public static void deletePlayerData(final Player player) {
		cache.remove(player.getUniqueId());
	}

	public enum TimeFrame {
		ALL_TIME ("PVP_TIMEFRAME_ALL_TIME"),
		MONTHLY ("PVP_TIMEFRAME_MONTHLY"),
		THIS_SESSION ("PVP_TIMEFRAME_THIS_SESSION"),
		;

		private static final TimeFrame[] vals = values();

		TimeFrame(final String pvpTimeframe) {

		}

		public TimeFrame next() {
			return vals[(this.ordinal() + 1) % vals.length];
		}
	}
}
