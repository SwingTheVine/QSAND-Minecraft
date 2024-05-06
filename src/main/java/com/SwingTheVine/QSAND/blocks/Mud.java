package com.SwingTheVine.QSAND.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Mud extends Block implements IMetaBlockName {
	private static final String[] types = {"0", "1", "2", "3"};
	private static final Boolean useOneTexture = true;
	private static final float[] sinkTypes = {0.35F, 0.50F, 0.75F, 1.00F};
	public static final PropertyInteger SINK = PropertyInteger.create("sink", 0, Integer.valueOf(types.length-1)); // Predefined steps of maximum sinking into the block

	public Mud(Material material) {
		super(material);
		this.setDefaultState(this.blockState.getBaseState().withProperty(SINK, Integer.valueOf(0)));
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        return new AxisAlignedBB((double)pos.getX() + this.minX, (double)pos.getY() + this.minY, (double)pos.getZ() + this.minZ, (double)pos.getX() + this.maxX, (double)pos.getY() + this.maxY - sinkTypes[state.getValue(SINK).intValue()], (double)pos.getZ() + this.maxZ);
    }
	
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity triggeringEntity) {
		//triggeringEntity.setInWeb();
		
	}
	
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item block, CreativeTabs creativeTabs, List<ItemStack> list) {
		for (int indexType = 0; indexType < types.length; indexType++) {
			list.add(new ItemStack(block, 1, indexType));
		}
    }
	
	@Override
	public int getMetaFromState(IBlockState state) {
		System.out.println(state.toString());
        return (Integer)state.getValue(SINK).intValue();
    }
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(SINK, Integer.valueOf(meta));
    }
	
	@Override
	public String getSpecialName(ItemStack stack) {
		return Mud.types[stack.getItemDamage()];
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(world.getBlockState(pos)));
	}
	
	@Override
	protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] {SINK});
    }
	
	@Override
	public int damageDropped(IBlockState state) {
		return this.getMetaFromState(state);
	}
	
	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		final int metadata = this.getMetaFromState(world.getBlockState(pos));
		return metadata <= 1;
	}
	
	// Used to determine ambient occlusion and culling when rebuilding chunks for render
	@Override
	public boolean isOpaqueCube() {
        return true;
    }

	// When false, the block will not push entities outside of itself
	@Override
	public boolean isFullCube() {
		return false;
    }
	
	// Returns types of metadata for the block
	public String[] getTypes() {
		return types;
	}
	
	// Returns if only one texture should be used for all metadata types
	public Boolean getUseOneTexture() {
		return useOneTexture;
	}
}
