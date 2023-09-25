package fi.flexplex.pvp.game.arena;

import fi.flexplex.core.api.Language;
import fi.flexplex.core.api.Permissions;
import fi.flexplex.pvp.misc.BlockDataLocation;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class DuelArenaTemplate {

	private int id = 0;
	private final int slices;
	private final String name;
	final int sizeX, sizeY, sizeZ;
	private final String displayNameKey;
	private final List<String> builderNames;
	private final Material displayMaterial;
	private final Set<BlockDataLocation> blocks;
	private final Location[] normalizedLocations;

	public DuelArenaTemplate(final String name,
							 final Location loc1,
							 final Location loc2,
							 final List<UUID> builders,
							 final Location[] locations,
							 final String displayNameKey,
							 final Material displayMaterial) {

		final World world = loc1.getWorld();
		final Location[] normalizedLocations = new Location[locations.length];

		this.sizeX = Math.abs(loc1.getBlockX() - loc2.blockX());
		this.sizeY = Math.abs(loc1.getBlockY() - loc2.blockY());
		this.sizeZ = Math.abs(loc1.getBlockZ() - loc2.blockZ());

		final Location smallest = new Location(world,
				loc1.getBlockX() <= loc2.blockX() ? loc1.getBlockX() : loc2.getBlockX(),
				loc1.getBlockY() <= loc2.blockY() ? loc1.getBlockY() : loc2.getBlockY(),
				loc1.getBlockZ() <= loc2.blockZ() ? loc1.getBlockZ() : loc2.getBlockZ()
		);

		final Set<BlockDataLocation> blocks1 = new HashSet<>();

		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				for (int k = 0; k < sizeZ; k++) {
					final BlockData data = world.getBlockData(smallest.getBlockX() + i, smallest.getBlockY() + j, smallest.getBlockZ() + k);
					if (data.getMaterial() != null && data.getMaterial() != Material.AIR) {
						blocks1.add(new BlockDataLocation(data, i, j, k));
					}
				}
			}
		}

		for (int i = 0; i < locations.length; i++) {
			normalizedLocations[i] = locations[i].add(-1.0 * smallest.getX(), -1.0 * smallest.getY(), -1.0 * smallest.getZ());
		}

		if ((sizeX * sizeY * sizeZ) % 100 > 0) {
			this.slices = ((sizeX * sizeY * sizeZ) / 500) + 1;
		} else {
			this.slices = ((sizeX * sizeY * sizeZ) / 500);
		}

		this.normalizedLocations = normalizedLocations;
		this.displayMaterial = displayMaterial;
		this.displayNameKey = displayNameKey;
		this.builderNames = new ArrayList<>();
		this.blocks = blocks1;
		this.name = name;

		for (final UUID uuid : builders) {
			Permissions.getLegacyDisplayName(Bukkit.getOfflinePlayer(uuid)).whenCompleteAsync((builderName, e)-> {
				if (e != null) {
					e.printStackTrace();
				}
				this.builderNames.add(builderName);
			});

		}
	}

	protected DuelArena buildArena(final Location bounds1) {
		final Location bounds2 = bounds1.clone().add(this.sizeX, this.sizeY, this.sizeZ);
		final Location[] locations = new Location[this.normalizedLocations.length];
		final String name = this.name + "-" + String.valueOf(this.id++);

		for (int i = 0; i < this.normalizedLocations.length; i++) {

			final Location loc = new Location(
					bounds1.getWorld(),
					this.normalizedLocations[i].getX(),
					this.normalizedLocations[i].getY(),
					this.normalizedLocations[i].getZ(),
					this.normalizedLocations[i].getYaw(),
					this.normalizedLocations[i].getPitch()
			);

			locations[i] = loc.add(bounds1);
		}

		final DuelArena arena = new DuelArena(name, bounds1, bounds2, locations, this);
		return arena;
	}


	protected int getAmountOfSlices() {
		return this.slices;
	}

	public String getName() {
		return this.name;
	}

	public Set<BlockDataLocation> getBlocks() {
		return this.blocks;
	}

	public Material getDisplayMaterial() {
		return displayMaterial;
	}

	public String getDisplayNameKey() {
		return displayNameKey;
	}

	public ItemStack getIcon(final CommandSender sender) {
		final ItemStack icon = new ItemStack(displayMaterial);
		final ItemMeta meta = icon.getItemMeta();

		meta.displayName(Language.getMessage(sender, displayNameKey));

		if (builderNames.size() != 0) {
			final List<Component> lore = new ArrayList<>();
			lore.add(Language.getMessage(sender, "PVP_ARENA_BUILDERS"));

			for (final String name : builderNames) {
				lore.add(Language.getMessage(sender, "PVP_ARENA_BUILDER", name));
			}
		}

		icon.setItemMeta(meta);
		return icon;
	}

}


