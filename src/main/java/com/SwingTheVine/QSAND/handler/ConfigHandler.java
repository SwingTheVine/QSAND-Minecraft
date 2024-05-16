package com.SwingTheVine.QSAND.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
	
	public static Configuration config;
	public static boolean makeBlocksOpaque;
	public static boolean useSkinOverlay;
	public static boolean useFleshTentacles;
	public static boolean useMudTentacles;
	public static boolean useCustomDrownPlayers; // QSAir
	public static boolean useCustomDrownEntities; // QSMAir
	public static int customDrownAirPlayersDW; // MuddyAir
	public static int customDrownAirEntitiesDW; // MuddyMobsAir
	public static int mudLevel;
	public static boolean useCustomBootCalc;
	public static boolean useCustomArmorCalc;
	public static boolean useCustomInvCalc;
	public static boolean spawnUnseenBubbles;
	
	public static int mudLevelEcp = 0;
	public static int customDrownAirPlayersDWEcp = 0;
	public static int customDrownAirEntitiesDWEcp = 0;
	public static double serverInstanceWeight = 0.0;
	public static double serverInstanceFOV = 1.0;
	public static double serverInstanceRenderYaw = 0.0;
	public static double serverInstancePreRenderYaw = 0.0;
	
	public static void init(File file) {
		config = new Configuration(file);
		String category;
		
		config.load();
		
		category = "Options";
		config.addCustomCategoryComment(category, "Changes aspects of the game");
		makeBlocksOpaque = config.getBoolean("Make Blocks Opaque", category, false, "Prevents light from traveling through blocks");
		useSkinOverlay = config.getBoolean("Use Skin Overlay", category, true, "Enables muddy skin overlay on players");
		mudLevel = config.getInt("Mud Level Datawatcher", category, 25, 20, 31, "Datawatcher for mud level for the skin overlay");
		useFleshTentacles = config.getBoolean("Use Flesh Tentacles", category, true, "Enables flesh tentacles in fleshy pits");
		useMudTentacles = config.getBoolean("Use Mud/Quicksand Tentacles", category, true, "Enables tentacles in some types of mud and quicksand");
		useCustomDrownPlayers = config.getBoolean("Use Custom Drown (Players)", category, true, "Enables realistic drowning for players");
		useCustomDrownEntities = config.getBoolean("Use Custom Drown (Entities)", category, true, "Enables realistic drowning for entities");
		customDrownAirPlayersDW = config.getInt("Custom Drown Datawatcher (Players)", category, 29, 20, 31, "Datawatcher for realistic drowning for players");
		customDrownAirEntitiesDW = config.getInt("Custom Drown Datawatcher (Entities)", category, 29, 20, 31, "Datawatcher for realistic drowning for entities");
		useCustomBootCalc = config.getBoolean("Use Custom Boot Calculations", category, true, "Enables realistic boot calculations for sinking");
		useCustomArmorCalc = config.getBoolean("Use Custom Armor Calculations", category, true, "Enables realistic sinking calculated from the weight of armor");
		useCustomInvCalc = config.getBoolean("Use Custom Inventory Calculations", category, true, "Enables realistic sinking calculated from the weight of the inventory");
		spawnUnseenBubbles = config.getBoolean("Spawn Unseen Bubbles", category, false, "Spawns bubbles you can not see normally in singleplayer. Enable this if you are using a mod to record");
		
		config.save();
	}
}
