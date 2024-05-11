package com.SwingTheVine.QSAND.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
	
	public static Configuration config;
	public static boolean makeBlocksOpaque;
	
	public static void init(File file) {
		config = new Configuration(file);
		String category;
		
		config.load();
		
		category = "Options";
		config.addCustomCategoryComment(category, "Changes aspects of the game");
		makeBlocksOpaque = config.getBoolean("makeBlocksOpaque", category, false, "Prevents light from traveling through blocks");
		
		config.save();
	}
}
