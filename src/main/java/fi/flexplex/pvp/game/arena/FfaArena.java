package fi.flexplex.pvp.game.arena;

import fi.flexplex.core.api.Language;
import fi.flexplex.core.api.Permissions;
import fi.flexplex.pvp.Main;
import fi.flexplex.pvp.game.kit.Kit;
import fi.flexplex.pvp.game.playerdata.PlayerData;
import fi.flexplex.pvp.game.playerdata.PlayerDataManager;
import fi.flexplex.pvp.menus.FFAKitSelector;
import fi.flexplex.pvp.misc.scoreboard.PvpScoreboard;
import fi.flexplex.pvp.misc.Util;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public final class FfaArena extends PvpArena {

	private final List<Kit> allowedKits = new ArrayList<>();
	private final List<Location> spawnLocations;
	private final Random rand = new Random();

	public FfaArena(final String name, final Location bounds1, final Location bounds2, final List<Kit> allowedKits, final List<Location> spawnLocations) {
		super(name, bounds1, bounds2);
		this.allowedKits.addAll(allowedKits);

		Collections.sort(this.allowedKits, new Comparator<Kit>() {
			@Override
			public int compare(final Kit o1, final Kit o2) {
				return o1.getSlot() < o2.getSlot() ? -1 : o1.getSlot() == o2.getSlot() ? 0 : 1;
			}
		});

		this.spawnLocations = spawnLocations;

		Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
			//TODO: .....
			for (final Player player : this.players) {
				if (player.getKiller() != null && player.getKiller() != player) {
					player.sendActionBar(Language.getMessage(player, "PVP_FFA_KILL_INDICATOR", player.getKiller().getName()));
				}
			}
		}, 20, 20);

	}


	@Override
	public void onDeath(final Player victim, final Player killer) {
		onFFADeath(victim, killer, false);
	}

	@Override
	public void send(final Player player, Kit kit) {
		if (kit == null) {
			return;
		}

		if (!this.hasPlayer(player)) {
			PlayerDataManager.getPlayerData(player).changeArena(this);
		}

		player.teleport(this.spawnLocations.get(this.rand.nextInt(this.spawnLocations.size())));
		player.setGameMode(this.gameMode);
		Util.resetPlayer(player);
		kit.deploy(player);
		PvpScoreboard.sendFFASidebarScoreboard(player);
		PvpScoreboard.sendFFAListScoreboard(player);
		PvpScoreboard.sendFFABellowNameScoreboard(player);

		player.playSound(Sound.sound(
				org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING.key(),
				Sound.Source.AMBIENT,
				1F,
				0.4F
		));

	}

	private void onFFADeath(final Player victim, final Player killer, final boolean combatLog) {

		final PlayerDeathEvent deathEvent = new PlayerDeathEvent(
				victim,
				Collections.emptyList(),
				0,
				""
		);

		deathEvent.setDeathSound(org.bukkit.Sound.BLOCK_ANVIL_PLACE);
		deathEvent.setDeathSoundCategory(SoundCategory.PLAYERS);
		deathEvent.setDeathSoundPitch(1.0F);
		deathEvent.setDeathSoundVolume(1.0F);
		deathEvent.setShouldPlayDeathSound(true);

		final PlayerData victimData = PlayerDataManager.getPlayerData(victim);
		final int victimStreak = victimData.getCurrentStreak();


		if (victimData.getCurrentStreak() < 10 || killer == null) {
			if (victim.getKiller() != killer && victim.getKiller() != victim) {
				victim.setKiller(killer);
			}
			Bukkit.getPluginManager().callEvent(deathEvent);
		}


		if (!deathEvent.isCancelled()) {

			PlayerDataManager.onFfaDeath(victim);
			PvpScoreboard.updateFFAListScore(victim);

			if (victimStreak >= 10) {
				final Location location = victim.getLocation().add(0, 1, 0);

				victim.getWorld().playSound(location, org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 1.5F, 0.5F);
				victim.getWorld().spawnParticle(Particle.CLOUD, location, (victimStreak + 1) * 15);
			}


			if (killer != null) {
				final PlayerData killerData = PlayerDataManager.getPlayerData(killer);
				final int streak = killerData.getCurrentStreak();
				PlayerDataManager.onFfaKill(killer, streak + 1);
				killer.setLevel(streak + 1);
				PvpScoreboard.updateFFAListScore(killer);
				PvpScoreboard.sendFFASidebarScoreboard(killer);

				final double max = killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

				if (killer.getHealth() + 4 <= max) {
					killer.setHealth(killer.getHealth() + 4);
				} else {
					killer.setHealth(max);
				}

				PvpScoreboard.updateFFABellowNameScoreboard(killer);

				//Streak break message
				if (victimStreak >= 10) {
					this.broadcast("PVP_FFA_STREAK_BREAK", Permissions.getLegacyDisplayName(killer), String.valueOf(victimStreak),  Permissions.getLegacyDisplayName(victim));
				}

				//Streak announcement
				if ((streak + 1) % 5 == 0) {
					Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
						this.broadcast("PVP_FFA_STREAK_ANNOUNCE", Permissions.getLegacyDisplayName(killer), String.valueOf(streak +1));
					}, 20 /* delay just for the aesthetics */);
				}
			}

			if (deathEvent.shouldPlayDeathSound()) {
				victim.getWorld().playSound(
						victim.getLocation(),
						deathEvent.getDeathSound(),
						deathEvent.getDeathSoundCategory(),
						deathEvent.getDeathSoundVolume(),
						deathEvent.getDeathSoundPitch()
				);
			}
			victim.setKiller(null);

			if (!combatLog) {
				victim.getInventory().clear();
				victim.setGameMode(GameMode.SPECTATOR);
				Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
					if (this.hasPlayer(victim)) {
						new FFAKitSelector(victim, true);
					}
				}, 20);
			}
		}
	}

	public boolean isKitAllowed(final Kit kit) {
		return this.allowedKits.contains(kit);
	}

	public void reSpawn(final Player player) {
		this.onJoin(player);
	}

	public List<Kit> getAllowedKits() {
		return this.allowedKits;
	}

	@Override
	public boolean allowDamage(final EntityDamageEvent.DamageCause cause) {
		return true;
	}

	@Override
	void onLeave(final Player player, final boolean causeDeath) {
		if (causeDeath) {
			if (player.getKiller() != null && player.getKiller() != player) {
				onFFADeath(player, player.getKiller(), true);
			}
		}
		PlayerDataManager.getPlayerData(player).resetCurrentStreak();
		PvpScoreboard.clearFFAScoreboards(player);
	}

	@Override
	void onJoin(final Player player) {

	}
}
