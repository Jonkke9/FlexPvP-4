package fi.flexplex.pvp.menus;

import fi.flexplex.core.api.Language;
import fi.flexplex.pvp.game.arena.ArenaManager;
import fi.flexplex.pvp.game.duel.DuelInvite;
import fi.flexplex.pvp.game.duel.DuelSettings;
import fi.flexplex.pvp.game.duel.Duels;
import fi.flexplex.pvp.game.kit.KitManager;
import fi.flexplex.pvp.game.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;


public final class DuelsInviteKitSelector extends Menu{
	final Player target;

	public DuelsInviteKitSelector(final Player player, final Player target) {
		super(player, 9, "PVP_MENU_KIT_SELECTOR", null);

		this.target = target;

		for (final Kit kit : KitManager.getKits()) {
			if (kit.isFfaKit()) {
				this.setItem(kit.getIcon(player), kit.getSlot(), (type) -> {
					Duels.sendInvite(new DuelInvite(player, target, List.of(kit), DuelSettings.defaultSettings(), ArenaManager.randomDuelArena()));
					player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
				});
			}
		}

		final ItemStack selectAll = new ItemStack(Material.NETHER_STAR);
		final ItemMeta meta = selectAll.getItemMeta();
		meta.displayName(Language.getMessage(player, "PVP_DUELS_KIT_SELECTOR_FREE"));
		selectAll.setItemMeta(meta);

		this.setItem(selectAll, 8, (type) -> {
			Duels.sendInvite(new DuelInvite(player, target, KitManager.getFFAKits(), DuelSettings.defaultSettings(), ArenaManager.randomDuelArena()));
			player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
		});

		this.open();
	}
}