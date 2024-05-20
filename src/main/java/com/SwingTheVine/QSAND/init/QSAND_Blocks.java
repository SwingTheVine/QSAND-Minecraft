package com.SwingTheVine.QSAND.init;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.QSAND;
import com.SwingTheVine.QSAND.blocks.BlockTest;
import com.SwingTheVine.QSAND.blocks.ItemBlockMeta;
import com.SwingTheVine.QSAND.blocks.Mud;
import com.SwingTheVine.QSAND.blocks.Quicksand;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class QSAND_Blocks {
	public static Block test_block;
	public static Block mud;
	public static Block quicksand;
	public static Block quicksandSoft;
	public static Block quicksandJungle;
	public static Block moss;
	public static Block mire;
	public static Block mireLiquid;
	public static Block mireLiquidStable;
	public static Block meat;
	public static Block meatHole;
	public static Block voidHole;
	public static Block swallowingFlesh;
	public static Block larvae;
	public static Block tar;
	public static Block[] blockList = {
			test_block,
			mud,
			quicksand,
			quicksandSoft,
			quicksandJungle,
			moss,
			mire,
			mireLiquid,
			mireLiquidStable,
			meat,
			meatHole,
			voidHole,
			swallowingFlesh,
			larvae,
			tar};
	public static int[] mudTypesColors = {4538917, 14611967, 16777215, 2431764, 6444596, 3089167, 2431764, 2431764, 15007713, 2431764, 2431764, 10197137, 1973277, 16777215, 16777215, 16777189, 16777215, 6180923, 14803425, 16777189, 14013598, 16777189, 7041868, 10056782, 8554890, 2431764, 12298319, 3806471, 5720614, 2431764, 5389343, 16758567};
	public static int[] mudMaxOpacity = {2000, 500, 0, 1000, 5000, 5000, 500, 500, 500, 5000, 5000, 5000, 10000, 0, 0, 250, 0, 2000, 3000, 250, 500, 250, 1000, 5000, 5000, 1000, 10000, 7000, 2000, 2000, 5000, 600};
	public static int[] mudLastOpacity = {500, 0, 0, 750, 750, 750, 250, 250, 0, 750, 750, 750, 1000, 0, 0, 0, 0, 500, 0, 0, 0, 0, 0, 750, 750, 750, 1000, 1000, 750, 750, 750, 600};
	public static int[] mudIncOpacity = {50, 25, 0, 100, 100, 100, 50, 50, 50, 100, 100, 100, 500, 0, 0, 25, 0, 100, 75, 25, 50, 25, 100, 100, 100, 100, 500, 500, 50, 100, 100, 50};
	// Test World Seed: 1637864495647481288
	// Entities fall at a rate of 0.076125 blocks
	
	public static void init() {
		
		// Constructs all blocks with their names and tab
		test_block = new BlockTest(Material.ground).setUnlocalizedName("test_block").setCreativeTab(QSAND.QSANDTab);
		mud = new Mud(Material.ground).setUnlocalizedName("mud").setCreativeTab(QSAND.QSANDTab);
		quicksand = new Quicksand(Material.ground).setUnlocalizedName("quicksand").setCreativeTab(QSAND.QSANDTab);
	}
	
	public static void registerBlocks() {
		
		// Registers the blocks with the Game Registry
		GameRegistry.registerBlock(test_block, ItemBlockMeta.class, test_block.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mud, ItemBlockMeta.class, mud.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(quicksand, ItemBlockMeta.class, quicksand.getUnlocalizedName().substring(5));
	}
	
	public static void registerRenders() {
		
		// Registers the inventory image. Block to render, number of metadata types, should one texture be used
		registerRenderInventory(test_block, ((BlockTest)test_block).getTypes(), ((BlockTest)test_block).getUseOneTexture());
		registerRenderInventory(mud, ((Mud)mud).getTypes(), ((Mud)mud).getUseOneTexture());
		registerRenderInventory(quicksand, ((Quicksand)quicksand).getTypes(), ((Quicksand)quicksand).getUseOneTexture());
	}
	
	// Registers the inventory image for all block variants
	public static void registerRenderInventory(Block block, String[] types, boolean useOneTexture) {
		
		Item item = Item.getItemFromBlock(block); // The item variant of the block
		
		// If the block should only use one texture...
		if (useOneTexture) {
			
			// Every block variant uses the same texture file
			for (int indexMeta = 0; indexMeta < types.length; indexMeta++) {
				Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, indexMeta, new ModelResourceLocation(ModInfo.id + ":" + item.getUnlocalizedName().substring(5), "inventory"));
			}
		}
		else {
			
			// Every block variant uses its own texture file
			for (int indexMeta = 0; indexMeta < types.length; indexMeta++) {
				Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, indexMeta, new ModelResourceLocation(ModInfo.id + ":" + item.getUnlocalizedName().substring(5) + "_" + types[indexMeta], "inventory"));
			}
		}
	}
}
