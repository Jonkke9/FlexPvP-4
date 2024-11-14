package fi.flexplex.pvp.game.arena;

import fi.flexplex.pvp.misc.BlockDataLocation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

public class DuelArenaBuildTask extends BukkitRunnable {

	private final Location loc;
	private final World world;
	private final DuelArena arena;
	private final int speed;
	private final Iterator<BlockDataLocation> iterator;
	private int duration = 0;

	public DuelArenaBuildTask(final DuelArenaTemplate template, final Location loc, final DuelArena arena) {
		this.loc = loc;
		this.world = loc.getWorld();
		this.arena = arena;
		this.iterator = template.getBlocks().iterator();
		this.duration = template.getAmountOfSlices();
		speed = ArenaManager.getGenerationSpeed();
	}

	@Override
	public void run() {
		for (int i = 0; i < speed; i++) {
			if (!iterator.hasNext()) {
				ArenaManager.removeBuildTask(this);
				this.arena.setReady(true);
				this.cancel();
				break;
			}

			final BlockDataLocation data = iterator.next();
			final Location blockLocation = new Location(loc.getWorld(), loc.getBlockX() + data.x(), loc.blockY() + data.y(), loc.blockZ() + data.z());

			world.getBlockAt(blockLocation).setBlockData(data.data(), false);
		}
		duration--;
	}

	protected int getDuration() {
		return this.duration;
	}
}
