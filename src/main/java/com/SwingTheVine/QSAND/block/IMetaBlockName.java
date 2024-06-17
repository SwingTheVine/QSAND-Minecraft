package com.SwingTheVine.QSAND.block;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** Acts as a go-between for the {@link ItemBlockMeta custom ItemBlock class} and the blocks themselves.
 * <p>
 * Functions defined here are called in the custom ItemBlock class.
 * Functions defined here are overridden in the blocks that implement this class.
 * 
 * @since <b>0.3.0</b>
 * @author <b>CJMinecraft</b> - 1.8.9 source code
 * @see {@link ItemBlockMeta}
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public interface IMetaBlockName {
	
	/** Obtains the special name of the subtype/metadata value of the block. (e.g. "0")
	 * 
	 * @param itemStack - The item stack to retrieve the special name from
	 * @return The special name of the item metadata/subtype */
	String getSpecialName(ItemStack itemStack);
	
	/** Extends the tooltip function from the ItemBlock class to the blocks.
	 * <p>
	 * Leave this blank to return no tooltip.
	 * <p>
	 * The first tooltip (which always secretly exists unless you remove it) is the display name of the block <i>after
	 * localization</i>. If you clear/modify the first tooltip entry, it will clear/modify the display name of the block.
	 * For example, if the block is named "Bucket" you can modify the first tooltip entry to add the special name to "Bucket"
	 * based on the metadata value. Assuming the special name is "%%%", if you overwrite the first tooltip as "Bucket of %%%",
	 * display name will be "Bucket of %%%" even though the name of the block is "Bucket". In addition, the display name will
	 * change based on the metadata value.
	 * 
	 * @param item - The item to put a tooltip on
	 * @param player - The player currently triggering/reading the tooltip
	 * @param tooltips - The list of tooltips to be added
	 * @param inAdvanced - Is the player in advanced tooltips mode? */
	void setTooltip(final ItemStack item, final EntityPlayer player, final List tooltips, final boolean inAdvanced);
}
