package fi.flexplex.pvp.game.arena;

import fi.flexplex.core.api.FlexPlayer;
import fi.flexplex.pvp.Main;
import fi.flexplex.pvp.game.kit.KitManager;
import fi.flexplex.pvp.game.kit.Kit;
import fi.flexplex.pvp.misc.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.HashMap;


public final class Lobby extends Arena implements Listener {

	private final Location spawn;
	private final HashMap<String, Location> kitSelectorLocations;
	private final HashMap<ArmorStand, String> kitSelectors = new HashMap<>();


	public Lobby(final String name, final Location bounds1, final Location bounds2, final Location spawn, final HashMap<String, Location> kitSelectorLocations) {
		super(name, bounds1, bounds2);
		this.spawn = spawn;
		this.kitSelectorLocations = kitSelectorLocations;
		this.createArmorStands();
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	}

	@Override
	void onJoin(final Player player) {
		player.teleport(this.spawn);
		player.setGameMode(this.gameMode);
		Util.resetPlayer(player);
		FlexPlayer.getPlayer(player).setHotbarItemsActive(true);
	}

	@Override
	void onLeave(final Player player, final boolean causeDeath) {
		FlexPlayer.getPlayer(player).setHotbarItemsActive(false);
	}

	private void createArmorStands() {
		for (Entity entity : bounds1.getWorld().getEntities()) {
			if (entity.getCustomName() != null && entity.getCustomName().startsWith("KIT_")) {
				entity.remove();
			}
		}
		for (final String key : this.kitSelectorLocations.keySet()) {
			final Kit kit = KitManager.getKit(key);
			if (kit == null) {
				continue;
			}
			final ArmorStand armorStand = kit.spawnArmorStand(kitSelectorLocations.get(key));
			kitSelectors.put(armorStand, kit.getName());
		}
	}

	public void removeKitSelectors() {
		for (final ArmorStand stand : this.kitSelectors.keySet()) {
			stand.remove();
		}
	}

	@EventHandler
	public void onPlayerInterectAtEntity(final PlayerInteractAtEntityEvent event) {
		if (event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
			final ArmorStand armorStand = (ArmorStand) event.getRightClicked();

			if (this.kitSelectors.containsKey(armorStand)) {
				Bukkit.dispatchCommand(event.getPlayer(),"ffa " + kitSelectors.get(armorStand));
			}
		}
	}

}
