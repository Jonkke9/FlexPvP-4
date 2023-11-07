package fi.flexplex.pvp.misc;

import fi.flexplex.core.api.FlexPlayer;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public final class Util {

	public static void resetPlayer(final Player player) {
		player.getInventory().clear();
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		player.getOpenInventory().setCursor(null);
		player.setFoodLevel(20);
		player.setLevel(0);
		player.setGlowing(false);

		if (player.getOpenInventory().getTopInventory() instanceof CraftingInventory cinv) {
			if (cinv.getMatrix().length == 4) {
				cinv.clear();
			}
		}

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

		if (i >= 54) i = 54;

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
		return "TOPLIST_MONTH_" + (calendar.get(Calendar.MONTH) + 1);
	}
	public static int getYear() {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	public static void totemEffect(final Player player) {
		player.playEffect(EntityEffect.TOTEM_RESURRECT);
		player.setHealth(1.0);
		player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 2));
		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 45 * 20, 2));
		player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 40 * 20, 1));
	}

	public static boolean isInArea(final Location loc, final Location pos1, final Location pos2) {
		final Location smallestCorner = new Location(loc.getWorld(),
				Math.min(pos1.getX(), pos2.getX()),
				Math.min(pos1.getY(), pos2.getY()),
				Math.min(pos1.getZ(), pos2.getZ())
		);

		final Location largestCorner = new Location(loc.getWorld(),
				Math.max(pos1.getX(), pos2.getX()),
				Math.max(pos1.getY(), pos2.getY()),
				Math.max(pos1.getZ(), pos2.getZ())
		);

		if (smallestCorner.getX() <= loc.getX() && loc.getX() <= largestCorner.getX() &&
				(smallestCorner.getY() <= loc.getY() && loc.getY() <= largestCorner.getY()) &&
				(smallestCorner.getZ() <= loc.getZ() && loc.getZ() <= largestCorner.getZ())) {
			return true;
		}
		return false;
	}
}
