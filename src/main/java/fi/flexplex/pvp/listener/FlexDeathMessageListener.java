package fi.flexplex.pvp.listener;

import fi.flexplex.core.util.event.DeathMessageSendEvent;
import fi.flexplex.pvp.game.arena.ArenaManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class FlexDeathMessageListener implements Listener {


	@EventHandler (priority = EventPriority.LOWEST)
	public void onDeathMessageSend(final DeathMessageSendEvent event) {
		event.getTargets().retainAll(ArenaManager.getFfaArena().getPlayers());
		if (event.getPlayer().getKiller() == null) {
			event.setMessageKey("PVP_FFA_DEATH_ANNOUNCE");
		} else {
			event.setMessageKey("PVP_FFA_DEATH_ANNOUNCE_WITH_KILLER");
		}
	}
}
