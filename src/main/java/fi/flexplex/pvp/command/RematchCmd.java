package fi.flexplex.pvp.command;

import fi.flexplex.core.api.Language;
import fi.flexplex.pvp.game.duel.Duel;
import fi.flexplex.pvp.game.duel.DuelState;
import fi.flexplex.pvp.game.duel.Duels;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RematchCmd implements CommandExecutor, TabCompleter {
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] strings) {

		if (!(sender instanceof Player player)) {
			Language.sendMessage(sender, "PLAYER_ONLY_COMMAND");
			return false;
		}

		final Duel duel = Duels.getPlayerDuel(player);

		if (duel == null || duel.getState() != DuelState.WAITING) {
			Language.sendMessage(player, "PVP_DUELS_REMATCH_CANT_BE_SENT");
			return false;
		}

		duel.rematchRequest(player);
		Language.sendMessage(sender, "PVP_DUELS_REMATCH_SENT");
		return false;
	}

	@Override
	public @Nullable List<String> onTabComplete(final CommandSender commandSender, final Command command, final String s, final String[] strings) {
		return new ArrayList<>();
	}
}
