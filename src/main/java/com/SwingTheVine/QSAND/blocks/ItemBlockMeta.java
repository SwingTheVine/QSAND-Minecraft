package com.SwingTheVine.QSAND.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMeta extends ItemBlock {

	public ItemBlockMeta(Block block) {
        super(block);
        if (!(block instanceof IMetaBlockName)) {
            throw new IllegalArgumentException(String.format("The Block %s is not an instance of IMetaBlockName.", block.getUnlocalizedName()));
        }
        this.setHasSubtypes(true);
    }
	
	public int getMetadata(int damage){
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "_" + ((IMetaBlockName)this.block).getSpecialName(stack);
    }
    
    public void addInformation(final ItemStack item, final EntityPlayer player, final List list, final boolean bool) {
    	((IMetaBlockName)this.block).setTooltip(item, player, list, bool); // Extends this function to the block
    }

}
