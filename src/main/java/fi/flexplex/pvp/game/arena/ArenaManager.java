package fi.flexplex.pvp.game.arena;

import fi.flexplex.pvp.Main;
import fi.flexplex.pvp.game.playerdata.PlayerDataManager;
import fi.flexplex.pvp.misc.world.UlamSpiral;
import fi.flexplex.pvp.misc.world.WorldUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class ArenaManager {

	private static final HashMap<String, Arena> ARENAS = new HashMap<>();
	private static final HashMap<String, DuelArenaTemplate> TEMPLATES = new HashMap<>();
	private static final Set<DuelArenaBuildTask> BUILD_TASKS = new HashSet<>();

	private static final World DUEL_WORLD = WorldUtils.createEmptyWorld();
	private static final UlamSpiral SPIRAL = new UlamSpiral(DUEL_WORLD, 101);

	private static Lobby lobby = null;
	private static FfaArena activeFfaArena = null;

	private static int GEN_SPEED = 500;

	public static void addArena(final Arena arena) {
		ARENAS.putIfAbsent(arena.getName(), arena);
	}

	public static boolean hasArena(final String name) {
		return ARENAS.containsKey(name);
	}

	public static Lobby getLobby() {
		return lobby;
	}

	public static void setLobby(final Lobby arena) {
		if (arena != null) {
			lobby = arena;
		}
	}

	public static FfaArena getFfaArena() {
		return activeFfaArena;
	}

	public static void setActiveFfaArena(final FfaArena arena) {
		if (arena != null) {
			activeFfaArena = arena;
		}
	}

	public static void onDisable() {
		WorldUtils.deleteWorld(DUEL_WORLD);
	}

	public static void changeActiveFfaArena(final FfaArena newffaArena) {
		final FfaArena oldFfaArena = getFfaArena();
		oldFfaArena.broadcast("PVP_ARENA_CHANGING");
		for (final Player player : oldFfaArena.getPlayers()) {
			PlayerDataManager.getPlayerData(player).changeArena(ArenaManager.getLobby());
		}
		setActiveFfaArena(newffaArena);
	}

	public static DuelArena randomDuelArena() {
		final List<DuelArena> duelArenas = new ArrayList<>();
		for (final Arena arena : ARENAS.values()) {
			if (arena instanceof DuelArena) {
				if (((DuelArena) arena).isFree()) {
					duelArenas.add((DuelArena) arena);
				}
			}
		}

		if (duelArenas.size() > 0) {
			return duelArenas.get(new Random().nextInt(duelArenas.size()));
		}

		return buildArena(new ArrayList<>(TEMPLATES.values()).get(new Random().nextInt(TEMPLATES.values().size())));
	}

	public static DuelArena randomDuelArena(final List<DuelArenaTemplate> allowed) {
		if (allowed.size() == 0) {
			return randomDuelArena();
		}
		final DuelArenaTemplate template = allowed.get(new Random().nextInt(allowed.size()));

		for (final Arena arena : ARENAS.values()) {
			if (arena instanceof final DuelArena duelArena) {
				if (duelArena.isFree()) {
					if (duelArena.getTemplate() == template) {
						return duelArena;
					}
				}
			}
		}
		return buildArena(template);
	}

	public static DuelArena buildArena(final DuelArenaTemplate template) {
		final Location loc = SPIRAL.next();
		final DuelArena arena = template.buildArena(loc);
		final DuelArenaBuildTask task = new DuelArenaBuildTask(template, loc, arena);
		task.runTaskTimer(Main.getInstance(), getBuildTaskDelay(), 0L);
		BUILD_TASKS.add(task);
		ARENAS.putIfAbsent(arena.getName(), arena);
		return arena;
	}

	public static void addDuelArenaTemplate(final DuelArenaTemplate template) {
		if (template == null) return;

		TEMPLATES.putIfAbsent(template.getName(), template);
	}

	protected static int getBuildTaskDelay() {
		int delay = 0;
		for (final DuelArenaBuildTask task : BUILD_TASKS) {
			delay += task.getDuration();
		}
		return delay;
	}

	protected static void removeBuildTask(final DuelArenaBuildTask task) {
		BUILD_TASKS.remove(task);
	}

	public static void createAllDuelArenas() {
		for (final DuelArenaTemplate template : TEMPLATES.values()) {
			for (int i = 0; i < template.getAmount(); i++) {
				final DuelArena arena = buildArena(template);
				ARENAS.putIfAbsent(arena.getName(), arena);
			}
		}
		GEN_SPEED = 500;
	}

	public static Collection<DuelArenaTemplate> getAllDuelArenaTemplates() {
		return TEMPLATES.values();
	}

	public static int getGenerationSpeed() {
		return GEN_SPEED;
	}

}
