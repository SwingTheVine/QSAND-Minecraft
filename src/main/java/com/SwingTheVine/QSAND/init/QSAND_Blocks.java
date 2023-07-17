package com.SwingTheVine.QSAND.init;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.QSAND;
import com.SwingTheVine.QSAND.blocks.BlockTest;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class QSAND_Blocks {
	public static Block test_block;
	
	public static void init() {
		test_block = new BlockTest(Material.cloth).setUnlocalizedName("test_block").setCreativeTab(QSAND.QSANDTab);
	}
	
	public static void registerBlocks() {
		GameRegistry.registerBlock(test_block, test_block.getUnlocalizedName().substring(5));
	}
	
	public static void registerRenders() {
		registerRenderInventory(test_block);
	}
	
	public static void registerRenderInventory(Block block) {
		Item item = Item.getItemFromBlock(block);
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(ModInfo.ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}
}
