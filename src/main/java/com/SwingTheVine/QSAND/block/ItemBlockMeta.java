package com.SwingTheVine.QSAND.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMeta extends ItemBlock {
	
	public ItemBlockMeta(final Block block) {
		super(block);
		if (!(block instanceof IMetaBlockName)) {
			throw new IllegalArgumentException(
				String.format("The Block %s is not an instance of IMetaBlockName.", block.getUnlocalizedName()));
		}
		this.setHasSubtypes(true);
	}
	
	@Override
	public int getMetadata(final int damage) {
		
		return damage;
	}
	
	@Override
	public String getUnlocalizedName(final ItemStack stack) {
		
		if (((IMetaBlockName) this.block).getSpecialName(stack) == null) { // Blocks with no metadata are null
			return super.getUnlocalizedName(stack);
		}
		
		return super.getUnlocalizedName(stack) + "_" + ((IMetaBlockName) this.block).getSpecialName(stack);
	}
	
	@Override
	public void addInformation(final ItemStack item, final EntityPlayer player, final List list, final boolean bool) {
		
		((IMetaBlockName) this.block).setTooltip(item, player, list, bool); // Extends this function to the block
	}
	
}
