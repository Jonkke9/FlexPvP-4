package fi.flexplex.pvp.command;

import fi.flexplex.core.api.Language;
import fi.flexplex.pvp.menus.AdvancedDuelsKitSelector;
import fi.flexplex.pvp.menus.DuelsInviteKitSelector;
import fi.flexplex.pvp.menus.DuelsInviteMenu;
import fi.flexplex.pvp.misc.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DuelInviteCmd implements CommandExecutor , TabCompleter {
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {

		if (!(sender instanceof Player player)) {
			Language.sendMessage(sender, "PLAYER_ONLY_COMMAND");
			return false;
		}

		final Collection<Player> online = Util.getOnlinePlayersMinusVanished();

		if (online.size() <= 1) {
			Language.sendMessage(sender, "PVP_DUELS_OPPONENT_NOBODY");
			return false;
		}

		if (args.length == 0) {
			new DuelsInviteMenu(player, online);

		} else {
			Player target = null;

			for (final Player p : online) {
				if (p.getName().equalsIgnoreCase(args[0])) {
					target = p;
					break;
				}
			}
			if (target == null) {
				Language.sendMessage(sender, "PLAYER_NOT_FOUND");
				return false;
			}

			if (target == player) {
				Language.sendMessage(sender, "PVP_DUELS_CANNOT_INVITE_SELF");
				return false;
			}

			if (command.getName().equalsIgnoreCase("advancedduelsinvite")) {
				new AdvancedDuelsKitSelector(player, target);
				return true;
			}
			new DuelsInviteKitSelector(player, target);
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String s, final String[] args) {
		final List<String> names = new ArrayList<>();

		if (args.length == 1) {
			Util.getOnlinePlayersMinusVanished().forEach((player) ->{
				names.add(player.getName());
			});
		}
		return names;
	}
}
