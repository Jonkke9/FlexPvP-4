package fi.flexplex.pvp.misc.world;

import fi.flexplex.pvp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.io.File;

public final class WorldUtils {

	private WorldUtils() {}

	private static int s_WorldCounter = 1;

	public static World createEmptyWorld() {
		final WorldCreator creator = WorldCreator.name(System.currentTimeMillis() + "-" + s_WorldCounter++
		);

		creator.environment(World.Environment.NORMAL);
		creator.generateStructures(false);
		creator.type(WorldType.FLAT);
		creator.generator(EmptyWorldGenerator.s_Instance);

		final World world = creator.createWorld();

		world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		world.setGameRule(GameRule.DISABLE_RAIDS, true);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
		world.setGameRule(GameRule.DO_FIRE_TICK, false);
		world.setGameRule(GameRule.DO_INSOMNIA, false);
		world.setGameRule(GameRule.DO_MOB_LOOT, false);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
		world.setGameRule(GameRule.DO_TILE_DROPS, false);
		world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
		world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		world.setGameRule(GameRule.KEEP_INVENTORY, true);
		world.setGameRule(GameRule.NATURAL_REGENERATION, false);
		world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
		world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
		world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
		world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
		world.setDifficulty(Difficulty.PEACEFUL);

		return world;
	}

	public static void deleteWorld(final World world) {
		final File directory = world.getWorldFolder();
		Bukkit.unloadWorld(world, false);
		deleteDirectory(directory);
	}

	public static void deleteWorldAsync(final World world) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			deleteWorld(world);
		});
	}

	public static void deleteDirectory(final File file) {
		for (final File child : file.listFiles()) {
			if (child.isDirectory()) {
				deleteDirectory(child);
			}
			child.delete();
		}
		file.delete();
	}

}
