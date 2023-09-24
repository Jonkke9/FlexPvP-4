package fi.flexplex.pvp.loader;

import fi.flexplex.pvp.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class ConfigLoader {

	public static List<YamlConfiguration> loadConfigFilesInDirectory(final File parent) {
		if (!parent.isDirectory()) {
			return null;
		}
		final List<YamlConfiguration> loaded = new ArrayList<>();
		final File[] files = parent.listFiles();


		for (final File file : files) {
			final YamlConfiguration conf = loadConfig(file);
			if (conf != null) {
				loaded.add(conf);
			}
		}
		return loaded;
	}

	public static YamlConfiguration loadConfig(final File file) {
		if (file.getName().endsWith(".yml") || file.getName().endsWith("yaml")) {
			try {
				return YamlConfiguration.loadConfiguration(file);
			} catch (Exception e) {
				Main.getInstance().getLogger().severe(
						"Could not load resource at file " + file.getAbsolutePath()
				);
				e.printStackTrace();
			}
		}
		return null;
	}

}
