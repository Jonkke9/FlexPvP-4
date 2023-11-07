package fi.flexplex.pvp.command;

import fi.flexplex.core.api.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class LocationToolCmd implements CommandExecutor {
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		if (!(sender instanceof Player player)) {
			Language.sendMessage(sender, "PLAYER_ONLY_COMMAND");
			return false;
		}

		if (!player.isOp()) {
			Language.sendMessage(player, "NO_PERMISSIONS_TO_COMMAND");
			return false;
		}
		final Location loc = player.getLocation();

		final String worldname = loc.getWorld().getName();
		final String x = String.valueOf(loc.getX());
		final String y = String.valueOf(loc.getY());
		final String z = String.valueOf(loc.getZ());
		final String yaw = String.valueOf(loc.getYaw());
		final String pitch = String.valueOf(loc.getPitch());
		final String msg = worldname + " " + x + " " + y + " " + z + " " + yaw + " " + pitch;

		final TextComponent textComponent = Component.text()
				.content("§a§lLOCATION")
				.clickEvent(ClickEvent.copyToClipboard(msg))
				.hoverEvent(HoverEvent.showText(Component.text("§a§lCOPY")))
				.build();
		player.sendMessage(textComponent);
		return true;
	}
}
