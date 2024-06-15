package com.SwingTheVine.QSAND.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Creates a customizable interface for spawn eggs.
 * Register this as an item with metadata/subtypes
 * 
 * @author SwingTheVine - Updated Jabelar's code from 1.7.10 to 1.8.9
 * @author Jabelar - 1.7.10 <a href=
 * "https://web.archive.org/web/20190529171948/http://jabelarminecraft.blogspot.com/p/minecraft-forge-1721710-creating-custom.html">
 * source code</a> */
public class ItemSpawnEgg extends ItemMonsterPlacer {
	
	// Constructor
	public ItemSpawnEgg() {
		this.setHasSubtypes(true); // Enables this item to have subtypes/metadata
		this.setMaxStackSize(64); // Sets the max item stack size for this item
	}
	
	/** Returns a list of items with the same ID, but different meta (e.g. dye returns 16 items)
	 * 
	 * @param defaultEgg - The item to generate subtypes from
	 * @param tab - The tab the item belongs to
	 * @param subItems - A list of subtypes of this item that will be registered
	 * @See {@link ItemMonsterPlacer#getSubItems Super Implementation} */
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(final Item defaultEgg, final CreativeTabs tab, final List<ItemStack> subItems) {
		
		// For every registered egg...
		for (final String eggName : EntityRegistry.getEggs().keySet()) {
			
			// Creates a new instance of the default egg item
			final ItemStack itemStackEgg = new ItemStack(defaultEgg);
			final NBTTagCompound nbtTag = new NBTTagCompound(); // Creates a new NBT Tag
			
			// Adds the name of the mob to spawn (the name of the mob to spawn is derived from its egg which has the same name)
			nbtTag.setString("entity_name", eggName);
			
			// Adds the NBT Tag onto the default egg. This makes it a subtype/metadata variant of the default egg
			itemStackEgg.setTagCompound(nbtTag);
			
			// Adds the subtype/metadata variant of the default egg to the list of subtypes for this item
			subItems.add(itemStackEgg);
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
		
		tooltip.add(StatCollector.translateToLocal("mfqm.tooltip_1")); // Adds the MFQM faithful tooltip
	}
}
