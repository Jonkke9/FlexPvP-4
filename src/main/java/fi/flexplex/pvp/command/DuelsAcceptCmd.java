package fi.flexplex.pvp.command;

import fi.flexplex.core.api.Language;
import fi.flexplex.pvp.game.duel.DuelInvite;
import fi.flexplex.pvp.game.duel.Duels;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DuelsAcceptCmd implements CommandExecutor, TabCompleter {
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {

		if (!(sender instanceof Player)) {
			Language.sendMessage(sender, "PLAYER_ONLY_COMMAND");
			return false;
		}

		final Player player = (Player) sender;

		if (Duels.isInDuel(player)) {
			Language.sendMessage(sender, "PVP_DUELS_CANNOT_SEND");
			return false;
		}

		final ArrayList<DuelInvite> invites = Duels.getInvitesTo(player);

		if (args.length == 0) {
			if (invites.size() > 0) {
				Duels.acceptInvite(invites.get(0));
				return true;
			}
			Language.sendMessage(player, "PVP_DUELS_NO_INVITES");
			return false;
		}

		final Player target = Bukkit.getPlayerExact(args[0]);

		if (target == null) {
			Language.sendMessage(player, "PVP_DUELS_ACCEPT_NOT_FOUND");
			return false;
		}

		for (final DuelInvite invite : invites) {
			if (invite.getFrom() == target) {
				Duels.acceptInvite(invite);
			}
			return true;
		}

		Language.sendMessage(player, "PVP_DUELS_ACCEPT_NOT_FOUND");

		return false;
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		if (!(sender instanceof Player)) return new ArrayList<>();
		if (args.length > 1) return new ArrayList<>();

		final ArrayList<String> tab = new ArrayList<>();

		for (final DuelInvite invite : Duels.getInvitesTo((Player) sender)) {
			if (invite.getFrom().getName().toLowerCase().contains(args[0].toLowerCase())) tab.add(invite.getFrom().getName());
		}

		return tab;
	}

}
