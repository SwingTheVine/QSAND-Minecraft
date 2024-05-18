package com.SwingTheVine.QSAND;

import java.io.File;

import com.SwingTheVine.QSAND.handler.ConfigHandler;
import com.SwingTheVine.QSAND.init.QSAND_Blocks;
import com.SwingTheVine.QSAND.init.QSAND_Entities;
import com.SwingTheVine.QSAND.init.QSAND_Items;
import com.SwingTheVine.QSAND.manager.PlayerManager;
import com.SwingTheVine.QSAND.proxy.CommonProxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ModInfo.id, 
		name = ModInfo.name, 
		version = ModInfo.version,
		acceptedMinecraftVersions = ModInfo.acceptedMCVersions)
public class QSAND {
	
	// Declares proxy
	@SidedProxy(clientSide = ModInfo.clientProxyClass, serverSide = ModInfo.serverProxyClass)
	public static CommonProxy proxy;
	
	// Declares an instance of the mod
	@Mod.Instance
	public static QSAND instance;
	
	public static final QSAND_Tab QSANDTab = new QSAND_Tab("QSANDTab"); // Makes the Modded Creative Inventory tab
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ModInfo.init(event); // Overwrites the mcmodinfo with the latest information
		ConfigHandler.init(new File(event.getModConfigurationDirectory() + "/QSAND.cfg"));
		QSAND_Blocks.init(); // Initializes the blocks
		QSAND_Blocks.registerBlocks(); // Registers the blocks in the game registry
		QSAND_Items.init(); // Initializes the items
		QSAND_Items.registerItems(); // Registers the items in the game registry
		QSAND_Entities.registerEntities(); // Registers the entities
		if (ConfigHandler.useSkinOverlay) {
			MinecraftForge.EVENT_BUS.register((Object)new PlayerManager());
		}
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerRenders(); // Registers the render models for all default blocks and items
		proxy.registerEntityRenders(); // Registers the render models for all entities
		proxy.registerModelQSAND(); // Registers the render models for all variants of the items
		QSAND_Entities.setEntityToSpawn();
		QSAND_Entities.generateSpawnEgg(); // Generates the spawn eggs for all the entities
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
