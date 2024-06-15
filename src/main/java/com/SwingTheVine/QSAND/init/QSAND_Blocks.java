package com.SwingTheVine.QSAND.init;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.QSAND;
import com.SwingTheVine.QSAND.block.BlockLarvae;
import com.SwingTheVine.QSAND.block.BlockMud;
import com.SwingTheVine.QSAND.block.BlockQuicksand;
import com.SwingTheVine.QSAND.block.BlockSnowSoft;
import com.SwingTheVine.QSAND.block.BlockTest;
import com.SwingTheVine.QSAND.block.ItemBlockMeta;
import com.SwingTheVine.QSAND.block.SinkingBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class QSAND_Blocks {
	public static Block test_block;
	
	
	// MFQM blocks that you can sink in
	public static SinkingBlock snowSoft;
	public static Block quicksandDry;
	public static Block morass;
	public static SinkingBlock quicksand;
	public static Block quicksandJungle;
	public static Block slimeSinking;
	public static Block mire;
	public static Block mireLiquid;
	public static Block mireLiquidStable;
	public static Block moor;
	public static Block claySinking;
	public static Block tar;
	public static Block quicksandCorrupted;
	public static Block woolSinking;
	public static Block quicksandSoft;
	public static Block webDense;
	public static Block swallowingFlesh;
	public static Block mucus;
	public static Block moss;
	public static Block clayBrown;
	public static Block peatWet;
	public static Block wax;
	public static Block larvae;
	public static Block chocolateLiquid;
	public static Block slurry;
	public static SinkingBlock mud;
	public static Block gravelSoft;
	public static Block honey;
	
	// MFQM blocks not included in the sink list
	//public static FluidBog fluidBog = new FluidBog();
	public static Block clayHardened;
	public static Block meat;
	public static Block meatHole;
	public static Block voidHole;
	/*public static Block[] blockList = {
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
			tar,
			snowSoft};*/
	
	// Block, Types Colors, Max Opacity, Last Opacity, Inc Opacity
	public static Object[] blockObjectList = {
			//bog,              4538917,  2000,  500,  50,
			null,             14611967, null,  null, null, // 1
			null,             16777215, null,  null, null, // 2
			morass,           2431764,  1000,  750,  100,
			quicksand,        6444596,  5000,  750,  100,
			quicksandJungle,  3089167,  5000,  750,  100,
			mireLiquid,       2431764,  500,   250,  50,
			mireLiquidStable, 2431764,  500,   250,  50,
			slimeSinking,     15007713, 500,   0,    50,
			mire,             2431764,  5000,  750,  100,
			moor,             1973277,  5000,  750,  100,
			claySinking,      10197137, 5000,  750,  100,
			tar,              1973277,  10000, 1000, 500,
			null,             16777215, null,  null, null, // 13
			null,             16777215, null,  null, null, // 14
			meatHole,         16777189, 250,   0,    25,
			null,             16777215, null,  null, null, // 16
			quicksandSoft,    6180923,  2000,  500,  100,
			webDense,         14803425, 3000,  0,    75,
			swallowingFlesh,  16777189, 250,   0,    25,
			mucus,            14013598, 500,   0,    50,
			voidHole,         16777189, 250,   0,    25,
			moss,             7041868,  1000,  0,    100,
			clayBrown,        10056782, 5000,  750,  100,
			null,             8554890,  null,  null, null, // 24
			peatWet,          2431764,  1000,  750,  100,
			wax,              12298319, 10000, 1000, 500,
			chocolateLiquid,  3806471,  7000,  1000, 500,
			slurry,           5720614,  2000,  750,  50,
			null,             2431764,  null,  null, null, // 29
			gravelSoft,       5389343,  5000,  750,  100,
			honey,            16758567, 600,   600,  50};
	// Test World Seed: 1637864495647481288
	// Entities fall at a rate of 0.076125 blocks
	
	public static void init() {
		
		// Constructs all blocks with their names and tab
		snowSoft = (SinkingBlock)new BlockSnowSoft(Material.ground).setUnlocalizedName("snow_soft").setCreativeTab(QSAND.QSANDTab);
		quicksand = (SinkingBlock)new BlockQuicksand(Material.ground).setUnlocalizedName("quicksand").setCreativeTab(QSAND.QSANDTab);
		larvae = (SinkingBlock)new BlockLarvae(Material.coral).setUnlocalizedName("larvae").setCreativeTab(QSAND.QSANDTab);
		mud = (SinkingBlock)new BlockMud(Material.ground).setUnlocalizedName("mud").setCreativeTab(QSAND.QSANDTab);
		test_block = new BlockTest(Material.ground).setUnlocalizedName("test_block").setCreativeTab(QSAND.QSANDTab);
	}
	
	public static void registerBlocks() {
		
		// Registers the blocks with the Game Registry
		GameRegistry.registerBlock(snowSoft, ItemBlockMeta.class, snowSoft.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(quicksand, ItemBlockMeta.class, quicksand.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(larvae, ItemBlockMeta.class, larvae.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(mud, ItemBlockMeta.class, mud.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(test_block, ItemBlockMeta.class, test_block.getUnlocalizedName().substring(5));
	}
	
	public static void registerRenders() {
		
		// Registers the inventory image. Block to render, number of metadata types, should one texture be used
		registerRenderInventory(snowSoft, ((BlockSnowSoft)snowSoft).getTypes(), ((BlockSnowSoft)snowSoft).getUseOneTexture());
		registerRenderInventory(quicksand, ((BlockQuicksand)quicksand).getTypes(), ((BlockQuicksand)quicksand).getUseOneTexture());
		registerRenderInventory(larvae, ((BlockLarvae)larvae).getTypes(), ((BlockLarvae)larvae).getUseOneTexture());
		registerRenderInventory(mud, ((BlockMud)mud).getTypes(), ((BlockMud)mud).getUseOneTexture());
		registerRenderInventory(test_block, ((BlockTest)test_block).getTypes(), ((BlockTest)test_block).getUseOneTexture());
	}
	
	// Registers the inventory image for all block variants
	public static void registerRenderInventory(Block block, String[] types, boolean useOneTexture) {
		
		Item item = Item.getItemFromBlock(block); // The item variant of the block
		OreDictionary.registerOre("quicksandBlock_" + block.getUnlocalizedName().substring(5), new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE)); // Registers the block to the ore dictionary for creative tab sorting
		
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
