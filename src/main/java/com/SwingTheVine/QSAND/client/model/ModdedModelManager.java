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

/** Registers the models for this mod
 * <p>
 * Big thanks to Choonster for having the only open source reference for custom fluids in 1.8.9!
 * The reference has comments to which is a first for 1.8.9 Minecraft mods <3
 * 
 * @since <b>0.52.0</b>
 * @author SwingTheVine - Additional comments
 * @author Choonster - 1.8.9 source code written here: <a href=
 * "https://github.com/Choonster-Minecraft-Mods/TestMod3/blob/1.8.9/src/main/java/com/choonster/testmod3/client/model/ModModelManager.java">
 * https://github.com/Choonster-Minecraft-Mods/TestMod3/blob/1.8.9/src/main/java/com/choonster/testmod3/client/model/
 * ModModelManager.java</a>
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
@SideOnly(Side.CLIENT)
public class ModdedModelManager {
	
	public static final ModdedModelManager INSTANCE = new ModdedModelManager();
	
	private static final String fluidPath = ModInfo.id + ":fluids";
	
	// Constructor
	private void registerModels() {
	
	}
	
	public void registerFluidModels() {
		
		QSAND_Fluids.modFluidBlocks.forEach(this::registerFluidModel);
	}
	
	private void registerFluidModel(final IFluidBlock fluidBlock) {
		
		final Item item = Item.getItemFromBlock((Block) fluidBlock);
		
		ModelBakery.registerItemVariants(item);
		
		final ModelResourceLocation modelResourceLocation = new ModelResourceLocation(fluidPath,
			fluidBlock.getFluid().getName());
			
		ModelLoader.setCustomMeshDefinition(item, MeshDefinitionFix.create(stack -> modelResourceLocation));
		
		ModelLoader.setCustomStateMapper((Block) fluidBlock, new StateMapperBase() {
			
			@Override
			protected ModelResourceLocation getModelResourceLocation(final IBlockState p_178132_1_) {
				
				return modelResourceLocation;
			}
		});
	}
}
