package fi.flexplex.pvp.listener;

import fi.flexplex.pvp.game.arena.ArenaManager;
import fi.flexplex.pvp.game.duel.Duels;
import fi.flexplex.pvp.game.playerdata.PlayerDataManager;
import fi.flexplex.pvp.misc.scoreboard.PvpScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public final class JoinAndLeaveListener implements Listener {

	@EventHandler()
	public void onJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		PlayerDataManager.onJoin(player);
		ArenaManager.getLobby().addPlayer(player);
		PvpScoreboard.updateFFAListScore(player);
	}

	@EventHandler
	public void onLeave(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		PlayerDataManager.onLeave(player);
		Duels.clearInvitesToAndFrom(player);
	}
}
