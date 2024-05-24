package com.SwingTheVine.QSAND.util;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
	
	public static Configuration config;
	
	// Mobs
	public static boolean spawnMobs; // InitMobs
	public static boolean spawnSlimeVoid; // SpawnVS
	public static boolean spawnSlimeMud; // SpawnMS
	public static boolean spawnSlimeSand; // SpawnSB
	public static boolean spawnSlimeTar; // SpawnTS
	
	// Options
	public static boolean onlyMFQM;
	public static boolean makeBlocksOpaque; // QSOpacity
	public static boolean useSkinOverlay; // QSCover
	public static boolean useCustomAirHUD; // QSCAir
	public static boolean forceFirstPerson; // QSThirdPerson
	public static boolean useFleshTentacles; // QSTentacles
	public static boolean useMudTentacles; // QSMudTentacles
	public static boolean useCustomDrownPlayers; // QSAir
	public static boolean useCustomDrownEntities; // QSMAir
	public static int customDrownAirPlayersDW; // MuddyAir
	public static int customDrownAirEntitiesDW; // MuddyMobsAir
	public static int mudLevel; // MuddyLevel
	public static boolean useCustomBootCalc; // QSBootsCalc
	public static boolean useCustomArmorCalc; // QSArmorCalc
	public static boolean useCustomInvCalc; // QSWeightCalc
	public static boolean spawnBubbles; // QSBubble
	public static boolean spawnUnseenBubbles;
	
	public static int mudLevelEcp = 0; // MuddyLevelEcp
	public static int customDrownAirPlayersDWEcp = 0; // MuddyAirEcp
	public static int customDrownAirEntitiesDWEcp = 0; // MuddyMobsAirEcp
	public static double serverInstanceWeightInv = 0.0; // SIWeight
	public static double serverInstanceFOV = 1.0; // SIFOV
	public static double serverInstanceRenderYaw = 0.0; // SIRenderYaw
	public static double serverInstancePreRenderYaw = 0.0; // SIRenderYawPre
	
	public static void init(File file) {
		config = new Configuration(file);
		String category;
		
		config.load();
		
		category = "Mobs";
		config.addCustomCategoryComment(category, "Changes aspects of the mobs");
		spawnMobs = config.getBoolean("Spawn Mobs", category, true, "Enables natural monster spawning");
		spawnSlimeVoid = config.getBoolean("Spawn Void Slime", category, true, "Enables natural spawning for Void Slimes");
		spawnSlimeMud = config.getBoolean("Spawn Mud Slime", category, true, "Enables natural spawning for Mud Slimes");
		spawnSlimeSand = config.getBoolean("Spawn Sand Slime", category, true, "Enables natural spawning for Sand Slimes");
		spawnSlimeTar = config.getBoolean("Spawn Tar Slime", category, true, "Enables natural spawning for Tar Slimes");
		
		category = "Options";
		config.addCustomCategoryComment(category, "Changes aspects of the game");
		onlyMFQM = config.getBoolean("Use Only MFQM", category, false, "Removes everything but what was in MFQM");
		makeBlocksOpaque = config.getBoolean("Make Blocks Opaque", category, false, "Prevents light from traveling through blocks");
		useSkinOverlay = config.getBoolean("Use Skin Overlay", category, true, "Enables muddy skin overlay on players");
		useCustomAirHUD = config.getBoolean("Use Custom Air HUD", category, true, "Enables custom air overlay");
		forceFirstPerson = config.getBoolean("Force First Person In Blocks", category, true, "Forces the player into first person view when inside mud/quicksand");
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
		spawnBubbles = config.getBoolean("Spawn Bubbles", category, true, "Spawns bubbles on the surface of some mud/quicksand blocks");
		spawnUnseenBubbles = config.getBoolean("Spawn Unseen Bubbles", category, false, "Spawns bubbles you can not see normally in singleplayer. Enable this if you are using a mod to record");
		
		config.save();
	}
}
