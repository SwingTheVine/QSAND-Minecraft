package com.SwingTheVine.QSAND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModInfo {
	
	public static final String id = "QSAND";
	public static final String name = "QuickSand And Numerous Dungeons";
	public static final String description = "This mod adds a large variety of unique quicksands and custom structures to the game.";
	public static final String version = "0.63.5";
	public static final String totalPatches = "363";
	public static final String url = "";
	public static final List<String> authorList = new ArrayList<String>(Arrays.asList("SwingTheVine"));
	public static final String credits = "MrBlackGoo, elix_x, CrishNate, VanderCat, and Sanic for making the More Fun Quicksand Mod";
	public static final String logoFile = "";
	public static final String acceptedMCVersions = "1.8 <= x <= 1.8.9";
	public static final String clientProxyClass = "com.SwingTheVine.QSAND.proxy.ClientProxy";
	public static final String serverProxyClass = "com.SwingTheVine.QSAND.proxy.CommonProxy";
	
	public static void init(final FMLPreInitializationEvent event) {
		
		event.getModMetadata().modId = id;
		event.getModMetadata().name = name;
		event.getModMetadata().description = description;
		event.getModMetadata().version = version;
		event.getModMetadata().url = url;
		event.getModMetadata().authorList = authorList;
		event.getModMetadata().credits = credits;
		event.getModMetadata().logoFile = logoFile;
	}
}
