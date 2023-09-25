package fi.flexplex.pvp.loader;

import fi.flexplex.pvp.Main;
import fi.flexplex.pvp.game.kit.Kit;
import fi.flexplex.pvp.game.kit.KitManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class KitLoader {
	public static void LoadAllKits() {
		final List<YamlConfiguration> configs = ConfigLoader.loadConfigFilesInDirectory(new File(Main.getInstance().getDataFolder().getPath() + "/kits"));

		if (configs == null) {
			return;
		}

		for (final YamlConfiguration config : configs) {
			if (config == null) {
				continue;
			}

			final String name = config.getString("name").toLowerCase();
			if (name == null) {
				Main.getInstance().getLogger().severe("Kit could not be loaded invalid name");
				continue;
			}

			if (KitManager.kitExists(name)) {
				Main.getInstance().getLogger().severe("Kit named " + name + " already exist");
				continue;
			}

			final String displayNameKey = config.getString("name-key");
			if (displayNameKey == null) {
				Main.getInstance().getLogger().severe("Kit " + name + " could not be loaded: invalid name-key");
				continue;
			}

			final List<String> descriptionLineKeys = new ArrayList<>();
			descriptionLineKeys.addAll(config.getStringList("description-lines"));


			Material displayMaterial;

			try {
				displayMaterial = Material.valueOf(config.getString("displaymaterial"));
			} catch (IllegalArgumentException | NullPointerException e) {
				displayMaterial = Material.IRON_SWORD;
				e.printStackTrace();
			}

			config.addDefault("ffa-kit", false);
			final boolean ffaKit = config.getBoolean("ffa-kit");
			final int slot = config.getInt("kit-menu-slot");
			final List<ItemStack> contentsList = new ArrayList<>();

			for (int i = 0; i < 9; i++) {
				contentsList.add(config.getItemStack("hotbar.slot" + String.valueOf(i)));
			}

			for (int i = 0; i < 27; i++) {
				contentsList.add(config.getItemStack("storage.slot" + String.valueOf(i)));
			}

			contentsList.add(config.getItemStack("armor.boots"));
			contentsList.add(config.getItemStack("armor.leggings"));
			contentsList.add(config.getItemStack("armor.chestplate"));
			contentsList.add(config.getItemStack("armor.helmet"));
			contentsList.add(config.getItemStack("offhand"));

			final ItemStack[] contents = new ItemStack[contentsList.size()];
			contentsList.toArray(contents);

			final Collection<PotionEffect> effects = loadPotionEffects(config);

			final Kit kit = new Kit(name,
							displayNameKey,
							descriptionLineKeys,
							displayMaterial,
							ffaKit,
							contents,
							effects,
							slot);

			KitManager.addKit(name, kit);
		}

	}


	private static Collection<PotionEffect> loadPotionEffects(final YamlConfiguration config) {

		final List<String> effectsRaw = config.getStringList("effects");
		final Collection<PotionEffect> effects;
		if (!effectsRaw.isEmpty()) {
			effects = new ArrayList<>(effectsRaw.size());

			for (String string : effectsRaw) {
				if (string.contains(" ")) {
					final String[] split = string.split(" ");

					try {
						effects.add(new PotionEffect(
								PotionEffectType.getByName(split[0]),
								Integer.MAX_VALUE,
								Integer.parseInt(split[1]) - 1,
								false,
								false
						));
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException(
								"invalid amplifier '" + split[1] + "'"
						);
					} catch (IllegalArgumentException e) {
						throw new IllegalArgumentException(
								"invalid potion effect type '" + split[0] + "'"
						);
					}
				} else {
					try {
						effects.add(new PotionEffect(
								PotionEffectType.getByName(string),
								Integer.MAX_VALUE,
								0,
								false,
								false
						));
					} catch (IllegalArgumentException e) {
						throw new IllegalArgumentException(
								"invalid potion effect type '" + string + "'"
						);
					}
				}
			}
		} else {
			effects = Collections.emptyList();
		}
		return effects;
	}
}
