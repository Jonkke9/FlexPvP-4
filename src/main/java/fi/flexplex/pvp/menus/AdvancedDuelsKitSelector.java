package fi.flexplex.pvp.menus;

import fi.flexplex.core.api.Language;
import fi.flexplex.pvp.game.kit.Kit;
import fi.flexplex.pvp.game.kit.KitManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdvancedDuelsKitSelector extends Menu {

	final HashMap<Kit, Boolean> kits = new HashMap<>();

	public AdvancedDuelsKitSelector(final Player player, final Player target) {
		super(player, 27, "PVP_DUELS_MENU_SELECT_KITS", null);

		for (final Kit kit : KitManager.getKits()) {
			final int slot = kit.getSlot();
			if (slot <= 25) {
				kits.put(kit, true);
				setItem(kit.getIcon(player), slot, (type) -> {
					final boolean active = kits.get(kit);
					kits.put(kit, !active);

					if (active) {
						this.inventory.getItem(slot).setType(Material.BARRIER);
					} else {
						this.inventory.getItem(slot).setType(kit.getDisabledMaterial());
					}
				});
			}
		}

		final ItemStack arrow = new ItemStack(Material.ARROW);
		final ItemMeta arrowMeta = arrow.getItemMeta();
		arrowMeta.displayName(Language.getMessage(player, "PVP_DUELS_MENU_NEXT"));
		arrow.setItemMeta(arrowMeta);

		setItem(arrow, 26, (type) -> {
			final List<Kit> allowedKits = new ArrayList<>();

			for (final Kit kit : kits.keySet()) {
				if (kits.get(kit)) {
					allowedKits.add(kit);
				}
			}

			if (allowedKits.isEmpty()) {
				allowedKits.addAll(KitManager.getFFAKits());
			}

			player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
			new DuelsArenaSelector(player, target, allowedKits);

		});
		open();
	}
}
