package com.SwingTheVine.QSAND;

import com.SwingTheVine.QSAND.init.QSAND_Blocks;
import com.SwingTheVine.QSAND.init.QSAND_Items;
import com.SwingTheVine.QSAND.proxy.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ModInfo.ID, name = ModInfo.Name, version = ModInfo.Version)
public class QSAND {
	
	@SidedProxy(clientSide = ModInfo.ClientProxyClass, serverSide = ModInfo.ServerProxyClass)
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		QSAND_Blocks.init();
		QSAND_Blocks.registerBlocks();
		QSAND_Items.init();
		QSAND_Items.registerItems();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerRenders();
	}
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
