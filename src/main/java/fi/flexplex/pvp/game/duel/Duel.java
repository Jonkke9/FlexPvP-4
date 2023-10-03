package fi.flexplex.pvp.game.duel;

import fi.flexplex.core.api.Language;
import fi.flexplex.core.api.Permissions;
import fi.flexplex.pvp.Main;
import fi.flexplex.pvp.game.arena.ArenaManager;
import fi.flexplex.pvp.game.arena.DuelArena;
import fi.flexplex.pvp.game.kit.Kit;
import fi.flexplex.pvp.game.playerdata.PlayerDataManager;
import fi.flexplex.pvp.menus.DuelsKitSelector;
import fi.flexplex.pvp.misc.Util;
import fi.flexplex.pvp.misc.scoreboard.PvpScoreboard;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public final class Duel implements Listener {

	private BukkitTask delayTask;
	private BukkitTask rematchTask;

	private final Player fromPlayer;
	private final Player toPlayer;
	private final List<Kit> kits;
	private final DuelArena arena;
	private final DuelSettings settings;
	private DuelState state = DuelState.COUNTDOWN;
	private int fromPlayerScore;
	private int toPlayerScore;

	private boolean fromPlayerRematch;
	private boolean toPlayerRematch;

	private boolean fromPlayerStatus;
	private boolean toPlayerStatus;

	public Duel(final Player fromPlayer, final Player toPlayer, final List<Kit> kits, final DuelArena arena, final DuelSettings settings) {
		this.fromPlayer = fromPlayer;
		this.toPlayer = toPlayer;
		this.kits = kits;
		this.arena = arena;
		this.settings = settings;
		this.fromPlayerScore = 0;
		this.toPlayerScore = 0;
		this.fromPlayerRematch = false;
		this.toPlayerRematch = false;
		this.fromPlayerStatus = false;
		this.toPlayerStatus = false;
	}

	private void onRoundStart() {
		state = DuelState.COUNTDOWN;
		fromPlayerRematch = false;
		toPlayerRematch = false;

		arena.onJoin(fromPlayer);
		arena.onJoin(toPlayer);

		PvpScoreboard.sendDuelsSidebarScoreboard(
				fromPlayer,
				toPlayer,
				toPlayerScore,
				fromPlayerScore,
				Language.getStringMessage(fromPlayer, arena.getTemplate().getDisplayNameKey())
		);

		PvpScoreboard.sendDuelsSidebarScoreboard(
				toPlayer,
				fromPlayer,
				fromPlayerScore,
				toPlayerScore,
				Language.getStringMessage(toPlayer, arena.getTemplate().getDisplayNameKey())
		);

		if (kits.size() == 1) {
			kits.get(0).deploy(fromPlayer);
			kits.get(0).deploy(toPlayer);
			fromPlayerStatus = true;
			toPlayerStatus = true;
		} else {
			new DuelsKitSelector(fromPlayer, this, (player) -> {
				onLeave(player);
			});
			new DuelsKitSelector(toPlayer, this, (player) -> {
				onLeave(player);
			});
		}

		delayTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
			int counter = 3;

			@Override
			public void run() {
				if (counter <= 0) {
					delayTask.cancel();
					state = DuelState.ACTIVE;
					fromPlayer.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Sound.Source.AMBIENT, 1.0f, 1.0f));
					toPlayer.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Sound.Source.AMBIENT, 1.0f, 1.0f));
					counter--;
				} else if (toPlayerStatus && fromPlayerStatus) {
					final Component component = Component.text("§6" + counter);
					fromPlayer.showTitle(Title.title(component, Component.empty()));
					toPlayer.showTitle(Title.title(component, Component.empty()));
					toPlayer.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Sound.Source.AMBIENT, 1.0f, 0.5f));
					fromPlayer.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Sound.Source.AMBIENT, 1.0f, 0.5f));
					counter--;
				}
			}
		}, 0, 20);
	}

	public void onRoundEnd() {

		state = DuelState.WAITING;
		fromPlayerStatus = false;
		toPlayerStatus = false;

		sendRematchMessage(fromPlayer);
		sendRematchMessage(toPlayer);

		rematchTask = Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				if (getState() == DuelState.WAITING) {
					if (fromPlayerRematch && toPlayerRematch) {
						onRoundStart();
					} else {
						stop();
					}
				}
			}
		}, 140);
	}

	public void onDeath(final Player player, final boolean resume) {

		final Player killer;

		if (player == toPlayer) {
			killer = fromPlayer;
			fromPlayerScore++;
		} else if (player == fromPlayer) {
			killer = toPlayer;
			toPlayerScore++;
		} else {
			return;
		}

		onWin(killer, player);

		Language.sendMessage(killer, "PVP_DUELS_VICTORY", Permissions.getLegacyDisplayName(player));
		Language.sendMessage(player, "PVP_DUELS_DEFEAT", Permissions.getLegacyDisplayName(killer));

		killer.playSound(Sound.sound(org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, Sound.Source.AMBIENT, 1.0f, 1.0f));
		player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_BLAZE_DEATH, Sound.Source.AMBIENT, 1.0f, 1.0f));

		new BukkitRunnable() {
			int count = 1;

			@Override
			public void run() {
				final Location loc = killer.getLocation();
				final Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
				final FireworkMeta fwm = fw.getFireworkMeta();
				fwm.setPower(1);
				fwm.addEffect(FireworkEffect.builder().withColor(Color.ORANGE).withColor(Color.YELLOW).withFade(Color.YELLOW).withTrail().build());
				fw.setFireworkMeta(fwm);
				if (count >= 5)
					this.cancel();
				count++;
			}
		}.runTaskTimer(Main.getInstance(), 0, 20);

		if (!resume) {
			return;
		}

		onRoundEnd();
		Util.resetPlayer(toPlayer);
		Util.resetPlayer(fromPlayer);
	}

	public void start() {
		arena.activate(settings.regenSpeed());
		Duels.activateDuel(this);
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
		PlayerDataManager.getPlayerData(fromPlayer).changeArena(arena, true);
		PlayerDataManager.getPlayerData(toPlayer).changeArena(arena, true);
		onRoundStart();
	}

	private void stop() {
		state = DuelState.STOPPING;
		if (rematchTask != null) {
			rematchTask.cancel();
		}

		if (delayTask != null) {
			delayTask.cancel();
		}

		if (fromPlayerScore > 0 || toPlayerScore > 0) {
			announceResults();
		}
		arena.deActivate();
		if (toPlayer.isOnline()) {
			PvpScoreboard.clearDuelsScoreboards(toPlayer);
			PlayerDataManager.getPlayerData(toPlayer).changeArena(ArenaManager.getLobby());
		}

		if (fromPlayer.isOnline()) {
			PvpScoreboard.clearDuelsScoreboards(fromPlayer);
			PlayerDataManager.getPlayerData(fromPlayer).changeArena(ArenaManager.getLobby());
		}
		arena.setDuel(null);
		Duels.deActivateDuel(this);
		HandlerList.unregisterAll(this);
	}

	@EventHandler
	public void onPlayerMove(final PlayerMoveEvent event) {
		if (state == DuelState.COUNTDOWN) {
			if (arena.getPlayers().contains(event.getPlayer())) {
				event.setTo(new Location(
						event.getFrom().getWorld(),
						event.getFrom().getX(),
						event.getFrom().getY(),
						event.getFrom().getZ(),
						event.getTo().getYaw(),
						event.getTo().getPitch()
				));
			}
		}
	}

	public void onLeave(final Player player) {
		if (state != DuelState.STOPPING) {
			final Player opponent = player == fromPlayer ? toPlayer : fromPlayer;

			Language.sendMessage(player, "PVP_DUELS_CANCEL_YOU");
			Language.sendMessage(opponent, "PVP_DUELS_CANCEL_OTHER");

			if (state == DuelState.ACTIVE) {
				onDeath(player, false);
			}
			stop();
		}
	}

	public void announceResults() {
		if (toPlayerScore == fromPlayerScore) {
			tieAnnouncement();
		} else {
			final Player winner;
			final Player loser;
			final int winnerScore;
			final int loserScore;

			if (fromPlayerScore > toPlayerScore) {
				winner = fromPlayer;
				loser = toPlayer;
				winnerScore = fromPlayerScore;
				loserScore = toPlayerScore;
			} else {
				winner = toPlayer;
				loser = fromPlayer;
				winnerScore = toPlayerScore;
				loserScore = fromPlayerScore;
			}

			victoryAnnouncement(winner, loser, winnerScore, loserScore);
		}
	}

	private void tieAnnouncement() {
		final String from = Permissions.getLegacyDisplayName(fromPlayer);
		final String to = Permissions.getLegacyDisplayName(toPlayer);
		final String score = String.valueOf(fromPlayerScore);
		for (final Player player : Bukkit.getOnlinePlayers()) {
			Language.sendMessage(player, "PVP_DUELS_ANNOUNCE_TIE", from, to, score, score);
		}

	}

	private void victoryAnnouncement(final Player winner, final Player loser, final int winnerScore, final int loserScore) {
		final String winnerName = Permissions.getLegacyDisplayName(winner);
		final String loserName = Permissions.getLegacyDisplayName(loser);
		final String winnersScoreS = toString().valueOf(winnerScore);
		final String losersScoreS = toString().valueOf(loserScore);
		for (final Player player : Bukkit.getOnlinePlayers()) {
			Language.sendMessage(player, "PVP_DUELS_ANNOUNCE", winnerName, loserName, winnersScoreS, losersScoreS);
		}
	}

	public void rematchRequest(final Player player) {
		if (player == toPlayer) {
			toPlayerRematch = true;
			Language.sendMessage(fromPlayer, "PVP_DUELS_REMATCH_RECEIVED", Permissions.getLegacyDisplayName(toPlayer));
		} else if (player == fromPlayer) {
			fromPlayerRematch = true;
			Language.sendMessage(toPlayer, "PVP_DUELS_REMATCH_RECEIVED", Permissions.getLegacyDisplayName(fromPlayer));
		} else return;

		if (toPlayerRematch && fromPlayerRematch) {
			onRoundStart();
		}
	}

	public void setPlayerStatus(final Player player, final boolean status) {
		if (player == toPlayer) {
			toPlayerStatus = status;
		} else if (player == fromPlayer) {
			fromPlayerStatus = status;
		}
	}

	private void sendRematchMessage(final Player player) {
		final TextComponent rematch = Component.text()
				.content(Language.getStringMessage(player, "PVP_DUELS_REMATCH"))
				.hoverEvent(HoverEvent.showText(Component.text("§a/rematch")))
				.clickEvent(ClickEvent.runCommand("/rematch"))
				.build();

		final TextComponent stop = Component.text()
				.content(Language.getStringMessage(player, "PVP_DUELS_END"))
				.hoverEvent(HoverEvent.showText(Component.text("§c/spawn")))
				.clickEvent(ClickEvent.runCommand("/spawn"))
				.build();

		player.sendMessage(Component.text("§6§m---------------------------------------"));

		player.sendMessage(Component.text()
				.append(rematch)
				.append(stop)
				.build());

		player.sendMessage(Component.text("§6§m---------------------------------------"));
	}

	private void onWin(final Player winner, final Player loser) {
		PlayerDataManager.onRankedDuelWin(winner);
		PlayerDataManager.onRankedDuelLoss(loser);
	}

	public Player fromPlayer() {
		return fromPlayer;
	}

	public Player toPlayer() {
		return toPlayer;
	}

	public DuelState getState() {
		return state;
	}

	public List<Kit> getKits() {
		return kits;
	}

	public DuelSettings getSettings() {
		return settings;
	}
}

