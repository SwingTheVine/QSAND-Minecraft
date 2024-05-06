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
import net.minecraft.entity.EntityLivingBase;
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
	private static final String[] types = {"0", "1", "2", "3"}; // Values of the different metadata levels
	private static final Boolean useOneTexture = true; // Should all metadata variants use the same texture?
	private static final float[] sinkTypes = {0.35F, 0.50F, 0.75F, 1.00F}; // The maximum sink level for each metadata variant
	public static final PropertyInteger SINK = PropertyInteger.create("sink", 0, Integer.valueOf(types.length-1)); // Creates a metadata value for every "types" metadata value

	// Constructor
	public Mud(Material material) {
		super(material);
		this.setDefaultState(this.blockState.getBaseState().withProperty(SINK, Integer.valueOf(0))); // Default metadata values for the block
	}
	
	// Changes the collision box. This is not the texture bounding box. This is not the hitbox.
	// This function changes the height of the block to make the entity "sink" into the block by the "sinkType" value corresponding to the metadata value of the block. (i.e. meta=0 means reduce height by 0.35)
	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        return new AxisAlignedBB((double)pos.getX() + this.minX, (double)pos.getY() + this.minY, (double)pos.getZ() + this.minZ, (double)pos.getX() + this.maxX, (double)pos.getY() + this.maxY - sinkTypes[state.getValue(SINK).intValue()], (double)pos.getZ() + this.maxZ);
    }
	
	// What to do when an entity is INSIDE the block
	// This is the core of the quicksand calculations
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity triggeringEntity) {
		//triggeringEntity.setInWeb();
		final double triggEntityPosY = triggeringEntity.posY; // Triggering entity's Y position
		final double triggEntityPrevPosY = triggeringEntity.prevPosY; // Triggering entity's previous Y position
		final double triggEntityVelY = triggeringEntity.motionY; // Triggering entity's Y velocity
		double triggEntitySunk, triggEntitySunk_kof1 = (pos.getY() - triggEntityPosY + 1.0) * -1.0; // How far into the block the entity has sunk (in percent) relative to the top of the block (i.e. sunk 15% from the top of the block is -0.15)
		double triggEntityPrevSunk, triggEntityPrevSunk_kof2 = Math.max(((pos.getY() - triggEntityPrevPosY + 1.0) * -1.0), 0.0); // How far into the block the entity previously sunk (in percent) to the top of the block
		double triggEntitySunkMod_kof1m = Math.max(triggEntitySunk_kof1, 0.0); // A modified version of trigger entity sunk
		final int blockMetadata = state.getValue(SINK).intValue(); // Obtains this block's variant/metadata
		
		if (triggeringEntity instanceof EntityLivingBase) {
			Boolean triggEntityAffected = true; // Should the triggering entity be affected by this block?
			Boolean triggEntityJumping = false; // Is the triggering entity jumping?
			Boolean triggEntityMoving = false; // Is the triggering entity moving?
			Boolean triggEntitySplashing = false; // Is the triggering entity splashing?
			Boolean triggEntityRotating = false; // Is the triggering entity rotating?
			final float blockMetadataBumped = (float)(blockMetadata + 1); // Adds 1 to the block's metadata value
			
			// These variables are unknown
			double triggEntityMovingDistance_movDis = 1.0;
			double triggEntityMovingCoefficient_movCof = 16.0;
			double triggEntityMovingKoefficientDivider_mofKofDiv = 1.0;
			final int mr_blackgoo = (int)Math.min(5.0 + Math.floor(Math.max(0.0, Math.pow(blockMetadataBumped * 2.0f * (1.5 - triggEntitySunkMod_kof1m), 2.0))), 145.0);
			
			// If the entity moves downwards with a velocity higher than the equation,
			//    the entity is marked as splashing
			triggEntitySplashing =
					(triggeringEntity.motionY < -0.1 / Math.max(1.0f, blockMetadataBumped / 2.0f))
					? true : false;
			
			// If the entity moves upwards with a velocity higher than the equation,
			//    the entity is marked as jumping
			triggEntityJumping = 
					(triggEntityPosY - triggEntityPrevPosY > 0.2)
					? true : false;
			
			// TODO: Complete the second equation.
			// If the entity is NOT a player, AND the entity has moved their camera along the Yaw axis...
			// OR if the entity is a player, and this is multiplayer, AND the server instance has detected Yaw axis movement...
			triggEntityRotating = 
					((!(triggeringEntity instanceof EntityPlayer) && ((EntityLivingBase)triggeringEntity).prevRotationYaw != ((EntityLivingBase)triggeringEntity).rotationYaw)
							|| (false))
					? true : false;
			
			// If the entity is marked as rotating, OR the entity has moved farther than 0.001 blocks along the X/Z axis...
			if (triggEntityRotating || Math.abs(triggeringEntity.prevPosX - triggeringEntity.posX) > 0.001 || Math.abs(triggeringEntity.prevPosZ - triggeringEntity.posZ) > 0.001) {
				
				triggEntityMoving = true; // The entity is moving
				
				// Finds the hypotenuse of the distance traveled.
				// This is the actual distance traveled on a radical plane
				triggEntityMovingDistance_movDis = Math.pow(Math.pow(triggeringEntity.prevPosX - triggeringEntity.posX, 2.0) + Math.pow(triggeringEntity.prevPosZ - triggeringEntity.posZ,  2.0), 0.5);
				
				// Unknown. However, it outputs a parabola
				triggEntityMovingCoefficient_movCof = Math.max(Math.min(32.0 / (1.0 + (triggEntityMovingDistance_movDis * 10.0)), 32.0), 16.0);
				
				// Unknown.
				triggEntityMovingKoefficientDivider_mofKofDiv = 1.0 + triggEntityMovingDistance_movDis * 25.0;
				
				// If the distance the entity has sunk into the block (relative to the top of the block) is less than 0.9,
				//    AND the distance the entity has sunk is not 0.0,
				//    AND the entity is marked as rotating...
				if (triggEntitySunkMod_kof1m < 0.9 && triggEntitySunkMod_kof1m != 0.0 && triggEntityRotating) {
					triggEntityMovingKoefficientDivider_mofKofDiv += mr_blackgoo * 0.005;
				}
			}
		}
	}
	
	// Declares that this block ID has metadata values.
	// Declares the metadata values for this block.
	// The metadata value corrosponds with its index in "types".
	// THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item block, CreativeTabs creativeTabs, List<ItemStack> list) {
		
		// For every index in "types"...
		for (int indexType = 0; indexType < types.length; indexType++) {
			list.add(new ItemStack(block, 1, indexType)); // Declare a metadata variant with that index value
		}
    }
	
	// Obtains the block's metadata from the block's blockstate.
	// THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION
	@Override
	public int getMetaFromState(IBlockState state) {
        return (Integer)state.getValue(SINK).intValue();
    }
	
	// Obtains the block's blockstate from the block's metadata.
	// THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(SINK, Integer.valueOf(meta));
    }
	
	// Obtains the special name of the block variant.
	// This is used to add a custom name to a block variant in the language file
	@Override
	public String getSpecialName(ItemStack stack) {
		return Mud.types[stack.getItemDamage()];
	}
	
	// Obtains the block (with metadata) when the player picks it (using Middle Mouse Button)
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(world.getBlockState(pos)));
	}
	
	// Creates a new block state for this block ID.
	// THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION
	@Override
	protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] {SINK});
    }
	
	// Obtains the metadata this block should drop.
	// THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION
	@Override
	public int damageDropped(IBlockState state) {
		return this.getMetaFromState(state);
	}
	
	// Obtains if the specific side of the block is solid.
	// The default (0) and Thinnish (1) variants of this block are solid.
	// The Deep (2) and Bottomless (3) vaariants of this block are NOT solid
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
