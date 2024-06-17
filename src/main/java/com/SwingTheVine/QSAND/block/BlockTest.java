package com.SwingTheVine.QSAND.block;

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

/** Implements Test block calculation and physics.
 * 
 * @since <b>0.3.0</b>
 * @author <b>SwingTheVine</b> - 1.8.9 source code
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class BlockTest extends SinkingBlock implements IMetaBlockName {
	
	// All the different special names of the metadata/subtype variants
	private static final String[] types = {"0", "1", "2", "3", "4"};
	
	private static final boolean useOneTexture = true; // Should all metadata variants use the same texture?
	
	// Creates a metadata value for every "types" metadata value
	public static final PropertyInteger SINK = PropertyInteger.create("sink", 0, 4);
	
	/** Constructor.
	 * <p>
	 * Constructs the Test block with the custom metadata and default state
	 * 
	 * @param material - The material of the block */
	public BlockTest(final Material material) {
		super(material); // Runs all the code in the super implementation of this constructor
		this.setDefaultState(this.blockState.getBaseState().withProperty(SINK, Integer.valueOf(0)));
	}
	
	/** What to do when an entity is INSIDE the block.
	 * <p>
	 * This is the core of the quicksand calculations. Most of (if not all) of the math for the calculations are in this
	 * function.
	 * 
	 * @param world - The world the block is in
	 * @param pos - The position of the block
	 * @param state - The state of the block
	 * @param triggeringEntity - The entity that triggered this function (and is colliding with the block)
	 * @see {@link Block#onEntityCollidedWithBlock(World, BlockPos, IBlockState, Entity) Super Implementation} */
	@Override
	public void onEntityCollidedWithBlock(final World world, final BlockPos pos, final IBlockState state,
		final Entity triggeringEntity) {
		
		// Prints out the name and metadata of the block to the terminal
		System.out.println("Collision detected. " + state.getBlock().toString() + " with metadata "
			+ state.getBlock().getBlockState());
	}
	
	/** Declares and defines the metadata/subtypes of this block.
	 * <p>
	 * <b>THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION</b>
	 * <br>
	 * The metadata/subtype value corresponds with its index in "types".
	 * 
	 * @param block - The block (in item form) to obtain, declare, and define the metadata/subtypes for
	 * @param tabs - The creative tab this block will be stored in
	 * @param subtypes - A list of all the declared subtypes for this block
	 * @see {@link Block#getSubBlocks(Item, CreativeTabs, List) Super Implementation} */
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(final Item block, final CreativeTabs tab, final List<ItemStack> subtypes) {
		
		// For every subtype defined in "types"...
		for (int indexType = 0; indexType < types.length; indexType++) {
			subtypes.add(new ItemStack(block, 1, indexType)); // Make a new item stack of that subtype and define it
		}
	}
	
	/** Obtains the block's metadata from the block's state
	 * <p>
	 * <b>THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION WITH METADATA</b>
	 * 
	 * @param state - The state of the block
	 * @return The metadata value as an integer
	 * @see {@link Block#getMetaFromState(IBlockState) Super Implementation} */
	@Override
	public int getMetaFromState(final IBlockState state) {
		
		return state.getValue(SINK).intValue(); // Retrieves and returns the metadata value from the block state
	}
	
	/** Obtains the block's state from the block's metadata.
	 * <p>
	 * <b> THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION WITH METADATA</b>
	 * 
	 * @param blockMetadata - The metadata value of the block
	 * @return The state of the block as an {@link IBlockState}
	 * @see {@link Block#getStateFromMeta(int) Super Implementation} */
	@Override
	public IBlockState getStateFromMeta(final int blockMetadata) {
		
		// Generates the default state of the block, changes the metadata, then returns the block state
		return getDefaultState().withProperty(SINK, Integer.valueOf(blockMetadata));
	}
	
	/** Obtains the block when the player picks it (using Middle Mouse Button)
	 * <p>
	 * When the block is picked, it carries over the metadata with it.
	 * 
	 * @param targetBlock - Information on the (multi-)block targeted by the player
	 * @param world - The world the block is in
	 * @param pos - The position of the block
	 * @param player - The player that triggered this function
	 * @return An item stack to add to the player's inventory. Return null to add nothing.
	 * @see {@link Block#getPickBlock(MovingObjectPosition, World, BlockPos, EntityPlayer) Super Implementation} */
	@Override
	public ItemStack getPickBlock(final MovingObjectPosition targetBlock, final World world, final BlockPos pos,
		final EntityPlayer player) {
		
		// Generates and returns an item stack of the targeted block with the metadata from the block position
		return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(world.getBlockState(pos)));
	}
	
	/** Creates a new block state for this block.
	 * <p>
	 * <b>THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION WITH METADATA</b>
	 * <br>
	 * The new block state includes information about the possible metadata values.
	 * 
	 * @return A block state with metadata */
	@Override
	protected BlockState createBlockState() {
		
		// Creates and returns a new block state that includes metadata values
		return new BlockState(this, new IProperty[] {SINK});
	}
	
	/** Obtains the metadata this block should have when dropped as an item.
	 * <p>
	 * <b>THIS FUNCTION IS NEEDED FOR BLOCK DECLARATION</b>
	 * 
	 * @param state - The block state to obtain metadata from
	 * @return The metadata of the block as an integer
	 * @see {@link Block#damageDropped(IBlockState) Super Implementation} */
	@Override
	public int damageDropped(final IBlockState state) {
		
		return this.getMetaFromState(state);
	}
	
	/** {@inheritDoc}
	 * 
	 * @see {@link ItemBlockMeta#getUnlocalizedName(ItemStack)} <br>
	 * {@link IMetaBlockName#getSpecialName(ItemStack) Super Implementation} */
	@Override
	public String getSpecialName(final ItemStack itemStack) {
		
		// Returns the special name of the block metadata/subtype stored in "types"
		// that matches the index of the damage value of the item stack
		return BlockTest.types[itemStack.getItemDamage()];
	}
	
	/** {@inheritDoc}
	 * 
	 * @see {@link IMetaBlockName#setTooltip(ItemStack, EntityPlayer, List, boolean) Super Implementation} */
	@Override
	public void setTooltip(final ItemStack item, final EntityPlayer player, final List list, final boolean bool) {
	
	}
	
	/** {@inheritDoc}
	 * 
	 * @see {@link SinkingBlock#getTypes() Super Implementation} */
	@Override
	public String[] getTypes() {
		
		return types; // Returns all possible metadata/subtype names
	}
	
	/** {@inheritDoc}
	 * 
	 * @see {@link SinkingBlock#getUseOneTexture() Super Implementation} */
	@Override
	public boolean getUseOneTexture() {
		
		return useOneTexture; // Returns if all metadata/subtypes should use one texture
	}
}
