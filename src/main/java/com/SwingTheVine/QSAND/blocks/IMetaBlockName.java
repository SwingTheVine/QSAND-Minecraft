package com.SwingTheVine.QSAND.blocks;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IMetaBlockName {
	
	String getSpecialName(ItemStack stack); // What the name of that variant of the block is (e.g. "0")
	
	void setTooltip(final ItemStack item, final EntityPlayer player, final List list, final boolean bool); // What tooltips (if any) should be added
}
