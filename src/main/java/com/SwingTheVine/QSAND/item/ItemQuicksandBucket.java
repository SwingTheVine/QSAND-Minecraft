package com.SwingTheVine.QSAND.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Implements a custom forge universal bucket for quicksand.
 * 
 * @since <b>0.53.0</b>
 * @author <b>SwingTheVine</b> - 1.8.9 source code
 * @see <a href=".@docroot/LICENSE.txt">License</a> */
public class ItemQuicksandBucket extends UniversalBucket {
	
	public final int capacity; // how much the bucket holds
	public final ItemStack empty; // empty item to return and recognize when filling
	public final boolean nbtSensitive;
	
	// Constructor
	public ItemQuicksandBucket() {
		
		// Creates a bucket by default if the user does not pass in any values
		this(FluidContainerRegistry.BUCKET_VOLUME, FluidContainerRegistry.EMPTY_BUCKET, false);
	}
	
	/** Constructor
	 * 
	 * @param capacity - The capacity of the container
	 * @param empty - The item to fill the bucket with. The item to place when emptying the bucket.
	 * The item is usually the (tile) item form of a fluid
	 * @param nbtSensitive - Whether the empty item is NBT sensitive (usually true if empty and full are the same items)
	 * @See {@link UniversalBucket#UniversalBucket(capacity, empty, nbtSensitive)
	 * Super Implementation} */
	public ItemQuicksandBucket(final int capacity, final ItemStack empty, final boolean nbtSensitive) {
		this.capacity = capacity;
		this.empty = empty;
		this.nbtSensitive = nbtSensitive;
		
		this.setMaxStackSize(1);
	}
	
	/** Returns a list of items with the same ID, but different meta (e.g. dye returns 16 items)
	 * 
	 * @param defaultBucket - The item to generate subtypes from
	 * @param tab - The tab the item belongs to
	 * @param subItems - A list of subtypes of this item that will be registered
	 * @See {@link UniversalBucket#getSubItems Super Implementation} */
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(final Item defaultBucket, final CreativeTabs tab, final List<ItemStack> subItems) {
		
		for (final Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
			// add all fluids that the bucket can be filled with
			final FluidStack fs = new FluidStack(fluid, capacity);
			final ItemStack stack = new ItemStack(this);
			if (fill(stack, fs, true) == fs.amount) {
				
				subItems.add(stack);
			}
		}
	}
	
	/** Sets the tooltip for the item.
	 * Leave this function empty for no tooltip
	 * 
	 * @param item - The item to place the tooltip on
	 * @param player - The player looking at the tooltip
	 * @param tooltip - A list of all pre-existing tooltips on this item
	 * @param inAdvancedMode - This is true if the player has toggled on detailed item descriptions (F3 + H)
	 * @See {@link Item#addInformation Super Implementation} */
	@Override
	public void addInformation(final ItemStack item, final EntityPlayer player, final List tooltip,
		final boolean inAdvancedMode) {
		
		String fluidName = item.getDisplayName(); // A fallback value if no fluid name is found
		
		if (item.getTagCompound().hasKey("FluidName")) {
			fluidName = item.getTagCompound().getString("FluidName");
		} else {
			System.out.printf(
				"An error occured trying to retrieve the display name for a quicksand bucket. Fallback name is %s. It seems the fluid's NBT data is missing. Currently the NBT data is: %s\n",
				StatCollector.translateToLocal("item.bucket.name") + " "
					+ StatCollector.translateToLocal("fluid." + fluidName),
				item.getTagCompound().toString());
		}
		
		tooltip.remove(0); // Removes the local name of the block
		tooltip.add(StatCollector.translateToLocal("item.bucket.name") + " "
			+ StatCollector.translateToLocal("fluid." + fluidName));
			
		if (item.getTagCompound().hasKey("FluidName")) {
			
			fluidName = item.getTagCompound().getString("FluidName");
			
			if (fluidName.equals("bog")) {
				tooltip.add(StatCollector.translateToLocal("mfqm.tooltip_1")); // Adds the MFQM faithful tooltip
			}
		}
	}
}
