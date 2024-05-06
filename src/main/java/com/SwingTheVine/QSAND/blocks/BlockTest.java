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
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTest extends Block implements IMetaBlockName {
	
	private static final String[] types = {"0", "1", "2", "3", "4"};
	private static final Boolean useOneTexture = true;
	public static final PropertyInteger SINK = PropertyInteger.create("sink", 0, 4); // Predefined steps of maximum sinking into the block

	public BlockTest(Material material) {
		super(material);
		this.setDefaultState(this.blockState.getBaseState().withProperty(SINK, Integer.valueOf(0)));
	}
	
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity triggeringEntity) {
		// Seed: 1637864495647481288
		//triggeringEntity.setInWeb();
		System.out.println("Collision detected. " + state.getBlock().toString() + " with metadata " + state.getBlock().getBlockState());
		
	}
	
	/*
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
		System.out.println(this.getStateFromMeta(meta).toString());
        return this.getStateFromMeta(meta);
    }*/
	
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item block, CreativeTabs creativeTabs, List<ItemStack> list) {
		for (int indexType = 0; indexType < types.length; indexType++) {
			list.add(new ItemStack(block, 1, indexType));
		}
    }
	
	/*
	public String getUnlocalizedName(final String baseName, final ItemStack itemStack) {
        return baseName + "." + BlockTest.types[itemStack.getItemDamage()];
    }*/
	
	@Override
	public int getMetaFromState(IBlockState state)
    {
		System.out.println(state.toString());
        return (Integer)state.getValue(SINK).intValue();
    }
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		System.out.println("Meta: " + meta);
		switch (meta) {
		case 0:
			return getDefaultState().withProperty(SINK, Integer.valueOf(0));
		case 1:
			return getDefaultState().withProperty(SINK, Integer.valueOf(1));
		case 2:
			return getDefaultState().withProperty(SINK, Integer.valueOf(2));
		case 3:
			return getDefaultState().withProperty(SINK, Integer.valueOf(3));
		case 4:
			return getDefaultState().withProperty(SINK, Integer.valueOf(4));
		default:
			return getDefaultState().withProperty(SINK, Integer.valueOf(0));
	}
		//return getDefaultState().withProperty(SINK, Integer.valueOf(meta));
    }
	
	/*
	public IBlockState getActualState(IBlockState state, IBlockAccess world, IBlockAccess pos) {
		switch (state.getValue(SINK)) {
			case 0:
				return state.withProperty(SINK, Integer.valueOf(0));
			case 1:
				return state.withProperty(SINK, Integer.valueOf(1));
			case 2:
				return state.withProperty(SINK, Integer.valueOf(2));
			case 3:
				return state.withProperty(SINK, Integer.valueOf(3));
			case 4:
				return state.withProperty(SINK, Integer.valueOf(4));
			default:
				return state.withProperty(SINK, Integer.valueOf(state.getValue(SINK)));
		}
	}*/
	
	@Override
	public String getSpecialName(ItemStack stack) {
		return BlockTest.types[stack.getItemDamage()];
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(world.getBlockState(pos)));
	}
	
	@Override
	protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {SINK});
    }
	
	@Override
	public int damageDropped(IBlockState state) {
		return this.getMetaFromState(state);
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
