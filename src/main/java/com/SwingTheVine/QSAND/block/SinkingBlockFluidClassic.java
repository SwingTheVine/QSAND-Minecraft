package com.SwingTheVine.QSAND.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SinkingBlockFluidClassic extends BlockFluidClassic{
	
	// Constructor
	public SinkingBlockFluidClassic(Fluid fluid, Material material) {
		super(fluid, material);
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
