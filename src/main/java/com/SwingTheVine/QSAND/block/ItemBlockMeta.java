package com.SwingTheVine.QSAND.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/** Obtains information based on a block's metadata value.
 * 
 * @since <b>0.3.0</b>
 * @author <b>CJMinecraft</b> - 1.8.9 source code
 * @see {@link IMetaBlockName}
 * @see <a href="@docroot/LICENSE.txt">License</a> */
public class ItemBlockMeta extends ItemBlock {
	
	/** Constructor
	 * 
	 * @param block - The block to obtain metadata from */
	public ItemBlockMeta(final Block block) {
		
		super(block); // Runs all the code in the super implementation of this constructor
		
		// If the block is NOT an instance of IMetaBlockName...
		if (!(block instanceof IMetaBlockName)) {
			
			// Throw a new Illegal Argument Exception
			throw new IllegalArgumentException(
				String.format("The Block %s is not an instance of IMetaBlockName.", block.getUnlocalizedName()));
		}
		
		this.setHasSubtypes(true); // Declare that this block item has subtypes
	}
	
	/** Obtains the block's metadata value.
	 * <p>
	 * This is derived from the damage value of the item that places the block.
	 * 
	 * @param damage - The damage value of the item
	 * @return The metadata value of the block
	 * @see {@link Item#getMetadata(int) Super Implementation} */
	@Override
	public int getMetadata(final int damage) {
		
		return damage; // Returns the damage value of the item (which directly equals the block's metadata)
	}
	
	/** Obtains the block's unlocalized name from its item form.
	 * <p>
	 * The block's unlocalized name looks something like "tile.mud_2" or where
	 * "tile" is the item type,
	 * "mud" is the item name,
	 * and "0" is the special name of the item's subtype/metadata variant
	 * 
	 * @param itemStack - The item to obtain the unlocalized name from
	 * @return The unlocalized name of the item stack's subtype/metadata variant (if any)
	 * @see {@link ItemBlock#getUnlocalizedName(ItemStack) Super Implementation} */
	@Override
	public String getUnlocalizedName(final ItemStack itemStack) {
		
		// If the block version of the item stack's special subtype/metadata name is equal to null...
		if (((IMetaBlockName) this.block).getSpecialName(itemStack) == null) {
			// (First it retrives the item block)
			// (Then, it casts the block to IMetaBlockName)
			// (Finally, it attempts to retrive the special name of the item stack)
			// If this if statement runs, then the block has no metadata/subtypes.
			
			return super.getUnlocalizedName(itemStack); // Returns the default name as there are no subtypes
		}
		
		// The item has metadata/subtype. Return the default name appended with the special name of the metadata/subtype
		return super.getUnlocalizedName(itemStack) + "_" + ((IMetaBlockName) this.block).getSpecialName(itemStack);
	}
	
	/** Extends the tooltip function from the ItemBlock class to the blocks
	 * 
	 * @param item - The item to put a tooltip on
	 * @param player - The player currently triggering/reading the tooltip
	 * @param tooltips - The list of tooltips to be added
	 * @param inAdvanced - Is the player in advanced tooltips mode?
	 * @see {@link Item#addInformation(ItemStack, EntityPlayer, List, boolean) Super Implementation} */
	@Override
	public void addInformation(final ItemStack item, final EntityPlayer player, final List tooltips,
		final boolean inAdvanced) {
		
		// Retrives the item block
		// Then, casts the block to IMetaBlockName
		// Finally, it sets the tooltip of the item block
		((IMetaBlockName) this.block).setTooltip(item, player, tooltips, inAdvanced);
	}
	
}
