package fi.flexplex.pvp.loader;

import fi.flexplex.pvp.Main;
import fi.flexplex.pvp.game.arena.ArenaManager;
import fi.flexplex.pvp.game.arena.DuelArenaTemplate;
import fi.flexplex.pvp.game.arena.FfaArena;
import fi.flexplex.pvp.game.arena.Lobby;
import fi.flexplex.pvp.game.kit.Kit;
import fi.flexplex.pvp.game.kit.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ArenaLoader {
	public static void loadAllArenas() {
		final List<YamlConfiguration> configs = ConfigLoader.loadConfigFilesInDirectory(new File(Main.getInstance().getDataFolder(), "arenas"));
		for (final YamlConfiguration config : configs) {

			final String name = config.getString("name").toLowerCase();
			if (name == null) {
				Main.getInstance().getLogger().severe("could not load arena: incorrect name");
				continue;
			}
			final String type = config.getString("type").toLowerCase();

			if (type == null) {
				Main.getInstance().getLogger().severe("could not load arena " + name + ": invalid type");
				continue;
			}

			if (ArenaManager.hasArena(name)) {
				Main.getInstance().getLogger().severe("Arena by name: " + name + " already exists");
				continue;
			}

			final Location bounds1 = parseLocation(config.getString("bounds1"));
			final Location bounds2 = parseLocation(config.getString("bounds2"));


			if (bounds1 == null || bounds2 == null) {
				Main.getInstance().getLogger().severe("could not load arena " + name + ": invalid bounds");
				continue;
			}

			switch (type) {
				case "ffa":
					ArenaManager.addArena(loadFfaArena(config, name, bounds1, bounds2));
					break;
				case "lobby":
					ArenaManager.addArena(loadLobby(config, name, bounds1, bounds2));
					break;
				case  "duel":
					loadDuelArenaTemplate(config, name, bounds1, bounds2);
					break;
				default:
					Main.getInstance().getLogger().severe("could not load arena " + name + ": invalid type");
			}
		}

		if (ArenaManager.getLobby() == null) {
			Main.getInstance().getLogger().severe("No lobby arena loaded: this will cause plugin to not work");
		}
	}


	private static Lobby loadLobby(final YamlConfiguration config, final String name, final Location bounds1, final Location bounds2) {
		final Location spawn = parseLocation(config.getString("spawn-loc"));
		if (spawn == null) {
			Main.getInstance().getLogger().severe("could not load arena " + name + "invalid spawn location");
			return null;
		}

		final ConfigurationSection section = config.getConfigurationSection("kit-selector-locations");
		final HashMap<String, Location> kitSelectorLocations = new HashMap<>();
		if (section != null) {
			for (final String key : section.getKeys(false)) {
				if (KitManager.kitExists(key.toLowerCase())) {
					final Location loc = parseLocation(section.getString(key));
					if (loc != null) {
						kitSelectorLocations.putIfAbsent(key.toLowerCase(), loc);
					}
				}
			}
		}
		final Lobby lobby = new Lobby(name, bounds1, bounds2, kitSelectorLocations);

		if (ArenaManager.getLobby() != null) {
			return null;
		}
		ArenaManager.setLobby(lobby);

		return lobby;
	}

	private static FfaArena loadFfaArena(final YamlConfiguration config, final String name, final Location bounds1, final Location bounds2) {

		final List<Kit> allowedKits = KitManager.getFFAKits();
		if (allowedKits.isEmpty()) {
			allowedKits.addAll(KitManager.getKits());
		}
		final List<Location> spawnLocations = new ArrayList<>();

		for (final String locRaw : config.getStringList("spawn-locations")) {
			final Location loc = parseLocation(locRaw);
			if (loc != null) {
				spawnLocations.add(loc);
			}
		}
		if (spawnLocations.isEmpty()) {
			Main.getInstance().getLogger().severe("could not load arena " + name + ": ffa-arena must have at least one spawning location");
			return null;
		}

		final FfaArena ffaArena = new FfaArena(name, bounds1, bounds2, allowedKits, spawnLocations);

		if (config.getBoolean("primary")) {
			ArenaManager.setActiveFfaArena(ffaArena);
			ffaArena.activate(40);
		}
		return ffaArena;
	}

	private static void loadDuelArenaTemplate(final YamlConfiguration config, final String name, final Location bounds1, final Location bounds2) {
		final ConfigurationSection section = config.getConfigurationSection("locations");
		final List<Location> locations = new ArrayList<>();
		int index = 1;
		while (true) {
			final String locationString = section.getString("loc" + index);

			if (locationString == null) {
				break;
			}

			final Location location = parseLocation(locationString);

			if (location != null) {
				locations.add(location);
			}
			index++;
		}


		Material displayMaterial;

		try {
			displayMaterial = Material.valueOf(config.getString("displaymaterial"));
		} catch (IllegalArgumentException | NullPointerException e) {
			displayMaterial = Material.GRASS_BLOCK;
			e.printStackTrace();
		}

		final List<UUID> builders = new ArrayList<>();
		final List<String> buildersRaw = config.getStringList("builders");

		if (! buildersRaw.isEmpty()) {
			for (String builder : buildersRaw) {
				// Check for uuid
				if (builder.length() == 36) {
					try {
						builders.add(UUID.fromString(builder));
						// If the above code didn't throw any exceptions, it was a valid uuid.
						continue; // Skip to the next
					} catch (IllegalArgumentException e) {}
				}
			}
		}

		config.addDefault("name-key", "PVP_ARENA_DUELS_GENERIC");
		final String nameKey = config.getString("name-key");

		config.addDefault("amount", 1);
		final int amount = config.getInt("amount");
		final DuelArenaTemplate template = new DuelArenaTemplate(amount,name, bounds1, bounds2, builders, locations.toArray(new Location[locations.size()]), nameKey, displayMaterial);
		ArenaManager.addDuelArenaTemplate(template);
	}

	private static Location parseLocation(final String value) {
		if (value == null) {
			return null;
		}
		try {
			final String[] parts = value.split(" ");
			if (parts.length == 4) {
				return new Location(
						Objects.requireNonNull(
								Bukkit.getWorld(parts[0]), "unknown world '" + parts[0] + "'"
						),
						Double.parseDouble(parts[1]),
						Double.parseDouble(parts[2]),
						Double.parseDouble(parts[3])
				);
			} else if (parts.length == 6) {
				return new Location(
						Objects.requireNonNull(
								Bukkit.getWorld(parts[0]), "unknown world '" + parts[0] + "'"
						),
						Double.parseDouble(parts[1]),
						Double.parseDouble(parts[2]),
						Double.parseDouble(parts[3]),
						Float.parseFloat(parts[4]),
						Float.parseFloat(parts[5])
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
