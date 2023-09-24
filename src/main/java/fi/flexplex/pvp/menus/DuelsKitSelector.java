package fi.flexplex.pvp.menus;

import fi.flexplex.pvp.game.duel.Duel;
import fi.flexplex.pvp.game.kit.Kit;
import fi.flexplex.pvp.misc.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.function.Consumer;

public class DuelsKitSelector extends Menu{

	public DuelsKitSelector(final Player player, final Duel duel, final Consumer<Player> onClose) {
		super(player, Util.menuSize(duel.getKits().size()), "PVP_MENU_KIT_SELECTOR", onClose);
		int slot = 0;
		for (final Kit kit : duel.getKits()) {
			if (slot + 1 > duel.getKits().size()) {
				break;
			}
			this.setItem(kit.getIcon(player), slot, (type) -> {
				kit.deploy(player);
				duel.setPlayerStatus(player, true);
				player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
			});
			slot++;
		}
		this.open();
	}
}
