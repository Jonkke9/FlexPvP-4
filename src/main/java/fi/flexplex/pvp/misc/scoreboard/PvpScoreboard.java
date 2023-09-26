package fi.flexplex.pvp.misc.scoreboard;

import fi.flexplex.core.api.Language;
import fi.flexplex.pvp.game.arena.ArenaManager;
import fi.flexplex.pvp.game.playerdata.PlayerData;
import fi.flexplex.pvp.game.playerdata.PlayerDataManager;
import fi.flexplex.pvp.misc.Util;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardObjective;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public final class PvpScoreboard {
	private static final Scoreboard scoreboard = new Scoreboard();

	public static void sendFFASidebarScoreboard(final Player p) {
		final PlayerData data = PlayerDataManager.getPlayerData(p);
		final ScoreboardObjective obj = new ScoreboardObjective(scoreboard, "FFA_SIDE", IScoreboardCriteria.a, IChatBaseComponent.a(""), IScoreboardCriteria.EnumScoreboardHealthDisplay.a);
		obj.a(IChatBaseComponent.a(Language.getStringMessage(p, "PVP_FFA_SCOREBOARD_TITLE")));
		sendPacket(p, new PacketPlayOutScoreboardObjective(obj, 1));
		sendPacket(p, new PacketPlayOutScoreboardObjective(obj, 0));
		sendPacket(p, new PacketPlayOutScoreboardDisplayObjective(1, obj));

		sendPacket(p, new PacketPlayOutScoreboardScore(ScoreboardServer.Action.a, "FFA_SIDE", "§8", 5));
		sendPacket(p, new PacketPlayOutScoreboardScore(ScoreboardServer.Action.a, "FFA_SIDE", Language.getStringMessage(p, "PVP_FFA_SCOREBOARD_KILLS", String.valueOf(data.getKills())), 4));
		sendPacket(p, new PacketPlayOutScoreboardScore(ScoreboardServer.Action.a, "FFA_SIDE", Language.getStringMessage(p, "PVP_FFA_SCOREBOARD_DEATHS", String.valueOf(data.getDeaths())), 3));
		sendPacket(p, new PacketPlayOutScoreboardScore(ScoreboardServer.Action.a, "FFA_SIDE", Language.getStringMessage(p, "PVP_FFA_SCOREBOARD_KD", new DecimalFormat("#.##").format(data.getKD())), 2));
		sendPacket(p, new PacketPlayOutScoreboardScore(ScoreboardServer.Action.a, "FFA_SIDE", Language.getStringMessage(p, "PVP_FFA_SCOREBOARD_TOP_STREAK", String.valueOf(data.getTopStreak())), 1));
		sendPacket(p, new PacketPlayOutScoreboardScore(ScoreboardServer.Action.a, "FFA_SIDE", getTimeFrameText(data), 0));

	}

	public static void sendFFABellowNameScoreboard(final Player p) {
		final ScoreboardObjective obj = new ScoreboardObjective(scoreboard, "FFA_BELLOW_NAME", IScoreboardCriteria.a, IChatBaseComponent.a("§c❤"), IScoreboardCriteria.EnumScoreboardHealthDisplay.a);
		sendPacket(p, new PacketPlayOutScoreboardObjective(obj, 1));
		sendPacket(p, new PacketPlayOutScoreboardObjective(obj, 0));
		sendPacket(p, new PacketPlayOutScoreboardDisplayObjective(2, obj));

		for (final Player player : ArenaManager.getFfaArena().getPlayers()) {
			sendPacket(player, new PacketPlayOutScoreboardScore(ScoreboardServer.Action.a, "FFA_BELLOW_NAME", p.getName(), (int) p.getHealth()));
			sendPacket(p,new PacketPlayOutScoreboardScore(ScoreboardServer.Action.a, "FFA_BELLOW_NAME", player.getName(), (int) player.getHealth()));
		}
	}

	public static void updateFFABellowNameScoreboard(final Player p, final int health) {
		for (final Player player : ArenaManager.getFfaArena().getPlayers()) {
			sendPacket(player, new PacketPlayOutScoreboardScore(ScoreboardServer.Action.a, "FFA_BELLOW_NAME", p.getName(), health));
		}
	}

	public static void sendFFAListScoreboard(final Player p) {
		final ScoreboardObjective obj = new ScoreboardObjective(scoreboard, "FFA_TAB", IScoreboardCriteria.a, IChatBaseComponent.a("FlexPvP"), IScoreboardCriteria.EnumScoreboardHealthDisplay.a);
		obj.a(IChatBaseComponent.a(Language.getStringMessage(p, "PVP_FFA_SCOREBOARD_TITLE")));
		sendPacket(p, new PacketPlayOutScoreboardObjective(obj, 1));
		sendPacket(p, new PacketPlayOutScoreboardObjective(obj, 0));
		sendPacket(p, new PacketPlayOutScoreboardDisplayObjective(0, obj));

		for (final Player player : Bukkit.getOnlinePlayers()) {
			sendPacket(p, new PacketPlayOutScoreboardScore(ScoreboardServer.Action.a, "FFA_TAB", player.getName(), PlayerDataManager.getPlayerData(player).getCurrentStreak()));
		}
	}

	public static void updateFFAListScore(final Player p) {
		final int streak = PlayerDataManager.getPlayerData(p).getCurrentStreak();
		for (final Player player : ArenaManager.getFfaArena().getPlayers()) {
			sendPacket(player, new PacketPlayOutScoreboardScore(ScoreboardServer.Action.a, "FFA_TAB", p.getName(), streak));
		}
	}

	public static void clearFFAScoreboards(final Player p) {
		final ScoreboardObjective side = new ScoreboardObjective(scoreboard, "FFA_SIDE", IScoreboardCriteria.a, IChatBaseComponent.a(""), IScoreboardCriteria.EnumScoreboardHealthDisplay.a);
		final ScoreboardObjective list = new ScoreboardObjective(scoreboard, "FFA_TAB", IScoreboardCriteria.a, IChatBaseComponent.a(""), IScoreboardCriteria.EnumScoreboardHealthDisplay.a);
		final ScoreboardObjective bellowName = new ScoreboardObjective(scoreboard, "FFA_BELLOW_NAME", IScoreboardCriteria.a, IChatBaseComponent.a(""), IScoreboardCriteria.EnumScoreboardHealthDisplay.a);

		sendPacket(p, new PacketPlayOutScoreboardObjective(side, 1));
		sendPacket(p, new PacketPlayOutScoreboardObjective(list, 1));
		sendPacket(p, new PacketPlayOutScoreboardObjective(bellowName, 1));
	}

	private static String getTimeFrameText(final PlayerData playerData) {
		String string = " ";
		switch (playerData.timeFrame()) {
			case THIS_SESSION -> {
				string = Language.getStringMessage(playerData.player(), "TOPLIST_SESSION");
			}
			case MONTHLY -> {
				string = Language.getStringMessage(playerData.player(), Util.getCurrentMonthKey()) + " " + String.valueOf(Util.getYear());
			}
			case ALL_TIME -> {
				string = Language.getStringMessage(playerData.player(), "TOPLIST_ALL_TIME");
			}
		}
		return "§8(" + string  + ")";
	}

	private static void sendPacket(final Player player, final Packet<?> packet) {
		((CraftPlayer) player).getHandle().c.a(packet);
	}
}



