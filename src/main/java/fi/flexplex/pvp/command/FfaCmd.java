package fi.flexplex.pvp.command;

import fi.flexplex.core.api.Language;
import fi.flexplex.pvp.game.arena.ArenaManager;
import fi.flexplex.pvp.game.arena.FfaArena;
import fi.flexplex.pvp.game.arena.Lobby;
import fi.flexplex.pvp.game.kit.Kit;
import fi.flexplex.pvp.game.kit.KitManager;
import fi.flexplex.pvp.game.playerdata.PlayerData;
import fi.flexplex.pvp.game.playerdata.PlayerDataManager;
import fi.flexplex.pvp.menus.FFAKitSelector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public final class FfaCmd implements CommandExecutor, TabCompleter {
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		if (!(sender instanceof Player player)) {
			Language.sendMessage(sender, "PLAYER_ONLY_COMMAND");
			return false;
		}

		final PlayerData data = PlayerDataManager.getPlayerData(player);

		if (!(data.getArena() instanceof Lobby)) {
			Language.sendMessage(sender, "PVP_CMD_FFA_NOT_IN_SPAWN");
			return false;
		}

		if (args.length == 0) {
			new FFAKitSelector(player, false);
			return true;
		}

		final Kit kit = KitManager.getKit(args[0].toLowerCase());

		if (kit == null) {
			Language.sendMessage(player, "PVP_KIT_NOT_FOUND", args[0]);
			return false;
		}

		final FfaArena ffaArena = ArenaManager.getFfaArena();

		if (!ffaArena.isKitAllowed(kit)) {
			Language.sendMessage(player, "PVP_KIT_NOT_ALLOWED", args[0]);
			return false;
		}
		ArenaManager.getFfaArena().send(player, kit);

		return true;
	}

	public List<String> onTabComplete(final CommandSender commandSender, final Command command, final String s, final String[] args) {
		final List<String> tab = new ArrayList<>();

		if (args.length < 2) {
			for (final Kit kit : ArenaManager.getFfaArena().getAllowedKits()) {
				if (kit.getName().startsWith(args[0])) {
					tab.add(kit.getName());
				}
			}
		}

		return tab;
	}
}
