package fi.flexplex.pvp.command;

import fi.flexplex.core.api.Language;
import fi.flexplex.pvp.game.playerdata.PlayerData;
import fi.flexplex.pvp.game.playerdata.PlayerDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CSTCmd implements CommandExecutor {
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {

		if (!(sender instanceof Player)) {
			Language.sendMessage(sender, "PLAYER_ONLY_COMMAND");
			return false;
		}

		//TODO: permission check

		final Player player = (Player) sender;
		final PlayerData data = PlayerDataManager.getPlayerData(player);

		data.timeFrame(data.timeFrame().next());

		return false;
	}
}
