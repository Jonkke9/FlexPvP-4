package fi.flexplex.pvp.game.kit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class KitManager {

	private static Map<String, Kit> KITS = new HashMap<>();

	public static Kit getKit(final String name) {
		if (KITS.containsKey(name)) {
			return KITS.get(name);
		}
		return null;
	}

	public static List<Kit> getKits() {
		final List<Kit> kits = new ArrayList<>(KITS.values());
		Collections.sort(kits, new Comparator<Kit>() {
			@Override
			public int compare(final Kit o1, final Kit o2) {
				return o1.getSlot() < o2.getSlot() ? -1 : o1.getSlot() == o2.getSlot() ? 0 : 1;
			}
		});
		return kits;
	}

	public static List<Kit> getFFAKits() {
		final List<Kit> kits = new ArrayList<>();

		for (final Kit kit : KITS.values()) {
			if (kit.isFfaKit()) {
				kits.add(kit);
			}
		}
		return kits;
	}

	public static void addKit(final String name, final Kit kit) {
		KITS.putIfAbsent(name,kit);
	}

	public static boolean kitExists(final String name) {
		return KITS.containsKey(name);
	}
}
