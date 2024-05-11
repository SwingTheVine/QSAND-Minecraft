package com.SwingTheVine.QSAND.blocks;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

// Acts as a go-between for the custom ItemBlock class and the blocks themselves.
// Functions defined here are called in the custom ItemBlock class.
// Functions defined here are overridden in the blocks
public interface IMetaBlockName {
	
	// What the name of that variant of the block is (e.g. "0")
	String getSpecialName(ItemStack stack);
	
	// Extends the tooltip function from the ItemBlock class to the blocks
	void setTooltip(final ItemStack item, final EntityPlayer player, final List list, final boolean bool);
}
