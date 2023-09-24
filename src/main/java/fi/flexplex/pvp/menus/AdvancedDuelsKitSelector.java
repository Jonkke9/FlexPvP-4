package fi.flexplex.pvp.menus;

import fi.flexplex.pvp.game.kit.Kit;
import fi.flexplex.pvp.game.kit.KitManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;


public class AdvancedDuelsKitSelector extends Menu {

	final HashMap<Kit, Boolean> kits = new HashMap<>();

	public AdvancedDuelsKitSelector(final Player player, final Player target) {
		super(player, 54, "", null);


		for (final Kit kit : KitManager.getKits()) {
			final int slot = kit.getSlot();
			if (slot <= 44) {
				kits.put(kit, false);
				final ItemStack icon = kit.getIcon(player);
				icon.setType(Material.BARRIER);

				setItem(icon, slot, (type) -> {
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
		open();
	}
}
