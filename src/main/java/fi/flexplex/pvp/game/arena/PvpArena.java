package fi.flexplex.pvp.game.arena;

import fi.flexplex.pvp.Main;
import fi.flexplex.pvp.game.kit.Kit;
import fi.flexplex.pvp.misc.HealTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitTask;

public abstract class PvpArena extends Arena {

	BukkitTask healTask;

	public PvpArena(final String name, final Location bounds1, final Location bounds2) {
		super(name, bounds1, bounds2);
	}

	public abstract void onDeath(Player victim, Player killer);

	public abstract void send(Player player, Kit kit);

	public boolean allowDamage(EntityDamageEvent.DamageCause cause) {
		return false;
	}

	public void activate(final int healDelay) {
		this.healTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new HealTask(this), healDelay, healDelay);
	}

	public void deActivate() {
		healTask.cancel();
	}

}
