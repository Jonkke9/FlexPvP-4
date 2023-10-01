package fi.flexplex.pvp.game.arena;

import fi.flexplex.pvp.game.duel.Duel;
import fi.flexplex.pvp.game.duel.DuelState;
import fi.flexplex.pvp.game.kit.Kit;
import fi.flexplex.pvp.game.playerdata.PlayerDataManager;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class DuelArena extends PvpArena{

	private final Location[] locations;
	private final DuelArenaTemplate template;
	private Duel activeDuel;

	private boolean ready;

	public DuelArena(final String name, final Location bounds1, final Location bounds2, final Location[] locations, final DuelArenaTemplate template) {
		super(name, bounds1, bounds2);
		this.template = template;
		this.locations = locations;
		activeDuel = null;
		ready = false;
	}

	@Override
	public void onJoin(final Player player) {
		if (activeDuel == null || !ready) {
			PlayerDataManager.getPlayerData(player).changeArena(ArenaManager.getLobby());
			return;
		}

		if (player == activeDuel.fromPlayer()) {
			player.teleport(locations[0]);
		}else if (player == activeDuel.toPlayer()) {
			player.teleport(locations[1]);
		} else return;

		player.setGameMode(activeDuel.getSettings().gameMode());
	}

	@Override
	void onLeave(final Player player, boolean causeDeath) {
		if (activeDuel != null) {
			activeDuel.onLeave(player);
		}
	}

	@Override
	public void onDeath(final Player victim, final Player killer) {
		if (victim.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {
			victim.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
			totemEffect(victim);
			return;
		} else if (victim.getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING) {
			victim.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
			totemEffect(victim);
			return;
		}
		if (activeDuel != null) {
			activeDuel.onDeath(victim, true);
		}
	}

	@Override
	public void send(final Player player, final Kit kit) {

	}

	@Override
	public boolean allowDamage(final EntityDamageEvent.DamageCause cause) {
		if (activeDuel == null) {
			return false;
		}
		return activeDuel.getState() == DuelState.ACTIVE;
	}

	public boolean isFree() {
		return activeDuel == null;
	}

	public void setDuel(final Duel duel) {
		activeDuel = duel;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(final boolean ready) {
		this.ready = ready;
	}

	@Override
	public boolean allowHunger() {
		if (activeDuel == null) {
			return false;
		}

		return activeDuel.getSettings().hunger();
	}

	public DuelArenaTemplate getTemplate() {
		return template;
	}


	private void totemEffect(final Player player) {
		player.playEffect(EntityEffect.TOTEM_RESURRECT);
		player.setHealth(1.0);
		player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 2));
		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 45 * 20, 2));
		player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 40 * 20, 1));
	}
}
