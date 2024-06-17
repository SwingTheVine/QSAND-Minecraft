package com.SwingTheVine.QSAND.util;

/** Adds beacon logs to the terminal.
 * 
 * @since <b>0.35.0</b>
 * @author <b>SwingTheVine</b> - 1.8.9 source code
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class BeaconHandler {
	
	private boolean enableBeacons = true; // Enables beacon logging
	
	// Constructor
	public BeaconHandler(final boolean enableBeacons) {
		this.enableBeacons = enableBeacons;
	}
	
	// Enables or disables beacon logging
	public void enableBeaconLogs(final boolean enableBeaconLogs) {
		
		this.enableBeacons = enableBeaconLogs;
	}
	
	// Logs a beacon with only a name
	public void logBeacon(final String name) {
		
		if (this.enableBeacons) {
			System.out.println(name + " Beacon");
		}
	}
	
	// Logs a beacon without a value
	public void logBeacon(final String name, final String id) {
		
		if (this.enableBeacons) {
			System.out.println(name + " Beacon " + id);
		}
	}
	
	// Logs a beacon with an integer value
	public void logBeacon(final String name, final String id, final int value) {
		
		if (this.enableBeacons) {
			System.out.println(name + " Beacon " + id + ": " + value);
		}
	}
	
	// Logs a beacon with a double value
	public void logBeacon(final String name, final String id, final double value) {
		
		if (this.enableBeacons) {
			System.out.println(name + " Beacon " + id + ": " + value);
		}
	}
	
	// Logs a beacon with a String value
	public void logBeacon(final String name, final String id, final String value) {
		
		if (this.enableBeacons) {
			System.out.println(name + " Beacon " + id + ": " + value);
		}
	}
}
