package com.dreamless.laithorn;

import org.bukkit.Location;



public class WellLocationHandler {
	
	private static Location wellCornerFirst;
	private static Location wellCornerSecond;
	
	private static double westBound;
	private static double northBound;
	private static double eastBound;
	private static double southBound;
	
	public static boolean addCorner(Location location) {
		boolean success = false;

		if (wellCornerFirst == null) {
			wellCornerFirst = location;
			success = true;
		} else {
			if (location.getBlockY() == wellCornerFirst.getBlockY()
					&& location.getWorld() == wellCornerFirst.getWorld()) {
				wellCornerSecond = wellCornerFirst;
				wellCornerFirst = location;
				success = true;
				calculateEdges();
			}
		}
		DataHandler.saveWellArea(wellCornerFirst, wellCornerSecond);

		return success;
	}

	public static void clearSpawn() {
		wellCornerFirst = null;
		wellCornerSecond = null;
		DataHandler.saveWellArea(wellCornerFirst, wellCornerSecond);
	}

	public static void loadFirstCorner(Location location) {
		wellCornerFirst = location;
	}

	public static void loadSecondCorner(Location location) {
		wellCornerSecond = location;
	}

	public static boolean bothCornersDefined() {
		return wellCornerFirst != null && wellCornerSecond != null;
	}
	
	public static boolean checkIfInWell(Location location) {
		double xCoord = location.getX();
		double zCoord = location.getZ();
		PlayerMessager.debugLog(westBound + "-" + xCoord + "-" +  eastBound + " " + southBound+ "-" + zCoord + "-" + northBound);
		return westBound <= xCoord && xCoord <= eastBound
				&& northBound <= zCoord && zCoord <= southBound;
	}
	
	public static void calculateEdges() {
		westBound = Math.min(wellCornerFirst.getBlockX(), wellCornerSecond.getBlockX());
		eastBound = Math.max(wellCornerFirst.getBlockX(), wellCornerSecond.getBlockX()) + 1;
		
		
		southBound = Math.max(wellCornerFirst.getBlockZ(), wellCornerSecond.getBlockZ()) + 1;
		northBound = Math.min(wellCornerFirst.getBlockZ(), wellCornerSecond.getBlockZ());
		
		PlayerMessager.debugLog(westBound + "-" + eastBound + " " + northBound + "-" + southBound);
	}
}
