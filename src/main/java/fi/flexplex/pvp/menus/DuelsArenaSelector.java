package fi.flexplex.pvp.menus;

import fi.flexplex.core.api.Language;
import fi.flexplex.pvp.game.arena.ArenaManager;
import fi.flexplex.pvp.game.arena.DuelArenaTemplate;
import fi.flexplex.pvp.game.duel.DuelInvite;
import fi.flexplex.pvp.game.duel.DuelSettings;
import fi.flexplex.pvp.game.duel.Duels;
import fi.flexplex.pvp.game.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DuelsArenaSelector extends Menu{
	final HashMap<DuelArenaTemplate, Boolean> templates = new HashMap<>();
	public DuelsArenaSelector(final Player player, final Player target, final List<Kit> kits) {
		super(player, 27, "PVP_DUELS_MENU_ARENA", null);
		int slot = 0;
		for (final DuelArenaTemplate template : ArenaManager.getAllDuelArenaTemplates()) {
			final int slot1 = slot;
			if (slot <= 17) {
				templates.put(template, false);
				final ItemStack icon = template.getIcon(player);
				icon.setType(Material.BARRIER);

				setItem(icon, slot, (type) -> {
					final boolean active = templates.get(template);
					templates.put(template, !active);

					if (active) {
						this.inventory.getItem(slot1).setType(Material.BARRIER);
					} else {
						this.inventory.getItem(slot1).setType(template.getDisplayMaterial());
					}
				});
			}
			slot++;
		}

		final ItemStack arrow = new ItemStack(Material.ARROW);
		arrow.getItemMeta().displayName(Language.getMessage(player, "PVP_DUELS_MENU_NEXT"));
		setItem(arrow, 26, (type) -> {
			final List<DuelArenaTemplate> arenas = new ArrayList<>();

			for (final DuelArenaTemplate template : templates.keySet()) {
				if (templates.get(template)) {
					arenas.add(template);
				}
			}

			player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
			Duels.sendInvite(new DuelInvite(player, target, kits, DuelSettings.defaultSettings() ,ArenaManager.randomDuelArena(arenas)));

		});
		open();
	}
}
