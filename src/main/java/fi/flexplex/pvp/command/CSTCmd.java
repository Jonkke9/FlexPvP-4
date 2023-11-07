package fi.flexplex.pvp.command;

import fi.flexplex.core.api.Language;
import fi.flexplex.pvp.game.playerdata.PlayerData;
import fi.flexplex.pvp.game.playerdata.PlayerDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class CSTCmd implements CommandExecutor, TabCompleter {
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {

		if (!(sender instanceof Player player)) {
			Language.sendMessage(sender, "PLAYER_ONLY_COMMAND");
			return false;
		}

		final PlayerData data = PlayerDataManager.getPlayerData(player);


		PlayerDataManager.TimeFrame timeFrame = data.timeFrame();

		if (args.length == 0) {
			timeFrame = timeFrame.next();

		} else {
			switch (args[0].toLowerCase()) {
				case "session" :
					timeFrame = PlayerDataManager.TimeFrame.THIS_SESSION;
					break;
				case "month" :
					timeFrame = PlayerDataManager.TimeFrame.MONTHLY;
					break;
				case "alltime" :
					timeFrame = PlayerDataManager.TimeFrame.ALL_TIME;
					break;
				default:
					timeFrame = timeFrame.next();
					break;
			}
		}

		data.timeFrame(timeFrame);
		Language.sendMessage(player, "PVP_TIMEFRAME_CHANGED", Language.getStringMessage(player, "PVP_TIMEFRAME_" + timeFrame.name()));
		return false;
	}

	@Override
	public @Nullable List<String> onTabComplete(final CommandSender commandSender, final Command command, final String s, final String[] args) {
		if (args.length == 1) {
			return List.of("session", "month", "alltime");
		} else return new ArrayList<>();
	}
}
