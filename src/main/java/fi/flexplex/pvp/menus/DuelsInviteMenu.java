package fi.flexplex.pvp.menus;

import fi.flexplex.pvp.misc.Util;
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
			this.setItem(skull(onlinePlayer), slot, (type) -> {
				if (type.isLeftClick()) {
					Bukkit.dispatchCommand(player, "duelsinvite " + onlinePlayer.getName());
				} else if (type.isRightClick()) {
					Bukkit.dispatchCommand(player, "advancedduelsinvite " + onlinePlayer.getName());
				}
			});
		}

		this.open();
	}

	private ItemStack skull(final Player owner, String... lore) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwningPlayer(owner);
		meta.setDisplayName(owner.getDisplayName());
		meta.setLore(Arrays.asList(lore));
		skull.setItemMeta(meta);
		return skull;
	}
}
