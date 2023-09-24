package fi.flexplex.pvp.menus;

import fi.flexplex.pvp.game.kit.Kit;
import fi.flexplex.pvp.game.arena.ArenaManager;
import fi.flexplex.pvp.game.playerdata.PlayerDataManager;
import fi.flexplex.pvp.misc.Util;
import org.bukkit.entity.Player;

public final class FFAKitSelector extends Menu {

	public FFAKitSelector(final Player p, final boolean reSpawn) {
		super(p, Util.menuSize(ArenaManager.getFfaArena().getAllowedKits().size()), "PVP_MENU_KIT_SELECTOR", (player) -> {
			if (PlayerDataManager.getPlayerData(player).getArena() != ArenaManager.getLobby()) {
				PlayerDataManager.getPlayerData(player).changeArena(ArenaManager.getLobby());
			}
		});

		int slot = 0;
		for (final Kit kit : ArenaManager.getFfaArena().getAllowedKits()) {
			this.setItem(kit.getIcon(p), slot, (type) -> {
				ArenaManager.getFfaArena().send(p, kit);
			});
			slot++;
		}
		this.open();
	}
}
