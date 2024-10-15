package fi.flexplex.pvp.game.arena;

import fi.flexplex.core.api.FlexPlayer;
import fi.flexplex.core.api.Language;
import fi.flexplex.core.api.Permissions;
import fi.flexplex.pvp.Main;
import fi.flexplex.pvp.game.kit.Kit;
import fi.flexplex.pvp.game.playerdata.PlayerData;
import fi.flexplex.pvp.game.playerdata.PlayerDataManager;
import fi.flexplex.pvp.menus.FFAKitSelector;
import fi.flexplex.pvp.misc.Util;
import fi.flexplex.pvp.misc.scoreboard.PvpScoreboard;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public final class FfaArena extends PvpArena {

	private final List<Kit> allowedKits = new ArrayList<>();
	private final List<Location> spawnLocations;
	private final Random rand = new Random();
	private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

	private Team starPlayerTeam;

	public FfaArena(final String name, final Location bounds1, final Location bounds2, final List<Kit> allowedKits, final List<Location> spawnLocations) {
		super(name, bounds1, bounds2);
		this.allowedKits.addAll(allowedKits);

		if (scoreboard.getTeam("000StarPlayer") != null) {
			scoreboard.getTeam("000StarPlayer").unregister();
		}

		starPlayerTeam = scoreboard.registerNewTeam("000StarPlayer");
		starPlayerTeam.suffix(Component.text(" §e✰"));
		starPlayerTeam.color(NamedTextColor.YELLOW);
		Collections.sort(this.allowedKits, new Comparator<Kit>() {
			@Override
			public int compare(final Kit o1, final Kit o2) {
				return o1.getSlot() < o2.getSlot() ? -1 : o1.getSlot() == o2.getSlot() ? 0 : 1;
			}
		});

		this.spawnLocations = spawnLocations;

		Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
			for (final Player player : this.players) {
				if (player.getKiller() != null && player.getKiller() != player) {
					player.sendActionBar(Language.getMessage(player, "PVP_FFA_KILL_INDICATOR", player.getKiller().getName()));
				}
			}
		}, 20, 20);
	}

	@Override
	public void onDeath(final Player victim, final Player killer) {

		if (victim.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {
			victim.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
			Util.totemEffect(victim);
			return;

		} else if (victim.getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING) {
			victim.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
			Util.totemEffect(victim);
			return;
		}

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
				DamageSource.builder(DamageType.GENERIC_KILL).build(),
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

			final boolean isStarPlayer = isStarPlayer(victim);
			removeStarPlayer(victim);

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

				if (isStarPlayer) {
					setPlayerStarPlayer(killer);
					this.broadcast("PVP_FFA_STAR_STOLEN", FlexPlayer.getPlayer(killer).getLegacyDisplayName(), FlexPlayer.getPlayer(victim).getLegacyDisplayName());
				}

				final double max = killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

				if (killer.getHealth() + 4 <= max) {
					killer.setHealth(killer.getHealth() + 4);
				} else {
					killer.setHealth(max);
				}

				PvpScoreboard.updateFFABellowNameScoreboard(killer, (int) killer.getHealth());

				//Streak break message
				if (victimStreak >= 10) {
					this.broadcast("PVP_FFA_STREAK_BREAK", FlexPlayer.getPlayer(killer).getLegacyDisplayName(), String.valueOf(victimStreak), FlexPlayer.getPlayer(victim).getLegacyDisplayName());
				}

				//Streak announcement
				if ((streak + 1) % 5 == 0) {
					Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
						this.broadcast("PVP_FFA_STREAK_ANNOUNCE", FlexPlayer.getPlayer(killer).getLegacyDisplayName(), String.valueOf(streak + 1));
					}, 20 /* delay just for the aesthetics */);
				}

				if (!isStarPlayerActive()) {
					if (streak + 1 >= 25) {
						setPlayerStarPlayer(killer);
						this.broadcast("PVP_FFA_STAR_GIVEN", FlexPlayer.getPlayer(killer).getLegacyDisplayName());
					}
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
			} else {
				if (isStarPlayer(player)) {
					this.broadcast("PVP_FFA_STAR_QUIT", FlexPlayer.getPlayer(player).getLegacyDisplayName());
				}
			}
		}

		removeStarPlayer(player);
		PlayerDataManager.getPlayerData(player).resetCurrentStreak();
		PvpScoreboard.clearFFAScoreboards(player);
	}

	@Override
	void onJoin(final Player player) {
	}


	private void setPlayerStarPlayer(final Player player) {
		if (starPlayerTeam.getSize() == 0) {
			Permissions.getPlayerPrefix(player).whenCompleteAsync(((prefix, e) -> {
				if (e != null) {
					e.printStackTrace();
				}
				starPlayerTeam.prefix(Component.join(JoinConfiguration.noSeparators(), MiniMessage.miniMessage().deserialize(prefix.getValue()), Component.text("§e")));
			}));

			starPlayerTeam.addPlayer(player);
			player.setGlowing(true);
		}
	}

	private boolean isStarPlayer(final Player player) {
		return starPlayerTeam.hasPlayer(player);
	}

	private boolean isStarPlayerActive() {
		return starPlayerTeam.getSize() > 0;
	}

	private void removeStarPlayer(final Player player) {
		if (isStarPlayer(player)) {
			starPlayerTeam.removePlayer(player);
			FlexPlayer.getPlayer(player).resetPrefix();
			player.setGlowing(false);
		}
	}

	@Override
	public boolean hasSpawnDelay() {
		return true;
	}
}
