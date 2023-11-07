package fi.flexplex.pvp.game.arena;

import fi.flexplex.core.api.FlexPlayer;
import fi.flexplex.core.api.Teleport;
import fi.flexplex.pvp.Main;
import fi.flexplex.pvp.game.kit.Kit;
import fi.flexplex.pvp.game.kit.KitManager;
import fi.flexplex.pvp.game.playerdata.PlayerData;
import fi.flexplex.pvp.game.playerdata.PlayerDataManager;
import fi.flexplex.pvp.misc.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.HashMap;


public final class Lobby extends Arena implements Listener {

	private final HashMap<String, Location> kitSelectorLocations;
	private final HashMap<ArmorStand, String> kitSelectors = new HashMap<>();


	public Lobby(final String name, final Location bounds1, final Location bounds2, final HashMap<String, Location> kitSelectorLocations) {
		super(name, bounds1, bounds2);
		this.kitSelectorLocations = kitSelectorLocations;
		this.createArmorStands();
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	}

	@Override
	void onJoin(final Player player) {
		player.teleport(Teleport.getSpawnLocation());
		player.setGameMode(this.gameMode);
		FlexPlayer.getPlayer(player).setHotbarItemsActive(true);
	}

	@Override
	void onLeave(final Player player, final boolean causeDeath) {
		FlexPlayer.getPlayer(player).setHotbarItemsActive(false);
	}

	private void createArmorStands() {
		for (final String key : this.kitSelectorLocations.keySet()) {
			final Kit kit = KitManager.getKit(key);
			if (kit == null) {
				continue;
			}
			final ArmorStand armorStand = kit.spawnArmorStand(kitSelectorLocations.get(key));
			kitSelectors.put(armorStand, kit.getName());
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
