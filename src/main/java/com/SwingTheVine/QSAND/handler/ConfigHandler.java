package com.SwingTheVine.QSAND.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
	
	public static Configuration config;
	public static boolean makeBlocksOpaque;
	public static boolean useCustomDrownBlock;
	public static boolean useCustomDrownEntity;
	public static int customDrownAirBlockDW;
	public static int customDrownAirEntityDW;
	public static boolean spawnUnseenBubbles;
	
	public static void init(File file) {
		config = new Configuration(file);
		String category;
		
		config.load();
		
		category = "Options";
		config.addCustomCategoryComment(category, "Changes aspects of the game");
		makeBlocksOpaque = config.getBoolean("Make Blocks Opaque", category, false, "Prevents light from traveling through blocks");
		useCustomDrownBlock = config.getBoolean("Use Custom Drown (Block)", category, true, "Enables realistic drowning in certain blocks");
		useCustomDrownEntity = config.getBoolean("Use Custom Drown (Entity)", category, true, "Enables realistic drowning in certain entities");
		useCustomDrownBlock = config.getBoolean("Custom Drown Datawatcher (Block)", category, true, "Datawatcher for realistic drowning in certain blocks");
		useCustomDrownEntity = config.getBoolean("Custom Drown Datawatcher (Entity)", category, true, "Datawatcher for realistic drowning in certain entities");
		spawnUnseenBubbles = config.getBoolean("Spawn Unseen Bubbles", category, false, "Spawns bubbles you can not see normally in singleplayer. Enable this if you are using a mod to record");
		
		config.save();
	}
}
