package com.SwingTheVine.QSAND.block;

import java.util.List;

import com.SwingTheVine.QSAND.util.ColorManager;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Extends the {@link BlockFluidClassic} class whilst implementing {@link IMetaBlockName} to gain access to tooltips.
 * <p>
 * This grants blocks extending this class special attributes.
 * Namely, the block's color, metadata name, and tooltip.
 * 
 * @since <b>0.49.0</b>
 * @author <b>SwingTheVine</b> - 1.8.9 source code
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class SinkingBlockFluidClassic extends BlockFluidClassic implements IMetaBlockName {
	
	/** Constructor
	 * 
	 * @param fluid - The fluid type of this fluid block
	 * @param material - The material of this block */
	public SinkingBlockFluidClassic(final Fluid fluid, final Material material) {
		super(fluid, material); // Runs all of the code in the super implementation of this constructor
	}
	
	/** {@inheritDoc} */
	@Override
	public String getSpecialName(final ItemStack stack) {
		
		return null; // Since this function is supposed to be overridden uniquely in every block, we return null
	}
	
	/** {@inheritDoc} */
	@Override
	public void setTooltip(final ItemStack item, final EntityPlayer player, final List list, final boolean bool) {
	
	}
	
	/** The color of the quicksand fluid block
	 * 
	 * @return RGB value in integer form
	 * @see {@link ColorManager} for converting the RGB integer into human readable formats */
	@SideOnly(Side.CLIENT)
	public int getQuicksandBlockColor() {
		
		return 16777215; // By default, the color is 16777215
	}
	
	/** The render color of the quicksand fluid block
	 * 
	 * @param state - The current state of the fluid block. Used for returning different colors for each metadata/subtype of the
	 * fluid block
	 * @return RGB value in integer form
	 * @see {@link ColorManager} for converting the RGB integer into human readable formats */
	@SideOnly(Side.CLIENT)
	public int getQuicksandRenderColor(final IBlockState state) {
		
		return 16777215; // By default, the color is 16777215
	}
	
	
	/** The color to modify the base quicksand fluid block color by.
	 * <p>
	 * "Tints" the fluid block color.
	 * 
	 * @param world - The world the fluid block is in
	 * @param pos - The coordinates of the fluid block
	 * @param renderPass - TODO: Figure out what this is and why 0 is the default value
	 * @return RGB value in integer form
	 * @see {@link ColorManager} for converting the RGB integer into human readable formats */
	@SideOnly(Side.CLIENT)
	public int getQuicksandColorMultiplier(final IBlockAccess world, final BlockPos pos, final int renderPass) {
		
		return 16777215; // By default, no color "tint" is 16777215
	}
	
	/** The color to modify the base quicksand fluid block color by.
	 * <p>
	 * "Tints" the fluid block color.
	 * 
	 * @param world - The world the fluid block is in
	 * @param pos - The coordinates of the fluid block
	 * @return RGB value in integer form
	 * @see {@link ColorManager} for converting the RGB integer into human readable formats */
	@SideOnly(Side.CLIENT)
	public int getQuicksandColorMultiplier(final IBlockAccess world, final BlockPos pos) {
		
		// Finds and returns the color multiplier with a render pass of 0 (since none was provided)
		return this.getQuicksandColorMultiplier(world, pos, 0); // By default, no color "tint" is 16777215
	}
	
	/** The original color multiplier called by Minecraft.
	 * <p>
	 * This functions calls the {@link SinkingBlockFluidClassic#getQuicksandBlockColor() getQuicksandBlockColor} function.
	 * 
	 * @param world - The world the fluid block is in
	 * @param pos - The coordinates of the fluid block
	 * @param renderPass - TODO: Figure out what this is and why 0 is the default value
	 * @return RGB value in integer form
	 * @see {@link ColorManager} for converting the RGB integer into human readable formats */
	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(final IBlockAccess world, final BlockPos pos, final int renderPass) {
		
		// Finds and returns the color multiplier with a render pass
		return this.getQuicksandColorMultiplier(world, pos, renderPass); // By default, no color "tint" is 16777215
	}
	
	/** Obtains a String array of all the special names of the quicksand fluid block.
	 * <p>
	 * Not to be confused with the {@link SinkingBlockFluidClassic#getSpecialName(ItemStack) getSpecialName} function
	 * which returns only a single metadata/subtype special name.
	 * 
	 * @return All possible metadata/subtype special names of the quicksand fluid block
	 * @see {@link SinkingBlockFluidClassic#getSpecialName(ItemStack) getSpecialName(ItemStack)} which returns only one
	 * metadata/subtype
	 * special name */
	public String[] getTypes() {
		
		return null; // Since this function is supposed to be overridden uniquely in every fluid block, we return null
	}
	
	/** Should all metadata/subtypes of the quicksand block use the same texture?
	 * 
	 * @return True if they all use the same texture. False if not */
	public boolean getUseOneTexture() {
		
		// If there is one texture and multiple metadata/subtype variants, it functions properly when true.
		// If there is one texture and multiple metadata/subtype variants, it crashes when false.
		return true; // Therefore, this is true by default to avoid the risk of a crash
	}
}
