package fi.flexplex.pvp.game.arena;

import fi.flexplex.core.api.Language;
import fi.flexplex.core.api.Permissions;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class Arena {

	protected final String name;
	protected final Set<Player> players;
	protected final Location bounds1;
	protected final Location bounds2;
	protected GameMode gameMode = GameMode.ADVENTURE;

	public Arena(final String name, final Location bounds1, final Location bounds2) {
		this.name = name;
		this.bounds1 = bounds1;
		this.bounds2 = bounds2;
		this.players = new HashSet<>();
	}

	public void broadcast(final String key, final String... args) {
		for (final Player p : players) {
			Language.sendMessage(p, key, args);
		}
	}

	public boolean hasPlayer(final Player player) {
		return players.contains(player);
	}

	public boolean isEmpty() {
		return players.isEmpty();
	}

	public String getName() {
		return name;
	}


	public Set<Player> getPlayers() {
		return this.players;
	}

	//try to use PlayerData.changeArena() instead if possible. these are only mend to be used when player logs in or out
	public boolean addPlayer(final Player player) {
		players.add(player);
		player.getInventory().clear();
		for (final PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		onJoin(player);
		return true;
	}


	public boolean removePlayer(final Player player, final boolean causeDeath) {
		players.remove(player);
		player.getInventory().clear();
		onLeave(player, causeDeath);
		return true;
	}

	public boolean allowHunger() {
		return false;
	}

	abstract void onJoin(final Player player);

	abstract void onLeave(final Player player, final boolean causeDeath);


}
