package com.dreamless.laithorn;

import org.bukkit.Location;



public class WellHandler {
	
	private static Location spawnCornerFirst;
	private static Location spawnCornerSecond;
	
	public static boolean addCorner(Location location) {
		boolean success = false;

		if (spawnCornerFirst == null) {
			spawnCornerFirst = location;
			success = true;
		} else {
			if (location.getBlockY() == spawnCornerFirst.getBlockY()
					&& location.getWorld() == spawnCornerFirst.getWorld()) {
				spawnCornerSecond = spawnCornerFirst;
				spawnCornerFirst = location;
				success = true;
			}
		}
		DataHandler.saveSpawnArea(spawnCornerFirst, spawnCornerSecond);

		return success;
	}

	public static void clearSpawn() {
		spawnCornerFirst = null;
		spawnCornerSecond = null;
		DataHandler.saveSpawnArea(spawnCornerFirst, spawnCornerSecond);
	}

	public static void loadFirstCorner(Location location) {
		spawnCornerFirst = location;
	}

	public static void loadSecondCorner(Location location) {
		spawnCornerSecond = location;
	}


	private static Location getLeftBottomLocation() {
		if (spawnCornerSecond == null || spawnCornerFirst == null) {
			return null;
		}
		double xAxis = Math.min(spawnCornerFirst.getX(), spawnCornerSecond.getX());
		double zAxis = Math.min(spawnCornerFirst.getZ(), spawnCornerSecond.getZ());

		return new Location(spawnCornerFirst.getWorld(), xAxis, spawnCornerFirst.getBlockY(), zAxis);
	}

}
