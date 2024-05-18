package com.SwingTheVine.QSAND.handler;

public class BeaconHandler {
	private boolean enableBeacons = true; // Enables beacon logging
	
	// Constructor
	public BeaconHandler(boolean enableBeacons) {
		this.enableBeacons = enableBeacons;
	}
	
	// Logs a beacon without a value
	public void logBeacon(String name, String id) {
		if (this.enableBeacons) {
			System.out.println(name + " Beacon " + id);
		}
	}
	
	// Logs a beacon with an integer value
	public void logBeacon(String name, String id, int value) {
		if (this.enableBeacons) {
			System.out.println(name + " Beacon " + id + ": " + value);
		}
	}
	
	// Logs a beacon with a double value
	public void logBeacon(String name, String id, double value) {
		if (this.enableBeacons) {
			System.out.println(name + " Beacon " + id + ": " + value);
		}
	}
	
	// Logs a beacon with a String value
	public void logBeacon(String name, String id, String value) {
		if (this.enableBeacons) {
			System.out.println(name + " Beacon " + id + ": " + value);
		}
	}
	
	// Enables or disables beacon logging
	public void enableBeaconLogs(boolean enableBeaconLogs) {
		this.enableBeacons = enableBeaconLogs;
	}
}
