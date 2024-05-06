package com.SwingTheVine.QSAND.init;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.QSAND;
import com.SwingTheVine.QSAND.blocks.BlockTest;
import com.SwingTheVine.QSAND.blocks.ItemBlockMeta;
import com.SwingTheVine.QSAND.blocks.Mud;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class QSAND_Blocks {
	public static Block test_block;
	public static Block mud;
	// Test World Seed: 1637864495647481288
	
	public static void init() {
		test_block = new BlockTest(Material.ground).setUnlocalizedName("test_block").setCreativeTab(QSAND.QSANDTab);
		mud = new Mud(Material.ground).setUnlocalizedName("mud").setCreativeTab(QSAND.QSANDTab);
	}
	
	public static void registerBlocks() {
		GameRegistry.registerBlock(test_block, ItemBlockMeta.class, test_block.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mud, ItemBlockMeta.class, mud.getUnlocalizedName().substring(5));
	}
	
	public static void registerRenders() {
		// Registers the inventory image. Block to render, number of metadata types, should one texture be used
		registerRenderInventory(test_block, ((BlockTest)test_block).getTypes(), ((BlockTest)test_block).getUseOneTexture());
		registerRenderInventory(mud, ((Mud)mud).getTypes(), ((Mud)mud).getUseOneTexture());
	}
	
	// numMeta = number of metadata values to render
	public static void registerRenderInventory(Block block, String[] types, Boolean useOneTexture) {
		
		Item item = Item.getItemFromBlock(block);
		
		// If the block should only use one texture...
		if (useOneTexture) {
			
			// Every metadata uses the same texture file
			for (int indexMeta = 0; indexMeta < types.length; indexMeta++) {
				Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, indexMeta, new ModelResourceLocation(ModInfo.ID + ":" + item.getUnlocalizedName().substring(5) + "", "inventory"));
			}
		}
		else {
			
			// Every metadata uses its own texture file
			for (int indexMeta = 0; indexMeta < types.length; indexMeta++) {
				Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, indexMeta, new ModelResourceLocation(ModInfo.ID + ":" + item.getUnlocalizedName().substring(5) + "_" + types[indexMeta], "inventory"));
			}
		}
	}
}
