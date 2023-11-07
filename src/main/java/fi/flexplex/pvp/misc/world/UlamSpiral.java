package fi.flexplex.pvp.misc.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import java.util.Iterator;

public final class UlamSpiral implements Iterator<Location> {

	private final Location center;
	private final int stepSize;

	private final Location loc;
	private int step = 0;
	private int stepsPerSide = 1;
	private BlockFace direction = BlockFace.EAST;
	private int index = 0;

	public UlamSpiral(final  World world, int stepSize) {
		center = new Location(world, -1 * stepSize, 0, 0);
		this.stepSize = stepSize;
		loc = center;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public Location next() {
		loc.add(direction.getModX() * stepSize, 0, direction.getModZ() * stepSize);
		step++;
		if (step >= stepsPerSide) {
			switch (direction) {
				case NORTH:
					direction = BlockFace.EAST;
					break;
				case EAST:
					direction = BlockFace.SOUTH;
					stepsPerSide++;
					break;
				case SOUTH:
					direction = BlockFace.WEST;
					break;
				case WEST:
					direction = BlockFace.NORTH;
					stepsPerSide++;
					break;
			}

			step = 0;
		}
		index++;
		return loc.clone();
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	public int getIndex() {
		return index;
	}


}
