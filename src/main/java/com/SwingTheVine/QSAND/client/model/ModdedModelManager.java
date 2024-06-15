package com.SwingTheVine.QSAND.client.model;

import com.SwingTheVine.QSAND.ModInfo;
import com.SwingTheVine.QSAND.init.QSAND_Fluids;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// Big thanks to Choonster for having the only open source reference for custom fluids in 1.8.9!
// It has comments too <3
// Choonster: https://forums.minecraftforge.net/profile/70776-choonster/
// Source: https://github.com/Choonster-Minecraft-Mods/TestMod3/blob/1.8.9/src/main/java/com/choonster/testmod3/client/model/ModModelManager.java
@SideOnly(Side.CLIENT)
public class ModdedModelManager {
	
	public static final ModdedModelManager INSTANCE = new ModdedModelManager();
	
	private static final String fluidPath = ModInfo.id + ":fluids";
	
	// Constructor
	private void registerModels() {}
	
	public void registerFluidModels() {
		QSAND_Fluids.modFluidBlocks.forEach(this::registerFluidModel);
	}
	
	private void registerFluidModel(IFluidBlock fluidBlock) {
		Item item = Item.getItemFromBlock((Block) fluidBlock);

		ModelBakery.registerItemVariants(item);

		ModelResourceLocation modelResourceLocation = new ModelResourceLocation(fluidPath, fluidBlock.getFluid().getName());
		
		ModelLoader.setCustomMeshDefinition(item, MeshDefinitionFix.create(stack -> modelResourceLocation));

		ModelLoader.setCustomStateMapper((Block) fluidBlock, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState p_178132_1_) {
				return modelResourceLocation;
			}
		});
	}
}
