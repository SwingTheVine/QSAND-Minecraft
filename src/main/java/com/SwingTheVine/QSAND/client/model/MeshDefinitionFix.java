package com.SwingTheVine.QSAND.client.model;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** A hackish adapter that allows lambdas to be used as {@link ItemMeshDefinition} implementations without breaking ForgeGradle's
 * reobfuscation and causing {@link AbstractMethodError}s.
 *
 * @since <b>0.52.0</b>
 * @author SwingTheVine - Additional comments
 * @author diesieben07 - 1.8.9 source code written in this thread:
 * <a href="http://www.minecraftforge.net/forum/index.php/topic,34034.0.html">http://www.minecraftforge.net/forum/index.php/topic,
 * 34034.0.html</a>
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
@SideOnly(Side.CLIENT)
interface MeshDefinitionFix extends ItemMeshDefinition {
	
	ModelResourceLocation getLocation(ItemStack stack);
	
	// Helper method to easily create lambda instances of this class
	static ItemMeshDefinition create(final MeshDefinitionFix lambda) {
		
		return lambda;
	}
	
	@Override
	default ModelResourceLocation getModelLocation(final ItemStack stack) {
		
		return getLocation(stack);
	}
}
