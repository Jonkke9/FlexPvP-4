package fi.flexplex.pvp.command;

import fi.flexplex.core.api.Language;
import fi.flexplex.pvp.game.arena.ArenaManager;
import fi.flexplex.pvp.game.playerdata.PlayerDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpawnCmd implements CommandExecutor, TabCompleter {
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (!(sender instanceof Player player)) {
			Language.sendMessage(sender, "PLAYER_ONLY_COMMAND");
			return false;
		}
		PlayerDataManager.getPlayerData(player).changeArena(ArenaManager.getLobby(), true);
		return false;
	}

	@Override
	public List<String> onTabComplete(final CommandSender commandSender, final Command command, final String s, final String[] strings) {
		return new ArrayList<>();
	}
}
