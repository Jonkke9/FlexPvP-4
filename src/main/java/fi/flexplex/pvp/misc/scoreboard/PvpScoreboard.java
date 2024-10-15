package fi.flexplex.pvp.misc.scoreboard;

import fi.flexplex.core.api.FlexPlayer;
import fi.flexplex.core.api.Language;
import fi.flexplex.pvp.game.arena.ArenaManager;
import fi.flexplex.pvp.game.playerdata.PlayerData;
import fi.flexplex.pvp.game.playerdata.PlayerDataManager;
import fi.flexplex.pvp.misc.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Optional;

public final class PvpScoreboard {
	private static final Scoreboard scoreboard = new Scoreboard();

	private static final Optional<NumberFormat> EMPTY = Optional.empty();

	public static void sendFFASidebarScoreboard(final Player p) {
		final PlayerData data = PlayerDataManager.getPlayerData(p);
		final Objective obj = new Objective(scoreboard, "FFA_SIDE", ObjectiveCriteria.DUMMY, Component.empty(), ObjectiveCriteria.RenderType.INTEGER, false, null);
		obj.setDisplayName(Component.literal(Language.getStringMessage(p, "PVP_FFA_SCOREBOARD_TITLE")));
		
		sendPacket(p, new ClientboundSetObjectivePacket(obj, 1));
		sendPacket(p, new ClientboundSetObjectivePacket(obj, 0));
		sendPacket(p, new ClientboundSetDisplayObjectivePacket(DisplaySlot.SIDEBAR, obj));

		sendPacket(p, new ClientboundSetScorePacket("SPACER", "FFA_SIDE", 5, Optional.of(Component.literal("§8 ")), EMPTY));
		sendPacket(p, new ClientboundSetScorePacket("KILLS", "FFA_SIDE", 4, Optional.of(Component.literal(Language.getStringMessage(p, "PVP_FFA_SCOREBOARD_KILLS", String.valueOf(data.getKills())))), EMPTY));
		sendPacket(p, new ClientboundSetScorePacket("DEATHS", "FFA_SIDE", 3, Optional.of(Component.literal(Language.getStringMessage(p, "PVP_FFA_SCOREBOARD_DEATHS", String.valueOf(data.getDeaths())))), EMPTY));
		sendPacket(p, new ClientboundSetScorePacket("KDR", "FFA_SIDE", 2, Optional.of(Component.literal(Language.getStringMessage(p, "PVP_FFA_SCOREBOARD_KD", new DecimalFormat("#.##").format(data.getKD())))), EMPTY));
		sendPacket(p, new ClientboundSetScorePacket("STREAK", "FFA_SIDE", 1, Optional.of(Component.literal(Language.getStringMessage(p, "PVP_FFA_SCOREBOARD_TOP_STREAK", String.valueOf(data.getTopStreak())))), EMPTY));
		sendPacket(p, new ClientboundSetScorePacket("TIMEFRAME", "FFA_SIDE", 0, Optional.of(Component.literal(getTimeFrameText(data))), EMPTY));

	}

	public static void sendDuelsSidebarScoreboard(final Player p, final Player opponent, final int points1, final int points2, final String arenaname) {
		final Objective obj = new Objective(scoreboard, "DUELS_SIDE", ObjectiveCriteria.DUMMY, Component.empty(), ObjectiveCriteria.RenderType.INTEGER, false, null);
		obj.setDisplayName(Component.literal(Language.getStringMessage(p, "PVP_DUELS_SCOREBOARD_TITLE")));
		sendPacket(p, new ClientboundSetObjectivePacket(obj, 1));
		sendPacket(p, new ClientboundSetObjectivePacket(obj, 0));
		sendPacket(p, new ClientboundSetDisplayObjectivePacket(DisplaySlot.SIDEBAR, obj));

		sendPacket(p, new ClientboundSetScorePacket("SPACER1", "DUELS_SIDE", 10, Optional.of(Component.literal("§8§l  ")), EMPTY));
		sendPacket(p, new ClientboundSetScorePacket("SPACER2", "DUELS_SIDE", 9, Optional.of(Component.literal("§8§l  ")), EMPTY));
		sendPacket(p, new ClientboundSetScorePacket("ROUND", "DUELS_SIDE", 8, Optional.of(Component.literal(Language.getStringMessage(p, "PVP_DUELS_SCOREBOARD_ROUND", String.valueOf(points1 + points2 + 1)))), EMPTY));
		sendPacket(p, new ClientboundSetScorePacket("POINTS", "DUELS_SIDE", 7, Optional.of(Component.literal(Language.getStringMessage(p, "PVP_DUELS_SCOREBOARD_POINTS", String.valueOf(points1), String.valueOf(points2)))), EMPTY));
		sendPacket(p, new ClientboundSetScorePacket("SPACER3", "DUELS_SIDE", 6, Optional.of(Component.literal("§8§l  ")), EMPTY));
		sendPacket(p, new ClientboundSetScorePacket("PLAYER", "DUELS_SIDE", 5, Optional.of(Component.literal(FlexPlayer.getPlayer(p).getLegacyDisplayName())), EMPTY));
		sendPacket(p, new ClientboundSetScorePacket("VS", "DUELS_SIDE", 4, Optional.of(Component.literal(Language.getStringMessage(p, "PVP_DUELS_SCOREBOARD_VERSUS"))), EMPTY));
		sendPacket(p, new ClientboundSetScorePacket("OPPONENT", "DUELS_SIDE", 3, Optional.of(Component.literal(FlexPlayer.getPlayer(opponent).getLegacyDisplayName())), EMPTY));
		sendPacket(p, new ClientboundSetScorePacket("SPACER4", "DUELS_SIDE", 2, Optional.of(Component.literal("§8 ")), EMPTY));
		sendPacket(p, new ClientboundSetScorePacket("ARENA", "DUELS_SIDE", 1, Optional.of(Component.literal(Language.getStringMessage(p, "PVP_DUELS_SCOREBOARD_ARENA"))), EMPTY));
		sendPacket(p, new ClientboundSetScorePacket("ARENA_NAME", "DUELS_SIDE", 0,  Optional.of(Component.literal("§7 " + arenaname)), EMPTY));
	}

	public static void sendFFABellowNameScoreboard(final Player p) {
		final Objective obj = new Objective(scoreboard, "FFA_BELLOW_NAME", ObjectiveCriteria.HEALTH, Component.literal("§c❤"), ObjectiveCriteria.RenderType.HEARTS, false, null);
		sendPacket(p, new ClientboundSetObjectivePacket(obj, 1));
		sendPacket(p, new ClientboundSetObjectivePacket(obj, 0));
		sendPacket(p, new ClientboundSetDisplayObjectivePacket(DisplaySlot.BELOW_NAME, obj));
		
		for (final Player player : ArenaManager.getFfaArena().getPlayers()) {
			sendPacket(player, new ClientboundSetScorePacket(p.getName(),"FFA_BELLOW_NAME", (int) p.getHealth(), Optional.empty(), EMPTY));
			sendPacket(p, new ClientboundSetScorePacket(player.getName(), "FFA_BELLOW_NAME", (int) player.getHealth(), Optional.empty(), EMPTY));
		}
	}

	public static void updateFFABellowNameScoreboard(final Player p, final int health) {
		for (final Player player : ArenaManager.getFfaArena().getPlayers()) {
			sendPacket(player, new ClientboundSetScorePacket(p.getName(), "FFA_BELLOW_NAME", health, Optional.empty(), Optional.empty()));
		}
	}

	public static void sendFFAListScoreboard(final Player p) {
		final Objective obj = new Objective(scoreboard, "FFA_TAB", ObjectiveCriteria.DUMMY, Component.literal("FlexPvP"), ObjectiveCriteria.RenderType.INTEGER, false, null);
		obj.setDisplayName(Component.literal(Language.getStringMessage(p, "PVP_FFA_SCOREBOARD_TITLE")));
		sendPacket(p, new ClientboundSetObjectivePacket(obj, 1));
		sendPacket(p, new ClientboundSetObjectivePacket(obj, 0));
		sendPacket(p, new ClientboundSetDisplayObjectivePacket(DisplaySlot.LIST, obj));

		for (final Player player : Bukkit.getOnlinePlayers()) {
			sendPacket(p, new ClientboundSetScorePacket(player.getName(), "FFA_TAB", PlayerDataManager.getPlayerData(player).getCurrentStreak(),  Optional.empty(), EMPTY));
		}
	}

	public static void updateFFAListScore(final Player p) {
		final int streak = PlayerDataManager.getPlayerData(p).getCurrentStreak();
		for (final Player player : ArenaManager.getFfaArena().getPlayers()) {
			sendPacket(player, new ClientboundSetScorePacket(p.getName(), "FFA_TAB" ,streak, Optional.empty(), Optional.empty()));
		}
	}

	public static void clearFFAScoreboards(final Player p) {
		final Objective side = new Objective(scoreboard, "FFA_SIDE", ObjectiveCriteria.DUMMY, Component.empty(), ObjectiveCriteria.RenderType.INTEGER, false, null);
		final Objective list = new Objective(scoreboard, "FFA_TAB", ObjectiveCriteria.DUMMY, Component.empty(), ObjectiveCriteria.RenderType.INTEGER, false, null);
		final Objective bellowName = new Objective(scoreboard, "FFA_BELLOW_NAME", ObjectiveCriteria.DUMMY, Component.empty(), ObjectiveCriteria.RenderType.INTEGER, false, null);

		sendPacket(p, new ClientboundSetObjectivePacket(side, 1));
		sendPacket(p, new ClientboundSetObjectivePacket(list, 1));
		sendPacket(p, new ClientboundSetObjectivePacket(bellowName, 1));
	}

	public static void clearDuelsScoreboards(final Player p) {
		final Objective side = new Objective(scoreboard, "DUELS_SIDE",  ObjectiveCriteria.DUMMY, Component.empty(), ObjectiveCriteria.RenderType.INTEGER, false, null);
		sendPacket(p, new ClientboundSetObjectivePacket(side, 1));
	}

	private static String getTimeFrameText(final PlayerData playerData) {
		String string = " ";
		switch (playerData.timeFrame()) {
			case THIS_SESSION ->
				string = Language.getStringMessage(playerData.player(), "PVP_STAT_TIMEFRAME_THIS_SESSION");

			case MONTHLY ->
				string = Language.getStringMessage(playerData.player(), Util.getCurrentMonthKey()) + " " + Util.getYear();

			case ALL_TIME ->
				string = Language.getStringMessage(playerData.player(), "PVP_STAT_TIMEFRAME_ALL_TIME");
		}
		return "§8(" + string + ")";
	}

	private static void sendPacket(final Player player, final Packet<?> packet) {
		try {
			final Method getHandle = player.getClass().getDeclaredMethod("getHandle");
			final Object handle = getHandle.invoke(player);
			final Field playerConnectionField = handle.getClass().getDeclaredField("connection");
			final Object playerConnection = playerConnectionField.get(handle);
			final Method sendPacket = playerConnection.getClass().getMethod("sendPacket", Packet.class);
			sendPacket.invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}



