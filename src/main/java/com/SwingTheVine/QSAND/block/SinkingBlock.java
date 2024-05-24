package com.SwingTheVine.QSAND.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SinkingBlock extends Block implements IMetaBlockName {

	// Constructor
	public SinkingBlock(Material materialIn) {
		super(materialIn);
	}

	// Special name of the block (e.g. "1")
	@Override
	public String getSpecialName(ItemStack stack) {
		return null;
	}

	// Flavor text shown during mouseover event
	@Override
	public void setTooltip(ItemStack item, EntityPlayer player, List list, boolean bool) {
		
	}
	
	// The color of the quicksand block
	@SideOnly(Side.CLIENT)
    public int getQuicksandBlockColor() {
        return 16777215;
    }
    
	// The render color of the quicksand block
    @SideOnly(Side.CLIENT)
    public int getQuicksandRenderColor(IBlockState state) {
        return 16777215;
    }
    
    
    // The color multiplier added onto the base color of the block
    @SideOnly(Side.CLIENT)
    public int getQuicksandColorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
        return 16777215;
    }
    
    // The color multiplier added onto the base color of the block
    @SideOnly(Side.CLIENT)
    public int getQuicksandColorMultiplier(IBlockAccess world, BlockPos pos) {
        return this.getQuicksandColorMultiplier(world, pos, 0);
    }
    
    // The color multiplier added onto the base color of the block
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
        return this.getQuicksandColorMultiplier(world, pos, renderPass);
    }
    
    // Returns types of metadata for the block
 	public String[] getTypes() {
 		return null;
 	}
 	
 	// Returns if only one texture should be used for all metadata types
 	public boolean getUseOneTexture() {
 		return true;
 	}
}
