package fi.flexplex.pvp.misc.world;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class EmptyWorldGenerator extends ChunkGenerator {

	@NotNull
	public static final EmptyWorldGenerator s_Instance = new EmptyWorldGenerator();

	private EmptyWorldGenerator() {}

	@Override
	@NotNull
	public ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z,
									   @NotNull BiomeGrid biome) {

		return createChunkData(world);
	}

	@Override
	@NotNull
	public List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
		return Collections.emptyList();
	}

	@Override
	public boolean isParallelCapable() {
		return true;
	}

	@Override
	public boolean shouldGenerateCaves() {
		return false;
	}

	@Override
	public boolean shouldGenerateDecorations() {
		return false;
	}

	@Override
	public boolean shouldGenerateMobs() {
		return false;
	}

	@Override
	public boolean shouldGenerateStructures() {
		return false;
	}

}
