package fi.flexplex.pvp.misc;

import fi.flexplex.core.api.FlexPlayer;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public final class Util {

	public static void resetPlayer(final Player player) {
		player.setGlowing(false);
		player.getInventory().clear();
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		player.setFoodLevel(20);
		player.setLevel(0);

		for (final PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}

	public static int menuSize(final int items) {
		int i = 0;
		while (items > i) {
			i += 9;
		}
		if (i == 0) i = 9;
		return i;
	}

	public static Set<Player> getOnlinePlayersMinusVanished() {
		final Set<Player> onlinePlayers = new HashSet<>();

		for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (!FlexPlayer.getPlayer(onlinePlayer).isVanished()) {
				onlinePlayers.add(onlinePlayer);
			}
		}
		return onlinePlayers;
	}

	public static String getCurrentMonthKey() {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return "TOPLIST_MONTH_" + String.valueOf(calendar.get(Calendar.MONTH) + 1);
	}
	public static int getYear() {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}
}
