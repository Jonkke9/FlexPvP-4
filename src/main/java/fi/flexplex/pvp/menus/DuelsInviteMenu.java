package fi.flexplex.pvp.menus;

import fi.flexplex.core.api.Language;
import fi.flexplex.pvp.misc.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.Collection;

public final class DuelsInviteMenu extends Menu{
	public DuelsInviteMenu(final Player player, final Collection<Player> players) {
		super(player, Util.menuSize(players.size()), "PVP_DUELS_OPPONENT_SELECTOR_TITLE", null);

		int slot = 0;

		players.remove(player);


		for (final Player onlinePlayer : players) {
			if (slot <= inventory.getSize() - 1) {
				this.setItem(skull(onlinePlayer, Language.getMessage(player, "PVP_DUELS_OPPONENT_SELECT"), Language.getMessage(player, "PVP_DUELS_MENU_CLICK_ADVANCED")), slot, (type) -> {
					if (type.isRightClick() || type.isShiftClick()) {
						Bukkit.dispatchCommand(player, "advancedduelsinvite " + onlinePlayer.getName());
					} else {
						Bukkit.dispatchCommand(player, "duelsinvite " + onlinePlayer.getName());
					}
				});
			} else break;
			slot++;
		}

		this.open();
	}

	private ItemStack skull(final Player owner, Component... lore) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwningPlayer(owner);
		meta.setDisplayName(owner.getDisplayName());
		meta.lore(Arrays.asList(lore));
		skull.setItemMeta(meta);
		return skull;
	}
}
