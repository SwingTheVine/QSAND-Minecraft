package com.SwingTheVine.QSAND.fluid;

import java.util.List;

import com.SwingTheVine.QSAND.block.IMetaBlockName;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

/** Implements Test fluid block calculation and physics.
 * 
 * @since <b>0.54.0</b>
 * @author <b>SwingTheVine</b> - 1.8.9 source code
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class FluidTest extends BlockFluidClassic implements IMetaBlockName {
	
	public FluidTest(final Fluid fluid, final Material material) {
		super(fluid, material);
	}
	
	@Override
	public String getSpecialName(final ItemStack stack) {
		
		return null;
	}
	
	@Override
	public void setTooltip(final ItemStack item, final EntityPlayer player, final List list, final boolean bool) {
	
	}
	
}
