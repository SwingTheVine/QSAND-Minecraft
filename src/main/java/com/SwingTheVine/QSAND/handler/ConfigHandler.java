package com.SwingTheVine.QSAND.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
	
	public static Configuration config;
	public static boolean makeBlocksOpaque;
	public static boolean useSkinOverlay;
	public static boolean useFleshTentacles;
	public static boolean useMudTentacles;
	public static boolean useCustomDrownBlock;
	public static boolean useCustomDrownEntity;
	public static int customDrownAirBlockDW;
	public static int customDrownAirEntityDW;
	public static int mudLevel;
	public static boolean useCustomBootCalc;
	public static boolean useCustomArmorCalc;
	public static boolean useCustomInvCalc;
	public static boolean spawnUnseenBubbles;
	
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
		useCustomDrownBlock = config.getBoolean("Use Custom Drown (Block)", category, true, "Enables realistic drowning in certain blocks");
		useCustomDrownEntity = config.getBoolean("Use Custom Drown (Entity)", category, true, "Enables realistic drowning in certain entities");
		useCustomDrownBlock = config.getBoolean("Custom Drown Datawatcher (Block)", category, true, "Datawatcher for realistic drowning in certain blocks");
		useCustomDrownEntity = config.getBoolean("Custom Drown Datawatcher (Entity)", category, true, "Datawatcher for realistic drowning in certain entities");
		useCustomBootCalc = config.getBoolean("Use Custom Boot Calculations", category, true, "Enables realistic boot calculations for sinking");
		useCustomArmorCalc = config.getBoolean("Use Custom Armor Calculations", category, true, "Enables realistic sinking calculated from the weight of armor");
		useCustomInvCalc = config.getBoolean("Use Custom Inventory Calculations", category, true, "Enables realistic sinking calculated from the weight of the inventory");
		spawnUnseenBubbles = config.getBoolean("Spawn Unseen Bubbles", category, false, "Spawns bubbles you can not see normally in singleplayer. Enable this if you are using a mod to record");
		
		config.save();
	}
}
